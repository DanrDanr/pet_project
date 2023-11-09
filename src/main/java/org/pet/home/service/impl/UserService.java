package org.pet.home.service.impl;

import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.CodeResBean;
import org.pet.home.entity.User;
import org.pet.home.mapper.UserMapper;
import org.pet.home.service.IUserService;
import org.pet.home.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
@Service
public class UserService implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private UserMapper userMapper;

    private RedisTemplate redisTemplate;

    private RedisServiceImpl redisService;

    @Autowired
    public UserService(UserMapper userMapper, RedisTemplate redisTemplate,RedisServiceImpl redisService) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
    }

    @Override
    public int add(User user) {
        return userMapper.add(user);
    }

    @Override
    public User checkPhone(String phone) {
        return userMapper.checkPhone(phone);
    }

    @Override
    public User userLogin(String phone, String password) {
        return userMapper.login(phone, password);
    }

    /**
     * 需要验证的情况有2种
     * 可能是注册 可能是登录
     *
     * @param phone
     * @return
     */
    @Override
    public NetResult sendRegisterCode(String phone) {//注册验证码
        /**
         * 排除手机号是空的状态
         */
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        /**
         * 排除手机号格式不正确
         */
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }

        //排除完输入手机号异常后 我们要看一下它是否已经注册
        //这里我们用用户表查询一下 该号码是否已经被注册
        User user = userMapper.checkPhone(phone);
        if (user != null) {
            //如果对应的号码已注册
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPATION, ErrorMessage.PHONE_OCCUPATION);
        }
        //如果这个号码没有被注册 设置它的当前进来时间
        Long lastSendTime = 0L;
        try {
            lastSendTime = Long.parseLong(this.redisService.getValue(phone));
        }catch (Exception e){
            logger.error(e.getMessage());
            lastSendTime = 0L;
            redisService.cacheValue(phone, System.currentTimeMillis()+"",60);
        }
        //查看是否是在1分钟以内 避免多次重复拉取
        if(System.currentTimeMillis()-lastSendTime<60*1000){
            return ResultGenerator.genErrorResult(NetCode.OPERATE_ERROR,ErrorMessage.OPERATE_ERROR);
        }

        String K = redisService.getValue(phone+phone);
        if(StringUtil.isNullOrNullStr(K)){
            String code = "159753_"+System.currentTimeMillis();
            redisService.cacheValue(phone+phone,code,60);
            CodeResBean<String> codeResBean = new CodeResBean<>();
            codeResBean.v = code;
            return ResultGenerator.genSuccessResult(codeResBean);
        }else {
            return ResultGenerator.genSuccessResult(K);
        }
    }

    /**
     * 用户登陆不需要code
     * @param userParam
     * @return
     */

    public NetResult login(UserParam userParam) {
        //排除号码和密码为空的状态
        if (StringUtil.isEmpty(userParam.phone)){
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL,ErrorMessage.USERNAME_NULL);
        }
        if (StringUtil.isEmpty(userParam.password)){
            return ResultGenerator.genErrorResult(NetCode.USER_PASSWORD_NULL,ErrorMessage.USER_PASSWORD_NULL);
        }
        String phone = userParam.getPhone();
        String password = MD5Util.MD5Encode(userParam.getPassword(),"utf-8");
        User u = userMapper.login(phone,password);
        if(u!=null){//如果获取的值不为空即代表账号密码正确
            //通过UUID的唯一特性用它为K 保存用户v 设置保存时间
            //每次登陆都会重新跟更新
            String token = UUID.randomUUID().toString();
            logger.info("token->"+token);
            redisTemplate.opsForValue().set(token,u.toString(),30, TimeUnit.MINUTES);
            u.setToken(token);
            u.setPassword(null);
            return ResultGenerator.genSuccessResult(u);
        }
        return ResultGenerator.genFailResult("账号或密码错误");
    }

}
