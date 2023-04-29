package com.ljh.project.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ljh.ljhcommon.model.entity.User;
import com.ljh.project.common.ErrorCode;
import com.ljh.project.exception.BusinessException;
import com.ljh.project.model.vo.UserVO;
import com.ljh.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ljh.project.constant.UserConstant.*;

/**
 * 刷新token
 *
 */

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request == null){
            return true;
        }

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){

            return true;
        }

        // 1.获取请求头中的token
        String token = request.getHeader("x-auth-token");
        if (StrUtil.isBlank(token)) {
            return true;
        }

        User user = (User) userObj;
        String userToken = SALT + token + user.getId();
        String tokenKey = LOGIN_USER_KEY + userToken;

        String userId = stringRedisTemplate.opsForValue().get(tokenKey);


        if ( StringUtils.isEmpty(userId)){
            return true;
        }


        UserHolder.saveUser(userId);
        //刷新token
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);


        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
