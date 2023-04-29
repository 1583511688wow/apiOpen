package com.ljh.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ljh.ljhcommon.model.entity.User;
import com.ljh.project.common.BaseResponse;
import com.ljh.project.common.PhoneRequest;
import com.ljh.project.model.dto.user.UserLoginRequest;
import com.ljh.project.model.dto.user.UserQueryRequest;
import com.ljh.project.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author ljh
 */
public interface UserService extends IService<User> {



    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userPhone);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    BaseResponse<UserVO> userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     *
     * 短信登入
     * @param userLoginRequest
     * @param request
     * @return
     */

    BaseResponse<UserVO> codeLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

}
