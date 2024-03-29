package org.pet.home.config;

import org.pet.home.common.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/7
 **/
@Configuration
public class LoginConfig implements WebMvcConfigurer {
    private RedisTemplate redisTemplate;

    @Autowired
    public LoginConfig(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new UserLoginInterceptor(redisTemplate));
        registration.addPathPatterns("/**"); //所有路径都被拦截
        registration.excludePathPatterns(    //添加不拦截路径
                "/addPetTask",
                "/shop/register",
                "/smsCode",
                "/userOrEmployeeLogin",      //登录路径
                "/employee/login",           //employee登陆路径
                "/sendCode",                 //发送验证码路径
                "/register",                 //注册路径
                "/userLogin",                //用户登陆
                "/**/*.html",                //html静态资源
                "/**/*.js",                  //js静态资源
                "/**/*.css"                  //css静态资源
        );
    }
}
