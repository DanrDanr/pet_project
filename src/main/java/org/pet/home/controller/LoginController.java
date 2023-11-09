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
import java.util.UUID;
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
    private static final String LOGIN_URL = "/login";
    private static final String USER_REGISTER_URL = "/register";
    private static final String USER_LOGIN_URL = "/userLogin";

    private RedisTemplate redisTemplate;
    private RedisService redisService;
    private UserService userService;

    private GetCode getCode;

    @Autowired
    public LoginController(StringRedisTemplate redisTemplate, RedisService redisService, UserService userService, GetCode getCode) {
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
        this.userService = userService;
        this.getCode = getCode;
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @GetMapping(USER_SEND_CODE_URL)
    public NetResult SendCode(@RequestParam String phone) {
        /**
         * 排除手机号是空的状态
         */
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.USER_PASSWORD_NULL);
        }
        /**
         * 排除手机号格式不正确
         */
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }

        // 尝试从缓存中获取验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(phone);
        if (!StringUtil.isEmpty(cachedCode)) {
            // 验证码未过期，无需重新生成
            CodeResBean< String > codeResBean = new CodeResBean<>();
            codeResBean.msg = "还是原来的验证码";
            codeResBean.v = cachedCode;
            return ResultGenerator.genSuccessResult(codeResBean);
        }

        // 生成新的验证码
        String newCode = getCode.sendCode();

        // 将新的验证码存入缓存，设置过期时间为60秒
        redisTemplate.opsForValue().set(phone, newCode, 60, TimeUnit.SECONDS);

        // 返回新生成的验证码
        CodeResBean< String > codeResBean = new CodeResBean<>();
        codeResBean.v = newCode;
        codeResBean.msg = "发送验证码";
        return ResultGenerator.genSuccessResult(codeResBean);
    }

    /**
     * 用户注册
     *
     * @return
     */
    @GetMapping(USER_REGISTER_URL)
    public NetResult register(@RequestBody RegisterAndLoginParam registerAndLoginParam) {
        User user = registerAndLoginParam.user;
        String code = registerAndLoginParam.code;
        // 排除用户名为空的状态
        if (StringUtil.isEmpty(user.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL, ErrorMessage.USERNAME_NULL);
        }
        // 排除密码为空的状态
        if (StringUtil.isEmpty(user.getPassword())) {
            return ResultGenerator.genErrorResult(NetCode.USER_PASSWORD_NULL, ErrorMessage.USER_PASSWORD_NULL);
        }
        // 排除用户邮箱为空的状态
        if (StringUtil.isEmpty(user.getEmail())) {
            return ResultGenerator.genErrorResult(NetCode.EMAIL_NULL, ErrorMessage.EMAIL_NULL);
        }
        // 排除电话未空的状态
        if (StringUtil.isEmpty(user.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        // 排除手机格式不正确
        if (!RegexUtil.isPhoneValid(user.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }
        User u = userService.checkPhone(user.getPhone());
        if (u != null) {
            // 排除手机号码已注册的状态
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPATION, ErrorMessage.PHONE_OCCUPATION);
        }

        // 尝试从缓存中获取验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(user.getPhone());
        if (!StringUtil.isEmpty(cachedCode)) {
            if (code.equals(cachedCode)) {
                String password= MD5Util.MD5Encode(user.getPassword(), "utf-8");
                user.setPassword(password);
                userService.add(user);
                user.setPassword(null);
                return ResultGenerator.genSuccessResult(user);
            } else {
                return ResultGenerator.genErrorResult(NetCode.CODE_ERROR, ErrorMessage.CODE_ERROR);
            }
        } else {
            return ResultGenerator.genErrorResult(NetCode.CODE_LAPSE, ErrorMessage.CODE_LAPSE);
        }

    }

    /**
     * 用户登陆
     *
     * @param userParam
     * @return
     */
    @PostMapping(USER_LOGIN_URL)
    public NetResult UserLogin(@RequestBody UserParam userParam) {
        //排除号码为账号为空的情况
        if (StringUtil.isEmpty(userParam.phone)) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL, ErrorMessage.USERNAME_NULL);
        }
        //排除密码为null的状态
        if (StringUtil.isEmpty(userParam.password)) {
            return ResultGenerator.genErrorResult(NetCode.USER_PASSWORD_NULL, ErrorMessage.USER_PASSWORD_NULL);
        }
        //排除验证码为空的状态
        if (StringUtil.isEmpty(userParam.code)) {
            return ResultGenerator.genErrorResult(NetCode.CODE_NULL, ErrorMessage.CODE_NULL);
        }
        // 排除手机格式不正确
        if (!RegexUtil.isPhoneValid(userParam.phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }
        // 尝试从缓存中获取验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(userParam.phone);
        if (!StringUtil.isEmpty(cachedCode)) {
            if (userParam.code.equals(cachedCode)) {
                String phone = userParam.getPhone();
                String password = MD5Util.MD5Encode(userParam.getPassword(), "utf-8");
                User u = userService.userLogin(phone, password);
                if (u != null) {//如果获取的值不为空即代表账号密码正确
                    //通过UUID的唯一特性用它为K 保存用户v 设置保存时间
                    //每次登陆都会重新跟更新
                    String token = UUID.randomUUID().toString();
                    logger.info("token->" + token);
                    redisTemplate.opsForValue().set(token, u.toString(), 30, TimeUnit.MINUTES);
                    u.setToken(token);
                    u.setPassword(null);
                    return ResultGenerator.genSuccessResult(u);
                }
                return ResultGenerator.genFailResult("账号或密码错误");
            } else {
                return ResultGenerator.genErrorResult(NetCode.CODE_ERROR, ErrorMessage.CODE_ERROR);
            }
        } else {
            return ResultGenerator.genErrorResult(NetCode.CODE_LAPSE, ErrorMessage.CODE_LAPSE);
        }

    }

    @GetMapping(USER_GET_VERIFY_CODE_URL)
    public NetResult getVerifyCode(@RequestParam String phone) {
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
        String K = redisService.getValue(phone + phone);
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

    @PostMapping(LOGIN_URL)
    public NetResult Login(@RequestBody UserParam userParam) {
        return userService.login(userParam);
    }

}
