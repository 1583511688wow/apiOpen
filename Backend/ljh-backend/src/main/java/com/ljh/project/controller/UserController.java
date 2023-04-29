package com.ljh.project.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.ljh.ljhclientsdk.client.LjhClient;
import com.ljh.ljhcommon.model.entity.User;
import com.ljh.ljhcommon.model.entity.UserInterfaceInfo;
import com.ljh.project.common.*;
import com.ljh.project.constant.UserConstant;
import com.ljh.project.exception.BusinessException;
import com.ljh.project.mapper.UserInterfaceInfoMapper;
import com.ljh.project.model.dto.user.*;
import com.ljh.project.model.dto.userinterfaceinfo.BuyInterfaceRequest;
import com.ljh.project.model.vo.UserVO;
import com.ljh.project.service.UserInterfaceInfoService;
import com.ljh.project.service.UserService;
import com.ljh.project.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ljh.project.constant.UserConstant.*;
import static com.ljh.project.constant.UserInterface.USER_INTERFACE_FOLLOW;
import static com.ljh.project.constant.UserInterface.USER_INTERFACE_NOTFOLLOW;

/**
 * 用户接口
 *
 * @author ljh
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;


    //默认没从未订阅为 false
    private boolean FLAG = false;


    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userPhone = userRegisterRequest.getUserPhone();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userPhone)) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能为空！");

        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }




        return userService.userLogin(userAccount, userPassword, request);
    }








    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {


        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     *
     * 获取登入Token
     */



    @GetMapping("/token")
    public String getToken(HttpServletRequest request){
        Object token = request.getSession().getAttribute("token");
        String  userToken = String.valueOf(token);

        return userToken;

    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }











//
//    @GetMapping("/login/captcha ")
//    public BaseResponse<String> sendCode( PhoneRequest phoneRequest,HttpServletRequest request){
//
//        if (phoneRequest == null || request == null){
//
//            new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        //查询是否存在用户
//        String userPhone = phoneRequest.getUserPhone();
//        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
//
//
//
//    }


    @PostMapping("/login/phone ")
    public BaseResponse<UserVO> codeLogin( @RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request){


        return userService.codeLogin(userLoginRequest, request);
    }


    @GetMapping("/send/code")
    public BaseResponse<String> sendCode(PhoneRequest phoneRequest, HttpServletRequest request){

        if (phoneRequest == null || request == null){

            new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userPhone = phoneRequest.getUserPhone();

        //开头数字必须为1，第二位必须为3至9之间的数字，后九尾必须为0至9组织成的十一位电话号码
        if (!ReUtil.isMatch(UserConstant.MOBILE_REGEX, userPhone)) {

            new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误！");
        }

              // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        //验证码保存在redis 并设置失效时间
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + userPhone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

         System.out.println(code);

        return ResultUtils.success(code);
    }


    /**
     *
     * 试用接口
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/follow")
    public BaseResponse<Integer> followInterface(IdRequest idRequest, HttpServletRequest request ){



        if (idRequest == null || request == null){

          throw new BusinessException(ErrorCode.PARAMS_ERROR, "此接口不存在！");
        }

        Long interfaceInfoId = idRequest.getId();
        String user= UserHolder.getUser();
        long userId = Long.parseLong(user);
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("interfaceInfoId", interfaceInfoId);
        Long selectCount = userInterfaceInfoMapper.selectCount(queryWrapper);

        if (selectCount <= 0){

            UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setLeftNum(INTERFACE_COUNT);
            userInterfaceInfo.setFollow(USER_INTERFACE_FOLLOW);
            userInterfaceInfoService.save(userInterfaceInfo);

            return ResultUtils.success(USER_INTERFACE_FOLLOW);

        }


        UpdateWrapper<UserInterfaceInfo> wrapper = new UpdateWrapper<>();
        wrapper.set("follow", USER_INTERFACE_FOLLOW);
        wrapper.eq("userId", userId).eq("interfaceInfoId", interfaceInfoId);

        userInterfaceInfoService.update(wrapper);



        return ResultUtils.success(USER_INTERFACE_FOLLOW);
    }


    /**
     * 测试接口
     * @param userId
     * @param interfaceInfoId
     * @param request
     * @return
     */
    @GetMapping("/get/follow")
    public BaseResponse<Integer> getFollow(Long userId, Long interfaceInfoId, HttpServletRequest request){

        if (userId ==0 || interfaceInfoId == 0){


            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int follow = userInterfaceInfoMapper.selectFollow(userId, interfaceInfoId);
        if (follow == USER_INTERFACE_FOLLOW){


            return ResultUtils.success(USER_INTERFACE_FOLLOW);

        }

        return ResultUtils.success(USER_INTERFACE_NOTFOLLOW);


    }


    /**
     * 购买接口
     * @param buyInterfaceRequest
     * @param request
     * @return
     */

    @PostMapping("/buy/interface")
    public BaseResponse<String> buyInterface(BuyInterfaceRequest buyInterfaceRequest, HttpServletRequest request){

        if (buyInterfaceRequest == null || request == null){

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "此接口不存在！");
        }

        Long interfaceInfoId = buyInterfaceRequest.getId();

        String user= UserHolder.getUser();
        long userId = Long.parseLong(user);

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("interfaceInfoId", interfaceInfoId);
        Long selectCount = userInterfaceInfoMapper.selectCount(queryWrapper);

        if (selectCount <= 0){

            UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setLeftNum(INTERFACE_COUNT);
            userInterfaceInfo.setFollow(USER_INTERFACE_FOLLOW);

            userInterfaceInfoService.save(userInterfaceInfo);
            return ResultUtils.success("购买成功嘞！！！！");

        }






        int anInterface = userInterfaceInfoMapper.buyInterface(userId, interfaceInfoId, INTERFACE_COUNT);

        if (anInterface < 0 ){

            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "购买失败喽！！！");

        }

        return ResultUtils.success("购买成功嘞 " + INTERFACE_COUNT + "次!!!!!");

    }


    /**
     *
     * 下载sdk
     * @param response
     * @throws FileNotFoundException
     */
    @GetMapping("/downLoad")
    public BaseResponse<Boolean> downLoad(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        // 下载本地文件
        String fileName = "ljh-client-sdk-0.0.1-SNAPSHOT.jar".toString(); // 文件的默认保存名
        // 读到流中
        InputStream inStream = new FileInputStream("D:\\资料\\java\\mysdk\\ljh-client-sdk-0.0.1-SNAPSHOT.jar");// 文件的存放路径
        // 设置输出的格式
        response.reset();
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.addHeader(  "Access-Control-Allow-Origin","http://localhost:8000");//允许所有来源访同
        response.addHeader(  "Access-Control-Allow-Method","POST,GET");//允许访问的方式


        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len = 0;
        try {
            while (true) {

                try {
                    if (!((len = inStream.read(b)) > 0)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.getOutputStream().write(b, 0, len);
            }
            inStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("cczcz");

        return ResultUtils.success(true);
    }



}
