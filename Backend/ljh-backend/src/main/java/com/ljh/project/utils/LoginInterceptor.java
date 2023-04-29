package com.ljh.project.utils;

import com.ljh.project.common.ErrorCode;
import com.ljh.project.exception.BusinessException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.判断是否需要拦截（ThreadLocal中是否有用户）
        if (UserHolder.getUser() == null) {

            new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 有用户，则放行
        return true;
    }



}
