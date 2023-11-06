package org.pet.home.controller;

import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.CodeResBean;
import org.pet.home.service.RedisService;
import org.pet.home.service.impl.UserService;
import org.pet.home.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
@RestController
public class LoginController {

    private StringRedisTemplate redisTemplate;
    private  RedisService redisService;

    private UserService userService;

    @Autowired
    public LoginController(StringRedisTemplate redisTemplate,RedisService redisService,UserService userService){
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
        this.userService = userService;
    }

    @GetMapping("/getCaptcha")
    public NetResult GetVerificationCode(@RequestParam String phone){
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

        /**
         * 如果这个号码进来 设置它的当前进来时间
         */
        String lastSendTime = this.redisTemplate.opsForValue().get(phone);
        this.redisTemplate.opsForValue().set(phone, String.valueOf(System.currentTimeMillis()));
        /**
         * 如果这个号码是是第一次进来 那它最后一次进来的时间就是0
         */
        if(lastSendTime == null) {
            lastSendTime = "";
        }else {
            Long lastSendTimeStr = Long.parseLong(lastSendTime);
            /**
             * 小于1分钟 不用发验证嘛
             */
            if(System.currentTimeMillis()-lastSendTimeStr< 60 * 1000){//1*60*1000

                return ResultGenerator.genErrorResult(NetCode.OPERATE_ERROR, ErrorMessage.OPERATE_ERROR);
            }
        }
        /**
         * 大于1分钟重新发验证码
         */
        String value = redisTemplate.opsForValue().get(phone+phone);
        if(StringUtil.isNullOrNullStr(value)){
            //过期了要重新设置
            String code = "159753_"+System.currentTimeMillis();
            //保存code
            redisTemplate.opsForValue().set(phone+phone,code,60, TimeUnit.SECONDS);
            //把code保存到date里
            CodeResBean<String> codeResBean = new CodeResBean<>();
            codeResBean.v = code;
            return ResultGenerator.genSuccessResult(codeResBean);
        }else {
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPANCY, ErrorMessage.PHONE_OCCUPANCY);
        }
    }

    @GetMapping("/getVerifyCode")
    public NetResult getVerifyCode(@RequestParam String phone){
        return userService.sendRegisterCode(phone);
    }

    @GetMapping("/verifyCode")
    public NetResult verifyCode(@RequestParam String phone, @RequestParam String code) {
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }
        //获取号码验证码
        String value = String.valueOf(redisTemplate.opsForValue().get(phone + phone));
        if (StringUtil.isNullOrNullStr(value)) {
            //如果验证码是null的
            return ResultGenerator.genFailResult("验证码过期");
        } else {
            //对比该号码的验证码和前台的输入 看是否一致
            if (value.equals(code)) {
                return ResultGenerator.genSuccessResult("验证码正常");
            } else {
                return ResultGenerator.genFailResult("验证码不存在");
            }
        }
    }

}
