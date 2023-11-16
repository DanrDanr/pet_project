package org.pet.home.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.*;
import org.pet.home.service.IEmployeeService;
import org.pet.home.service.RedisService;
import org.pet.home.service.impl.*;
import org.pet.home.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
@RestController
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String USER_GET_VERIFY_CODE_URL = "/getVerifyCode";
    private static final String USER_VERIFY_CODE_URL = "/verifyCode";
    private static final String LOGIN_URL = "/login";
    private static final String USER_REGISTER_URL = "/register";
    private static final String USER_LOGIN_URL = "/userOrEmployeeLogin";
    private static final String SMS_SEND_CODE_URL = "/smsCode";
    private static final String USER_ADD_TASK = "/addPetTask";
    private static final String USER_PET_LIST = "/petList";
    private static final String USER_SHOP_LIST = "/shopList";
    private static final String USER_SHOP_BABY = "/shopBaby";
    private static final String USER_CHECK_BABY= "/babyCheck";
    private static final String USER_PET_ADOPT= "/petAdopt";
    private static final String USER_ADOPT_LIST= "/adoptList";

    private RedisTemplate redisTemplate;
    private RedisService redisService;
    private UserService userService;
    private GetCode getCode;
    private IEmployeeService iEmployeeService;
    private ShopService shopService;

    private PetCategoryService petCategoryService;
    private PetFindMasterService petFindMasterService;

    private PetCommodityService petCommodityService;

    @Autowired
    public UserController(StringRedisTemplate redisTemplate, RedisService redisService, UserService userService,
                          GetCode getCode, IEmployeeService iEmployeeService, ShopService shopService,
                          PetCategoryService petCategoryService, PetFindMasterService petFindMasterService,
                          PetCommodityService petCommodityService) {
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
        this.userService = userService;
        this.getCode = getCode;
        this.iEmployeeService = iEmployeeService;
        this.shopService = shopService;
        this.petCategoryService = petCategoryService;
        this.petFindMasterService = petFindMasterService;
        this.petCommodityService = petCommodityService;
    }

    /**
     * 短信发送验证码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    @GetMapping(SMS_SEND_CODE_URL)
    public NetResult SMSSendCode(@RequestParam String phone) throws Exception {
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
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "GET";
        String appcode = "dd31c4a2f9014af5b66dd61889cfcfb0";
        Map< String, String > headers = new HashMap< String, String >();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map< String, String > querys = new HashMap< String, String >();
        Map< String, String > bodys = new HashMap< String, String >();
        String code = getCode.sendCode();
        bodys.put("content", "code:" + code);
        bodys.put("template_id", "CST_ptdie100");
        bodys.put("phone_number", phone);
        logger.info(code);
        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            try (InputStream inputStream = entity.getContent()) {
                result = convertStreamToString(inputStream); // 将输入流转换为字符串
                logger.info(result);
                // 将新的验证码存入缓存，设置过期时间为60秒
                redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
                return ResultGenerator.genSuccessResult(Result.fromJsonString(result));
            } catch (IOException e) {
                // 处理异常
            }
        }
        return ResultGenerator.genFailResult("发送验证码失败！");
    }

    //处理流异常的状态
    private String convertStreamToString(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            // 处理异常
            return null;
        }
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
        if (StringUtil.isEmpty(code)) {
            return ResultGenerator.genErrorResult(NetCode.CODE_NULL, ErrorMessage.CODE_NULL);
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
                String password = MD5Util.MD5Encode(user.getPassword(), "utf-8");
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
    public NetResult UserLogin(@RequestBody UserParam userParam) throws JsonProcessingException {
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
                if (userParam.role == 0) {//用户登陆
                    User u = userService.userLogin(phone, password);
                    if (u != null) {//如果获取的值不为空即代表账号密码正确
                        //通过UUID的唯一特性用它为K 保存用户v 设置保存时间
                        //每次登陆都会重新跟更新
                        String token = UUID.randomUUID().toString();
                        logger.info("token->" + token);
                        redisTemplate.opsForValue().set(token, u.toString(), 180, TimeUnit.MINUTES);
                        u.setToken(token);
                        u.setPassword(null);
                        return ResultGenerator.genSuccessResult(u);

                    }
                    return ResultGenerator.genFailResult("账号或密码错误");
                } else if (userParam.role == 1) {//商铺管理员登陆
                    Employee e = iEmployeeService.login(phone, password);
                    if (e != null) {//如果获取的值不为空即代表账号密码正确
                        //通过UUID的唯一特性用它为K 保存用户v 设置保存时间
                        //每次登陆都会重新跟更新
                        String token = UUID.randomUUID().toString();
                        logger.info("token->" + token);
                        redisTemplate.opsForValue().set(token, e.toString(), 30, TimeUnit.MINUTES);
                        e.setToken(token);
                        e.setPassword(null);
                        return ResultGenerator.genSuccessResult(e);
                    }
                    return ResultGenerator.genFailResult("账号或密码错误");
                }

            } else {
                return ResultGenerator.genErrorResult(NetCode.CODE_ERROR, ErrorMessage.CODE_ERROR);
            }
        }
        return ResultGenerator.genErrorResult(NetCode.CODE_LAPSE, ErrorMessage.CODE_LAPSE);
    }

    /**
     * 用户添加寻主任务
     *
     * @param
     * @return
     */
    @PostMapping(USER_ADD_TASK)
    public NetResult AddPetFindMaster(@RequestBody PetFindMaster petFindMaster, HttpServletRequest request) throws Exception {
        //通过token获取user的信息
        String token = request.getHeader("token");
        String userString = (String) redisTemplate.opsForValue().get(token);
        // 通过字符串处理获取用户的 id
        int startIndex = userString.indexOf("id=") + 3; // 获取 id 的起始位置
        int endIndex = userString.indexOf(",", startIndex); // 获取 id 的结束位置
        String idString = userString.substring(startIndex, endIndex); // 提取 id 的字符串表示
        Long userId = Long.parseLong(idString); // 将 id 字符串转换为 Long 类型
        logger.info(userString);
        logger.info(String.valueOf(userId));

        // 排除宠物名为空的状态
        if (StringUtil.isEmpty(petFindMaster.getPetName())) {
            return ResultGenerator.genErrorResult(NetCode.PET_NAME_NULL, ErrorMessage.PET_NAME_NULL);
        }
        if (!StringUtil.state(petFindMaster.getSex())) {
            return ResultGenerator.genErrorResult(NetCode.PET_SEX_INVALID, ErrorMessage.PET_SEX_INVALID);
        }
        if (petFindMaster.getBirth() == 0) {
            return ResultGenerator.genErrorResult(NetCode.PET_BIRTH_INVALID, ErrorMessage.PET_BIRTH_INVALID);
        }
        if (!StringUtil.state(petFindMaster.getIsInoculation())) {
            return ResultGenerator.genErrorResult(NetCode.PET_IS_INOCULATION_INVALID, ErrorMessage.PET_IS_INOCULATION_INVALID);
        }
        if (petFindMaster.getPetCategory_id() == 0) {
            return ResultGenerator.genErrorResult(NetCode.PET_CATEGORY_INVALID, ErrorMessage.PET_CATEGORY_INVALID);
        }
        if (StringUtil.isEmpty(petFindMaster.getAddress())) {
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_NULL, ErrorMessage.ADDRESS_NULL);
        }

        //排除所以输入异常状态后我们看一下所有shop的地址
        List< Shop > shops = shopService.list();
        Location UserLocation = GaoDeMapUtil.getLngAndLag(petFindMaster.getAddress());
        List< Location > locations = new LinkedList<>();
        for (int i = 0; i < shops.size(); i++) {
            locations.add(i, GaoDeMapUtil.getLngAndLag(shops.get(i).getAddress()));
        }
        //获得最近的地址
        Location near = AddressDistanceComparator.findNearestAddress(UserLocation, locations);
        //根据地址确定要绑定的shop
        Shop shop = shopService.findByAddress(near.getAddress());
        if (shop == null) {
            return ResultGenerator.genFailResult("附近没有匹配的店铺");
        }
        petFindMaster.setShop_id(shop.getId());
        //根据店铺获取要绑定的shop_admin的账号
        Employee admin = iEmployeeService.findById(shop.getAdmin_id());
        petFindMaster.setEmployee_id(admin.getId());
        //根据宠物类型id绑定宠物类型id
        PetCategory petCategory = petCategoryService.findById(petFindMaster.getPetCategory_id());
        petFindMaster.setPetCategory(petCategory);
        //绑定的user
        User user = userService.findById(userId);
        petFindMaster.setUser_id(user.getId());
        //添加时间
        petFindMaster.setCreateTime(System.currentTimeMillis());
        //然后可以添加寻主任务
        logger.info(admin.getId().toString());
        int count = petFindMasterService.add(shop, admin, petCategory, user, petFindMaster);
        if (count != 1) {
            return ResultGenerator.genFailResult("添加失败");
        }
        //添加寻主任务成功 发短信给商家
        sendSmsShop(admin.getPhone(), user.getUsername(), user.getPhone());
        return ResultGenerator.genSuccessResult(petFindMaster);
    }

    /**
     * 用户通知店铺的短信
     *
     * @param phone
     * @param name
     */
    private SmsMsg sendSmsShop(String phone, String name, String userPhone) throws Exception {
        String host = "https://gyyyx1.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "25948b3da7cd41699b37c71c2a70070c";
        Map< String, String > headers = new HashMap< String, String >();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map< String, String > querys = new HashMap< String, String >();
        querys.put("mobile", phone);
        querys.put("templateId", "066285a885974689ab3f78e127a5cc06");
        querys.put("smsSignId", "1596868d15704706bee87cca32639de7");
        querys.put("param", "**name**:" + name + ",**phone**:" + userPhone);
        Map< String, String > bodys = new HashMap< String, String >();
        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        SmsMsg smsMsg = SmsMsg.fromJsonString(responseString);
        logger.info(smsMsg.toString());
        return smsMsg;
    }

    /**
     * 用户查看自己的待处理寻主任务 和已处理寻主任务
     * @param state
     * @return
     */
    @GetMapping(USER_PET_LIST)
    public NetResult petList(@RequestParam int state, HttpServletRequest request ){
        //通过token获取user的信息
        String token = request.getHeader("token");
        String userString = (String) redisTemplate.opsForValue().get(token);
        // 通过字符串处理获取用户的 id
        int startIndex = userString.indexOf("id=") + 3; // 获取 id 的起始位置
        int endIndex = userString.indexOf(",", startIndex); // 获取 id 的结束位置
        String idString = userString.substring(startIndex, endIndex); // 提取 id 的字符串表示
        Long userId = Long.parseLong(idString); // 将 id 字符串转换为 Long 类型

        List<PetFindMaster>petFindMasters = petFindMasterService.findByUser(state,userId);
        return ResultGenerator.genSuccessResult(petFindMasters);
    }

    /**
     * 获取所以店铺信息
     * @param
     * @return
     */
    @GetMapping(USER_SHOP_LIST)
    public NetResult shopList(){
        return ResultGenerator.genSuccessResult(shopService.list());
    }

    /**
     * 点击商铺 获取该商品宝贝列表
     * @param
     * @return
     */ @GetMapping(USER_SHOP_BABY)
    public NetResult getShopBaby(@RequestParam int shop_id) {
        return ResultGenerator.genSuccessResult(petCommodityService.findByShop(shop_id));
    }

    /**
     * 查看商品详情
     * @param
     * @return
     */
    @GetMapping(USER_CHECK_BABY)
    public NetResult checkBaby(@RequestParam long id) {
        PetCommodity petCommodity = petCommodityService.check(id);
        if(petCommodity!=null){
            if(petCommodity.getState()==0){
                return ResultGenerator.genFailResult("此商品已下架");
            }else if(petCommodity.getState()==1){
                petCommodity.setCostPrice(null);
                return ResultGenerator.genSuccessResult(petCommodity);
            }
        }
        return ResultGenerator.genFailResult("店铺数据异常");
    }

    /**
     * 领养宠物 修改宠物user_id 下架时间 修改宠物状态 商品下架
     * @param
     * @return
     */
    @GetMapping(USER_PET_ADOPT)
    public NetResult petAdopt(@RequestParam long id,HttpServletRequest request){
        //通过token获取user的信息
        String token = request.getHeader("token");
        String userString = (String) redisTemplate.opsForValue().get(token);
        // 通过字符串处理获取用户的 id
        int startIndex = userString.indexOf("id=") + 3; // 获取 id 的起始位置
        int endIndex = userString.indexOf(",", startIndex); // 获取 id 的结束位置
        String idString = userString.substring(startIndex, endIndex); // 提取 id 的字符串表示
        Long userId = Long.parseLong(idString); // 将 id 字符串转换为 Long 类型
        PetCommodity petCommodity = petCommodityService.check(id);
        if(petCommodity==null){
            return ResultGenerator.genFailResult("该商平不存在");
        }
        if(petCommodity.getState()==0){
            return ResultGenerator.genFailResult("该商品未上架");
        }
        long endTime = System.currentTimeMillis();
        int count = petCommodityService.petAdopt(userId,endTime,id);
        if(count==1){
            return ResultGenerator.genSuccessResult("领养成功");
        }
        return ResultGenerator.genFailResult("领养失败");
    }

    /**
     * 查看用户领养名单
     * @param request
     * @return
     */
    @GetMapping(USER_ADOPT_LIST)
    public NetResult petAdopt(HttpServletRequest request){
        //通过token获取user的信息
        String token = request.getHeader("token");
        String userString = (String) redisTemplate.opsForValue().get(token);
        // 通过字符串处理获取用户的 id
        int startIndex = userString.indexOf("id=") + 3; // 获取 id 的起始位置
        int endIndex = userString.indexOf(",", startIndex); // 获取 id 的结束位置
        String idString = userString.substring(startIndex, endIndex); // 提取 id 的字符串表示
        Long userId = Long.parseLong(idString); // 将 id 字符串转换为 Long 类型
        List<PetCommodity>petCommodities = petCommodityService.findByUser(userId);
        petCommodities.forEach(pet -> pet.setCostPrice(null));
        return ResultGenerator.genSuccessResult(petCommodities);
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
