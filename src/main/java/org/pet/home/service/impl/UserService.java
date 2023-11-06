package org.pet.home.service.impl;

import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.CodeResBean;
import org.pet.home.entity.Employee;
import org.pet.home.service.IUserService;
import org.pet.home.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
@Service
public class UserService implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private EmployeeService employeeService;

    private RedisServiceImpl redisService;

    @Autowired
    public UserService(EmployeeService employeeService, RedisServiceImpl redisService) {
        this.employeeService = employeeService;
        this.redisService = redisService;
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
        //这里我们用员工表查询一下 该号码是否已经被注册
        Employee employee = employeeService.checkPhone(phone);
        if (employee != null) {
            //如果对应的号码已注册
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPATION, ErrorMessage.PHONE_OCCUPATION);
        }
        //如果这个号码没有被注册 设置它的当前进来时间
        Long lastSendTime = 0L;
        try {
            lastSendTime = Long.parseLong(redisService.getValue(phone));
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
            redisService.cacheValue(K,code,60);
            CodeResBean<String> codeResBean = new CodeResBean<>();
            codeResBean.v = code;
            return ResultGenerator.genSuccessResult(codeResBean);
        }else {
            return ResultGenerator.genSuccessResult(K);
        }
    }

}
