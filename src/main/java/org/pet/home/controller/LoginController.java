package org.pet.home.controller;

import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.CodeResBean;
import org.pet.home.entity.User;
import org.pet.home.service.RedisService;
import org.pet.home.service.impl.UserService;
import org.pet.home.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
@RestController
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String USER_SEND_CODE_URL = "/sendCode";
    private static final String USER_GET_VERIFY_CODE_URL = "/getVerifyCode";
    private static final String USER_VERIFY_CODE_URL = "/verifyCode";
    private static final String USER_LOGIN_URL = "/login";

    private RedisTemplate redisTemplate;
    private  RedisService redisService;
    private UserService userService;

    private GetCode getCode;

    @Autowired
    public LoginController(StringRedisTemplate redisTemplate,RedisService redisService,UserService userService,GetCode getCode){
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
        this.userService = userService;
        this.getCode=getCode;
    }

    /**
     *
     * @param phone
     * @return
     */
    @GetMapping(USER_SEND_CODE_URL)
    public NetResult SendCode(@RequestParam String phone){
        /**
         * 排除手机号是空的状态
         */
        if (StringUtil.isEmpty(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.USER_PASSWORD_NULL);
        }
        /**
         * 排除手机号格式不正确
         */
        if (!RegexUtil.isPhoneValid(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }

        // 尝试从缓存中获取验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(phone);
        if (!StringUtil.isEmpty(cachedCode)) {
            // 验证码未过期，无需重新生成
            CodeResBean<String> codeResBean = new CodeResBean<>();
            codeResBean.v = "还是原来的验证码"+cachedCode;
            return ResultGenerator.genSuccessResult(codeResBean);
        }

        // 生成新的验证码
        String newCode = getCode.sendCode();

        // 将新的验证码存入缓存，设置过期时间为60秒
        redisTemplate.opsForValue().set(phone, newCode, 60, TimeUnit.SECONDS);

        // 返回新生成的验证码
        CodeResBean<String> codeResBean = new CodeResBean<>();
        codeResBean.v = newCode;
        return ResultGenerator.genSuccessResult(codeResBean);
    }

    @GetMapping(USER_GET_VERIFY_CODE_URL)
    public NetResult getVerifyCode(@RequestParam String phone){
        return userService.sendRegisterCode(phone);
    }

    @GetMapping(USER_VERIFY_CODE_URL)
    public NetResult verifyCode(@RequestParam String phone, @RequestParam String code) {
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }
        //获取号码验证码
        String K = redisService.getValue(phone+phone);
        if (StringUtil.isNullOrNullStr(K)) {
            //如果验证码是null的
            return ResultGenerator.genFailResult("验证码过期");
        } else {
            //对比该号码的验证码和前台的输入 看是否一致
            if (K.equals(code)) {
                return ResultGenerator.genSuccessResult("验证码正常");
            } else {
                return ResultGenerator.genFailResult("验证码不存在");
            }
        }
    }

    @PostMapping(USER_LOGIN_URL)
    public NetResult Login(@RequestBody UserParam userParam){
        return userService.login(userParam);
    }

}
