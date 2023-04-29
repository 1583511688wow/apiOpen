package com.ljh.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljh.ljhcommon.model.entity.User;
import com.ljh.project.common.BaseResponse;
import com.ljh.project.common.ErrorCode;
import com.ljh.project.common.PhoneRequest;
import com.ljh.project.common.ResultUtils;
import com.ljh.project.constant.UserConstant;
import com.ljh.project.exception.BusinessException;
import com.ljh.project.mapper.UserMapper;
import com.ljh.project.model.dto.user.UserLoginRequest;
import com.ljh.project.model.vo.UserVO;
import com.ljh.project.service.UserService;
import com.ljh.project.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ljh.project.constant.UserConstant.*;


/**
 * 用户服务实现类
 *
 * @author ljh
 */
@Service
@Slf4j
public class  UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;



    private static final String USERNAME_PREFIX = "Wow_";

    @Override
    public BaseResponse<UserVO> codeLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null || request == null){

            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //手机号校验
        String userPhone = userLoginRequest.getUserPhone();
        //开头数字必须为1，第二位必须为3至9之间的数字，后九尾必须为0至9组织成的十一位电话号码
        String mobileRegEx = "^1[3,4,5,6,7,8,9][0-9]{9}$";
        if (!ReUtil.isMatch(mobileRegEx, userPhone)) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误！");
        }

        // 3.从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + userPhone);
        String code = userLoginRequest.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 不一致，报错
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误！");

        }

        // 4.一致，根据手机号查询用户 select * from tb_user where phone = ?
        User user = query().eq("userPhone", userPhone).one();
        if (user == null){

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在！");
        }

        Long userId = user.getId();
        String id = String.valueOf(userId);

        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, user);

        System.out.println(session.getId());

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 生成token,作为口令令牌
        String token = UUID.randomUUID().toString(true);

        //生成tokenKey 保存在redis
        String userToken = SALT + token + userVO.getId();



        String tokenKey = LOGIN_USER_KEY + userToken;
        session.setAttribute("token", tokenKey);


        //用户信息保存在redis
        stringRedisTemplate.opsForValue().set(tokenKey, id);
        //设置过期时间
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);


        userVO.setUserToken(token);




        return ResultUtils.success(userVO);
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userPhone) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        //校验手机号
        //开头数字必须为1，第二位必须为3至9之间的数字，后九尾必须为0至9组织成的十一位电话号码
        if (!ReUtil.isMatch(MOBILE_REGEX, userPhone)) {

            new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误！");
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();

            queryWrapper.eq("userAccount", userAccount);
            queryWrapper1.eq("userPhone", userPhone);
            long count = userMapper.selectCount(queryWrapper);
            Long count1 = userMapper.selectCount(queryWrapper1);
            if (count > 0 ) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被注册！");
            }
            if ( count1 > 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号已被注册！");

            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

            String accessKey = DigestUtils.md5DigestAsHex((SALT + userAccount + RandomUtil.randomNumbers(5)).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + userAccount + RandomUtil.randomNumbers(8)).getBytes());


            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserPhone(userPhone);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAvatar(USER_DEFAULT_AVATAR);
            user.setUserName(USERNAME_PREFIX +  RandomUtil.randomNumbers(4) + userAccount);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }



            return user.getId();
        }
    }

    @Override
    public BaseResponse<UserVO> userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        Long userId = user.getId();
        String id = String.valueOf(userId);

        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, user);

        System.out.println(session.getId());

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 生成token,作为口令令牌
        String token = UUID.randomUUID().toString(true);

        //生成tokenKey 保存在redis
        String userToken = SALT + token + userVO.getId();



        String tokenKey = LOGIN_USER_KEY + userToken;
        session.setAttribute("token", tokenKey);


        //用户信息保存在redis
        stringRedisTemplate.opsForValue().set(tokenKey, id);
        //设置过期时间
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);


        userVO.setUserToken(token);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);


        }
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 1.获取请求头中的token
        String token = request.getHeader("x-auth-token");
        if (StrUtil.isBlank(token)) {

            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String userToken = SALT + token + currentUser.getId();
        String tokenKey = LOGIN_USER_KEY + userToken;

        String userId = stringRedisTemplate.opsForValue().get(tokenKey);
        if ( StringUtils.isEmpty(userId)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        request.getSession().setAttribute(USER_LOGIN_STATE, currentUser);
        return currentUser;
    }







    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        try {
            if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
            }

            HttpSession session = request.getSession();
            // 移除登录态
            session.removeAttribute(USER_LOGIN_STATE);
            Object token = session.getAttribute("token");
            String  userToken = String.valueOf(token);
            stringRedisTemplate.delete(userToken);
            session.removeAttribute("token");
        } catch (BusinessException e) {
            e.printStackTrace();
        } finally {
            UserHolder.removeUser();
        }

        return true;
    }

}




