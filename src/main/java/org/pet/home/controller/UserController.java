package org.pet.home.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
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
    private static final String USER_CHECK_BABY = "/babyCheck";
    private static final String USER_PET_ADOPT = "/petAdopt";
    private static final String USER_PAY_PET = "/payPet";
    private static final String USER_ADOPT_LIST = "/adoptList";
    private static final String USER_PAY = "/pay";
    private static final String USER_RECHARGE = "/recharge";
    private static final String USER_SERVE_LIST = "/serveList";
    private static final String USER_SERVE = "/serve";
    private static final String USER_BUY_SERVE = "/buyServe";
    private RedisTemplate redisTemplate;
    private RedisService redisService;
    private UserService userService;
    private IEmployeeService iEmployeeService;
    private ShopService shopService;
    private PetCategoryService petCategoryService;
    private PetFindMasterService petFindMasterService;
    private PetCommodityService petCommodityService;
    private IOrderService orderService;
    private ApplicationContext applicationContext;
    private TypeService typeService;
    private ServeService serveService;

    @Autowired
    public UserController(RedisTemplate redisTemplate, IOrderService orderService,
                          RedisService redisService, UserService userService,
                          IEmployeeService iEmployeeService, ShopService shopService,
                          PetCategoryService petCategoryService, PetFindMasterService petFindMasterService,
                          PetCommodityService petCommodityService,ApplicationContext applicationContext,
                          TypeService typeService, ServeService serveService) {
        this.redisTemplate = redisTemplate;
        this.redisService = redisService;
        this.userService = userService;
        this.iEmployeeService = iEmployeeService;
        this.shopService = shopService;
        this.petCategoryService = petCategoryService;
        this.petFindMasterService = petFindMasterService;
        this.petCommodityService = petCommodityService;
        this.orderService = orderService;
        this.applicationContext = applicationContext;
        this.typeService=typeService;
        this.serveService = serveService;
    }

    /**
     * 短信发送验证码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    @GetMapping(SMS_SEND_CODE_URL)
    public NetResult SMSSendCode(@RequestParam String phone) {
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
        String smsResult = AliSendSMSUtil.sendSMS(smsCode, phone);
        if (smsResult == null) {
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
                BeanUtils.copyProperties(registerAndLoginParam, u);
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
                        redisTemplate.opsForValue().set(RedisKeyUtil.getTokenRedisKey(token), e, 180, TimeUnit.MINUTES);
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
     * <p>
     * 用户添加寻主任务
     *
     * @param
     * @return
     */
    @PostMapping(USER_ADD_TASK)
    public NetResult AddPetFindMaster(@RequestBody PetFindMaster petFindMaster, HttpServletRequest request) {
        // 排除宠物名为空的状态
        if (StringUtil.isEmpty(petFindMaster.getPetName())) {
            return ResultGenerator.genErrorResult(NetCode.PET_NAME_NULL, ErrorMessage.PET_NAME_NULL);
        }
        if (petFindMaster.getSex() != 0 && petFindMaster.getSex() != 1) {
            return ResultGenerator.genErrorResult(NetCode.PET_SEX_INVALID, ErrorMessage.PET_SEX_INVALID);
        }
        if (petFindMaster.getBirth() < 0) { //万一我穿一个 -1呢？
            return ResultGenerator.genErrorResult(NetCode.PET_BIRTH_INVALID, ErrorMessage.PET_BIRTH_INVALID);
        }
        if (!StringUtil.isInoculationIsNull(petFindMaster.getIsInoculation())) {
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
        if (user == null) {
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
     *
     * @param state
     * @return
     */
    @GetMapping(USER_PET_LIST)
    public NetResult petList(@RequestParam int state, HttpServletRequest request) {
        if (!StringUtil.stateIsNull(state)) {
            return ResultGenerator.genFailResult("状态码异常");
        }
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        logger.info("user->" + user);

        List< PetFindMaster > petFindMasters = petFindMasterService.findByUser(state, user.getId());
        return ResultGenerator.genSuccessResult(petFindMasters);
    }

    /**
     * 获取所以店铺信息
     *
     * @param
     * @return
     */
    @GetMapping(USER_SHOP_LIST)
    public NetResult shopList() {
        return ResultGenerator.genSuccessResult(shopService.list());
    }

    /**
     * 点击商铺 获取该商品宝贝列表
     *
     * @param
     * @return
     */
    @GetMapping(USER_SHOP_BABY)
    public NetResult getShopBaby(@RequestParam int shop_id) {
        return ResultGenerator.genSuccessResult(petCommodityService.findByShop(shop_id));
    }

    /**
     * 查看商品详情
     *
     * @param id 宠物商铺的id
     * @return
     */
    @GetMapping(USER_CHECK_BABY)
    public NetResult babyDetails(@RequestParam long id) {
        PetCommodity petCommodity = petCommodityService.check(id);
        if (petCommodity != null) {
            if (petCommodity.getState() == 0) {
                return ResultGenerator.genFailResult("此商品已下架");
            } else if (petCommodity.getState() == 1) {
                petCommodity.setCostPrice(null);
                return ResultGenerator.genSuccessResult(petCommodity);
            }
        }
        return ResultGenerator.genFailResult("店铺数据异常");
    }

    /**
     * 领养宠物 修改宠物user_id 下架时间 修改宠物状态 商品下架
     *
     * @param id 宠物的id
     * @return
     */
    @GetMapping(USER_PET_ADOPT)
    public NetResult petAdopt(@RequestParam long id, HttpServletRequest request) {
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));

        PetCommodity petCommodity = petCommodityService.check(id);
        if (petCommodity == null) {
            return ResultGenerator.genFailResult("该商平不存在");
        }
        if (petCommodity.getState() == 0) {
            return ResultGenerator.genFailResult("该商品未上架");
        }
        long endTime = System.currentTimeMillis();
        int count = petCommodityService.petAdopt(user.getId(), endTime, id);
        if (count == 1) {
            return ResultGenerator.genSuccessResult("领养成功");
        }
        return ResultGenerator.genFailResult("领养失败");
    }

    /**
     * 领养宠物 生成订单
     * @param id
     * @param request
     * @return
     */
    @GetMapping(USER_PAY_PET)
    public NetResult payPet(@RequestParam long id, HttpServletRequest request) {
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        if(user==null){
            return ResultGenerator.genFailResult("token过期");
        }
        PetCommodity petCommodity = petCommodityService.check(id);
        if (petCommodity == null) {
            return ResultGenerator.genFailResult("该商品不存在");
        }
        if (petCommodity.getState() == 0) {
            return ResultGenerator.genFailResult("该商品未上架");
        }
        //生成订单
        Order order = new Order();
        order.setAmount(petCommodity.getSellPrice());
        order.setPetCommodity_id(petCommodity.getId());
        order.setShop_id(petCommodity.getShop_id());
        order.setUser_id(user.getId());
        long createTime = System.currentTimeMillis();
        order.setCreateTime(createTime);
        String orderNumber =UUID.randomUUID().toString();
        order.setOrderNumber(orderNumber);
        int count = orderService.add(order);
        if (count == 1) {
            //订单生成成功
            //把订单信息存到redis 存的时间根据延时时间来设置 这里为了方便测试用1分钟
//            long expireTime = System.currentTimeMillis() + 60 * 1000; // 当前时间加上一分钟的毫秒数
            //订单号为k 过期时间为v
            //对应定时器
//            redisTemplate.opsForValue().set(RedisKeyUtil.getOrderRedisKey(orderNumber), expireTime);
            //对应redis过期监听
//            redisTemplate.opsForValue().set(RedisKeyUtil.getOrderRedisKey(orderNumber), order,1,TimeUnit.MINUTES);
            // 开启定时任务并传递订单号
            QuartzSchedulerUtil.startOrderExpirationJob(orderNumber,orderService);
            return ResultGenerator.genSuccessResult(order);
        }
        return ResultGenerator.genFailResult("领养失败");
    }

    /**
     * 支付宝支付接口
     *
     * @param
     * @return
     */
    @PostMapping(USER_PAY)
    public NetResult pay() {
        String aliPayGateway = "https://openapi.alipay.com/gateway.do";
        String appID = "支付宝分配给开发者的应用ID";
        String rsa_private_key = "商户应用私钥";
        String format = "数据格式商品信息";
        String charset = "编码格式";
        String alipayPublicKey = "支付宝公钥";
        String signType = "签名算法类型";
        String orderNo = "商户订单编号";
        //网关地址,APPID,商户应用私钥,数据格式,编码格式,支付宝公钥,签名算法类型 不同支付类型需使用不同的请求对象
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayGateway, appID, rsa_private_key, format, charset, alipayPublicKey, signType);
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        //请求参数集合对象,除了公共参数之外,所有参数都可通过此对象传递（不同支付类型需使用不同的请求参数对象）
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        //订单描述
        model.setBody("订单描述");
        //订单标题
        model.setSubject("显示效果见于下图的标题");
        //商户订单号
        model.setOutTradeNo(orderNo);
        //订单的过期时长(取值为5m - 15d,即五分钟到十五天)
        model.setTimeoutExpress("30m");
        //订单总金额
//        model.setTotalAmount(String.valueOf(cashNum));
        //产品码  QUICK_WAP_WAY:无线快捷支付产品
        model.setProductCode("QUICK_MSECURITY_PAY");
        //用户付款中途退出返回商户网站的地址
        model.setQuitUrl("https://wwww.baidu.com");
        request.setBizModel(model);
        //支付成功后的跳转地址
        request.setReturnUrl("支付成功之后的跳转地址");
        //支付成功后的回调地址（此地址必须为公网地址，用于支付宝收到用户付款之后,通知我们的服务端,我们可以在此接口中更改订单状态为已付款或后续操作）
//        request.setNotifyUrl(aliPayNotifyUrl);
        String orderStr = "";
        AlipayTradeWapPayResponse responseH5 = null;
        try {
            responseH5 = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
//            return ReturnUtils.returnVal(CommonConstants.appCode.UNKNOWNERROR.get(), null);
            return ResultGenerator.genFailResult("支付失败");
        }

        orderStr = responseH5.getBody();
        Map< String, String > result = new HashMap< String, String >();
        result.put("orderStr", orderStr);
        result.put("outTradeNo", orderNo);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 查看用户领养名单
     *
     * @param request
     * @return
     */
    @GetMapping(USER_ADOPT_LIST)
    public NetResult petAdopt(HttpServletRequest request) {
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        List< PetCommodity > petCommodities = petCommodityService.findByUser(user.getId());
        petCommodities.forEach(pet -> pet.setCostPrice(null));
        return ResultGenerator.genSuccessResult(petCommodities);
    }

    /**
     * 用户充值
     * @param request
     * @return
     */
    @GetMapping(USER_RECHARGE)
    public NetResult recharge(HttpServletRequest request,@RequestParam double balance) {
        //通过token获取user的信息
        String token = request.getHeader("token");
        User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        long id = user.getId();
        if(balance<=0){
            return ResultGenerator.genFailResult("充值异常");
        }
        int count = userService.recharge(id,balance);
        if(count==1){
            return ResultGenerator.genSuccessResult("充值"+balance+"成功");
        }
        return ResultGenerator.genFailResult("充值失败");
    }
    /**
     * 根据上下架的状态获得相关服务列表 （分页）
     * @param page
     * @param size
     * @return
     */
    @GetMapping(USER_SERVE_LIST)
    public NetResult serveList(@RequestParam int page,@RequestParam int size) {
        int offset = (page-1)*size;
        List<Serve>serves=serveService.list(size,offset);
        return ResultGenerator.genSuccessResult(serves);
    }

    /**
     * 根据id查看服务
     * @param id
     * @return
     */
    @GetMapping(USER_SERVE)
    public NetResult serveByID(@RequestParam long id) {
        Serve serve = serveService.findById(id);
        if(serve!=null){
            int state = serve.getState();
            if(state==0){
                return ResultGenerator.genSuccessResult("该服务没上架");
            }
            if (state==1){
                return ResultGenerator.genSuccessResult(serve);
            }
        }
        return ResultGenerator.genFailResult("该服务不存在");
    }

    /**
     * 购买服务
     * @param request
     * @param id
     * @param number
     * @return
     */
    @GetMapping(USER_BUY_SERVE)
    public NetResult buyServe(HttpServletRequest request,@RequestParam long id,@RequestParam int number) {
        Serve serve = serveService.findById(id);
        if(serve!=null){
            int state = serve.getState();
            if(state==0){
                return ResultGenerator.genSuccessResult("该服务没上架");
            }
            if (state==1){
                //通过token获取user的信息
                String token = request.getHeader("token");
                User user = (User) redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
                if(user==null){
                    return ResultGenerator.genFailResult("token过期");
                }
                double balance = user.getBalance();//用户余额
                double amount = serve.getPrice()*number;//消费金额=服务单价*数量
                if(balance<amount){
                    return ResultGenerator.genFailResult("余额不足请充值");
                }
                //余额充足那就可以购买
                //支付成功----修改服务销量  扣除用户余额
                serveService.updateSales(id,serve.getSales()+number);
                userService.pay(user.getId(), amount);
                CodeResBean codeResBean =new CodeResBean();
                codeResBean.v=serve;
                codeResBean.msg="消费金额:"+amount;
                return ResultGenerator.genSuccessResult(codeResBean);
            }
        }
        return ResultGenerator.genFailResult("该服务不存在");
    }

    /**
     *
     * @param phone
     * @return
     */

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
