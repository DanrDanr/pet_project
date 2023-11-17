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
import org.springframework.beans.BeanUtils;
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

    private IEmployeeService iEmployeeService;
    private ShopService shopService;

    private PetCategoryService petCategoryService;
    private PetFindMasterService petFindMasterService;

    private PetCommodityService petCommodityService;

    @Autowired
    public UserController(RedisTemplate redisTemplate,

                          RedisService redisService, UserService userService,
                          IEmployeeService iEmployeeService, ShopService shopService,
                          PetCategoryService petCategoryService, PetFindMasterService petFindMasterService,
                          PetCommodityService petCommodityService) {
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
        this.userService = userService;
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
    public NetResult SMSSendCode(@RequestParam String phone){
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
        String smsCode = SMSCode.getSMSCode();
        String smsResult = AliSendSMSUtil.sendSMS(smsCode,phone);
        if(smsResult == null){
            return ResultGenerator.genFailResult("发送验证码失败！");
        }

        // 将新的验证码存入缓存，设置过期时间为60秒
        redisTemplate.opsForValue().set(RedisKeyUtil.getSMSRedisKey(phone), smsCode, 300, TimeUnit.SECONDS);
        return ResultGenerator.genSuccessResult(Result.fromJsonString(smsResult));
    }


    /**
     * 用户注册
     *
     * @return
     */
    @GetMapping(USER_REGISTER_URL)
    public NetResult register(@RequestBody RegisterAndLoginParam registerAndLoginParam) {

        // 排除用户名为空的状态
        if (StringUtil.isEmpty(registerAndLoginParam.getUsername())) {

            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL, ErrorMessage.USERNAME_NULL);
        }
        // 排除密码为空的状态
        if (StringUtil.isEmpty(registerAndLoginParam.getPassword())) {
            return ResultGenerator.genErrorResult(NetCode.USER_PASSWORD_NULL, ErrorMessage.USER_PASSWORD_NULL);
        }
        // 排除用户邮箱为空的状态
        if (StringUtil.isEmpty(registerAndLoginParam.getEmail())) {
            return ResultGenerator.genErrorResult(NetCode.EMAIL_NULL, ErrorMessage.EMAIL_NULL);
        }
        //getphone的逻辑很简单，但是有时候方法执行的逻辑很负责，你就不能每次去调方法，并且
        //还存在方法每次执行的return都不一样。
        String phone = registerAndLoginParam.getPhone();
        // 排除电话未空的状态
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        // 排除手机格式不正确
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }
        //check phone 给人的感觉是检查手机号，而不是判断手机号是否注册
        User u = userService.checkPhone(phone);

        if (u != null) {
            // 排除手机号码已注册的状态
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPATION, ErrorMessage.PHONE_OCCUPATION);
        }

        //先判断哪个后判断哪个，项目没有要求，但是，如果测试有要求，那么则必须按照测试给定的顺序去判断
        String code = registerAndLoginParam.code;
        if (StringUtil.isEmpty(code)) {
            return ResultGenerator.genErrorResult(NetCode.CODE_NULL, ErrorMessage.CODE_NULL);
        }
        // 尝试从缓存中获取验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(RedisKeyUtil.getSMSRedisKey(phone));
        if (!StringUtil.isEmpty(cachedCode)) {
            if (code.equals(cachedCode)) {
                String password = MD5Util.MD5Encode(registerAndLoginParam.getPassword(), "utf-8");
                u = new User();//spring 属性copy的,
                BeanUtils.copyProperties(registerAndLoginParam,u);
                logger.info(u.toString());
                u.setPassword(password);
                userService.add(u);
                u.setPassword(null);
                return ResultGenerator.genSuccessResult(u);
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
        String phone = userParam.getPhone();
        //排除号码为账号为空的情况
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL, ErrorMessage.USERNAME_NULL);
        }
        // 排除手机格式不正确
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, ErrorMessage.PHONE_INVALID);
        }

        //排除密码为null的状态
        if (StringUtil.isEmpty(userParam.password)) {
            return ResultGenerator.genErrorResult(NetCode.USER_PASSWORD_NULL, ErrorMessage.USER_PASSWORD_NULL);
        }
        //排除验证码为空的状态
        if (StringUtil.isEmpty(userParam.code)) {
            return ResultGenerator.genErrorResult(NetCode.CODE_NULL, ErrorMessage.CODE_NULL);
        }

        // 尝试从缓存中获取验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(RedisKeyUtil.getSMSRedisKey(phone));
        if (!StringUtil.isEmpty(cachedCode)) {
            if (userParam.code.equals(cachedCode)) {
                //登陆的密码  是 传到后端，然后后端去做md5然后去检验，
                // 一般的情况下，都是前端用md5去加密密码，然后 将加密的密码传输给后台，
                //然后后台直接判断   这样就避免了密码的 明文传输
                String password = MD5Util.MD5Encode(userParam.getPassword(), "utf-8");
                if (userParam.role == 0) {//用户登陆
                    User u = userService.userLogin(phone, password);
                    if (u != null) {//如果获取的值不为空即代表账号密码正确
                        //通过UUID的唯一特性用它为K 保存用户v 设置保存时间
                        //每次登陆都会重新跟更新
                        String token = UUID.randomUUID().toString();
                        logger.info("token->" + token);
                        //redis你要存object，必须要给对应的object加上序列化
                        //序列化的过程 是值 将 对象 -> 字节数组
                        //反序列化的过程就是将 字节数组 -> 对象 序列化你可以任何事给这个 对象->字节数组加上了一个 规则
                        // java为了提供序列化的转换， ObjectOutputStream

                        redisTemplate.opsForValue().set(RedisKeyUtil.getTokenRedisKey(token), u, 180, TimeUnit.MINUTES);
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
                        redisTemplate.opsForValue().set(RedisKeyUtil.getTokenRedisKey(token),e, 180, TimeUnit.MINUTES);
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
     * 说到底，所有的逻辑
     * 就是 对数据去做检查，去做存储之，数据的查询 的
     *
     * 用户添加寻主任务
     *
     * @param
     * @return
     */
    @PostMapping(USER_ADD_TASK)
    public NetResult AddPetFindMaster(@RequestBody PetFindMaster petFindMaster, HttpServletRequest request){


        // 排除宠物名为空的状态
        if (StringUtil.isEmpty(petFindMaster.getPetName())) {
            return ResultGenerator.genErrorResult(NetCode.PET_NAME_NULL, ErrorMessage.PET_NAME_NULL);
        }
        if (petFindMaster.getSex() !=0 && petFindMaster.getSex()!=1) {
            return ResultGenerator.genErrorResult(NetCode.PET_SEX_INVALID, ErrorMessage.PET_SEX_INVALID);
        }
        if (petFindMaster.getBirth() < 1 ) { //万一我穿一个 -1呢？
            return ResultGenerator.genErrorResult(NetCode.PET_BIRTH_INVALID, ErrorMessage.PET_BIRTH_INVALID);
        }
        if (!StringUtil.state(petFindMaster.getIsInoculation())) {
            return ResultGenerator.genErrorResult(NetCode.PET_IS_INOCULATION_INVALID, ErrorMessage.PET_IS_INOCULATION_INVALID);
        }
        //宠物分类，按照需求，
        // 应该是有一个宠物分类列表结果 , 然后这个参数是从宠物分类列表接口里面拿的
        // 1： 为不为空
        // 2： id合法性 比较你的宠物分类 ， 那这个时候又要从数据库拿吗?
        // 如果宠物分类是非常常用的数据，那每次都要从数据库拿耗费流量时间吗？
        // 放在redis里面， 每次从redis里面读 , 就非常快了。
        // redis.set (pet_id, pet_max_id )
        if (petFindMaster.getPetCategory_id() == 0) {
            return ResultGenerator.genErrorResult(NetCode.PET_CATEGORY_INVALID, ErrorMessage.PET_CATEGORY_INVALID);
        }
        if (StringUtil.isEmpty(petFindMaster.getAddress())) {
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_NULL, ErrorMessage.ADDRESS_NULL);
        }

        //拦截器到这里还是有一点时间的，虽然很快。 但是在使用前检查一下还是可以的
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        if(user == null){
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, ErrorMessage.TOKEN_INVALID);
        }

        //排除所以输入异常状态后我们看一下所有shop的地址
        List< Shop > shops = shopService.list();
        Location userLocation = null;
        try {
            userLocation = GaoDeMapUtil.getLngAndLag(petFindMaster.getAddress());
        } catch (UnsupportedEncodingException e) {
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_INVALID, ErrorMessage.ADDRESS_INVALID);
        }
        List< Location > locations = new LinkedList<>();
        for (int i = 0; i < shops.size(); i++) {
            try {
                locations.add(i, GaoDeMapUtil.getLngAndLag(shops.get(i).getAddress()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        //获得最近的地址
        Location near = AddressDistanceComparator.findNearestAddress(userLocation, locations);
        //根据地址确定要绑定的shop
        Shop shop = shopService.findByAddress(near.getAddress());
        if (shop == null) {
            return ResultGenerator.genFailResult("附近没有匹配的店铺");
        }
        petFindMaster.setShop_id(shop.getId());
        //根据店铺获取要绑定的shop_admin的账号
        petFindMaster.setEmployee_id(shop.getAdmin_id());
        //绑定的user
        petFindMaster.setUser_id(user.getId());
        //添加时间
        petFindMaster.setCreateTime(System.currentTimeMillis());
        //然后可以添加寻主任务
        logger.info(petFindMaster.toString());

        int count = petFindMasterService.add(petFindMaster);
        if (count != 1) {
            return ResultGenerator.genFailResult("添加失败");
        }
        //添加寻主任务成功 发短信给商家
        try {
            AliSendSMSUtil.sendSmsShop(shop.getTel(), user.getUsername(), user.getPhone());
        } catch (Exception e) {
           //打印发送验证码错误
           // 验证码没发送会记录到数据库
           logger.error(e.getMessage());
        }
        return ResultGenerator.genSuccessResult(petFindMaster);

    }



    /**
     * 用户查看自己的待处理寻主任务 和已处理寻主任务
     * @param state
     * @return
     */
    @GetMapping(USER_PET_LIST)
    public NetResult petList(@RequestParam int state, HttpServletRequest request ){
        if(!StringUtil.state(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        logger.info("user->"+user);

        List<PetFindMaster>petFindMasters = petFindMasterService.findByUser(state,user.getId());
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
     * @param id 宠物商铺的id
     * @return
     */
    @GetMapping(USER_CHECK_BABY)
    public NetResult babyDetails(@RequestParam long id) {
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
     * @param id 宠物的id
     * @return
     */
    @GetMapping(USER_PET_ADOPT)
    public NetResult petAdopt(@RequestParam long id,HttpServletRequest request){
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));


        PetCommodity petCommodity = petCommodityService.check(id);
        if(petCommodity==null){
            return ResultGenerator.genFailResult("该商平不存在");
        }
        if(petCommodity.getState()==0){
            return ResultGenerator.genFailResult("该商品未上架");
        }
        long endTime = System.currentTimeMillis();
        int count = petCommodityService.petAdopt(user.getId(),endTime,id);
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
