package com.ljh.project.config;

import com.ljh.project.utils.LoginInterceptor;
import com.ljh.project.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Mvc配置
 *
 * @author ljh
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {



    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     *全局跨域处理
     * @param registry
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // 覆盖所有请求
//        registry.addMapping("/**")
//                // 允许发送 Cookie
//                .allowCredentials(true)
//                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .exposedHeaders("*");
//
//
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:8000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true)
                    .maxAge(3600);
        }





    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/user/get/login",
                        "/user/login/phone",
                        "/user/send/code",
                        "/interfaceInfo/list/page"

                ).order(1);

        /**
         * 刷新token
         */
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
               .addPathPatterns("/**").order(0);

   }
}
