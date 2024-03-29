package org.pet.home.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.*;
import org.pet.home.service.*;
import org.pet.home.service.impl.ServeService;
import org.pet.home.service.impl.ShopService;
import org.pet.home.service.impl.TypeService;
import org.pet.home.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/26
 **/
@Api(tags = "shop接口文档")
@RestController
@RequestMapping("/shop")
public class ShopController {
    private Logger logger = LoggerFactory.getLogger(ShopController.class);
    private static final String SHOP_REGISTER_URL = "/register";
    private static final String SHOP_PASS_URL = "/pass";
    private static final String SHOP_EDIT_URL = "/edit";
    private static final String SHOP_PAGINATION_LIST_URL = "/paginationList";
    private static final String SHOP_LIST_URL = "/list";
    private static final String SHOP_PET_LIST_URL = "/petList";
    private static final String SHOP_SURE_PET_URL = "/surePetTask";
    private static final String SHOP_REFUSE_URL = "/refuse";
    private static final String SHOP_REMOVE_URL = "/remove";
    private static final String SHOP_ADD_BABY_URL = "/addPetBaby";
    private static final String SHOP_BABY_LIST_URL = "/babyList";
    private static final String SHOP_REVISE_STATE_URL = "/reviseState";
    private static final String SHOP_ADD_SERVE_URL = "/savaServe";
    private static final String SHOP_SERVE_REVISE_STATE = "/serveReviseState";
    private static final String USER_SERVE_List = "/serveList";
    private RedisTemplate redisTemplate;
    private IShopService iShopService;
    private IEmployeeService iEmployeeService;
    private IPetFindMasterService iPetFindMasterService;
    private IUserService iUserService;
    private IPetCommodityService iPetCommodityService;
    private TypeService typeService;
    private ServeService serveService;

    @Autowired
    public ShopController(RedisTemplate redisTemplate, IShopService iShopService,
                          IEmployeeService iEmployeeService, IPetFindMasterService iPetFindMasterService,
                          IUserService iUserService, IPetCommodityService iPetCommodityService,
                          TypeService typeService, ServeService serveService) {
        this.redisTemplate = redisTemplate;
        this.iShopService = iShopService;
        this.iEmployeeService = iEmployeeService;
        this.iPetFindMasterService = iPetFindMasterService;
        this.iUserService = iUserService;
        this.iPetCommodityService = iPetCommodityService;
        this.typeService = typeService;
        this.serveService = serveService;
    }

    @ApiOperation("注册店铺")
    @PostMapping(SHOP_REGISTER_URL)
    public NetResult showRegister(@RequestBody Shop shop) {
        if (StringUtil.isEmpty(shop.getName())) {
            return ResultGenerator.genErrorResult(NetCode.SHOP_NAME_NULL, ErrorMessage.SHOP_NAME_NULL);
        }
        if (StringUtil.isEmpty(shop.getTel())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        if (StringUtil.isEmpty(shop.getAddress())) {
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_NULL, ErrorMessage.ADDRESS_NULL);
        }
        Shop s = iShopService.checkPhone(shop.getTel());
        if (s != null) {
            return ResultGenerator.genFailResult("该号码已注册，请换一个号码");
        }
        shop.setRegisterTime(System.currentTimeMillis());
        int count = iShopService.add(shop);
        if (count != 1) {
            return ResultGenerator.genFailResult("添加店铺失败");
        }
        return ResultGenerator.genSuccessResult(shop);
    }

    /**
     * 店铺获取待处理或者已处理列表
     *
     * @param state
     * @return
     */
    @GetMapping(SHOP_PET_LIST_URL)
    public NetResult petList(@RequestParam int state, HttpServletRequest request) {
        if(!StringUtil.stateIsNull(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        String token = request.getHeader("token");
        Employee admin =(Employee)redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        logger.info(admin.toString());
        List< PetFindMaster > petFindMasters = iPetFindMasterService.findByState(state, admin.getId());
        return ResultGenerator.genSuccessResult(petFindMasters);

    }

    /**
     * 确认订单
     *
     * @return
     */
    @PostMapping(SHOP_SURE_PET_URL)
    public NetResult surePetTask(@RequestParam int state, @RequestParam long petFindMaster_id) throws Exception {
        if(!StringUtil.stateIsNull(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        int count = iPetFindMasterService.updateState(state, petFindMaster_id);
        if (count != 1) {
            return ResultGenerator.genFailResult("订单异常");
        }
        //订单确认 发送信息
        PetFindMaster petFindMaster = iPetFindMasterService.findById(petFindMaster_id);
        Employee admin = iEmployeeService.findById(petFindMaster.getEmployee_id());
        User user = iUserService.findById(petFindMaster.getUser_id());
        AliSendSMSUtil.shopSendUser(admin.getPhone(), user.getUsername());
        return ResultGenerator.genSuccessResult(petFindMaster);
    }


    /**
     * 添加宠物宝贝
     * @param petCommodity
     * @return
     */
    @PostMapping(SHOP_ADD_BABY_URL)
    private NetResult addPetBaby(@RequestBody PetCommodity petCommodity) {
        if (petCommodity.getSellPrice() == null) {
            return ResultGenerator.genErrorResult(NetCode.SELL_PRICE_NULL, ErrorMessage.SELL_PRICE_NULL);
        }

        //根据前台的寻主任务id 获得宠物信息
        long petFindMaster_id = petCommodity.getPetFindMaster_id();
        PetFindMaster petFindMaster = iPetFindMasterService.findById(petFindMaster_id);
        if (petFindMaster == null) {
            return ResultGenerator.genFailResult("寻主任务异常，请查看");
        }
        BigDecimal costPrice = petFindMaster.getPrice();//成本价
        BigDecimal sellPrice = petCommodity.getSellPrice();//售价
        if (sellPrice.compareTo(costPrice) < 0) {//售价小于成本价
            return ResultGenerator.genFailResult("售价不能低于成本价");
        }
        petCommodity.setPetName(petFindMaster.getPetName());//宠物宝贝名字
        petCommodity.setSex(petFindMaster.getSex());
        petCommodity.setBirth(petFindMaster.getBirth());
        petCommodity.setSellTime(System.currentTimeMillis());
        petCommodity.setIsInoculation(petFindMaster.getIsInoculation());
        petCommodity.setUser_id(petFindMaster.getUser_id());
        petCommodity.setCostPrice(costPrice);
        petCommodity.setSellPrice(sellPrice);
        petCommodity.setShop_id(petFindMaster.getShop_id());
        petCommodity.setEmployee_id(petFindMaster.getEmployee_id());
        petCommodity.setPetCategory_id(petFindMaster.getPetCategory_id());

        int count = iPetCommodityService.add(petCommodity);
        if (count == 1) {
            return ResultGenerator.genSuccessResult(petCommodity);
        }
        return ResultGenerator.genFailResult("添加失败");
    }

    /**
     * 获取上架或者未上架商品
     * @param state
     * @param request
     * @return
     */
    @GetMapping(SHOP_BABY_LIST_URL)
    private NetResult addPetBaby(@RequestParam int state,HttpServletRequest request){
        if(!StringUtil.stateIsNull(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        String token = request.getHeader("token");
        Employee admin =(Employee)redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        logger.info(admin.toString());
        List<PetCommodity>petCommodities = iPetCommodityService.findByState(state,admin.getId());
        return ResultGenerator.genSuccessResult(petCommodities);

    }

    /**
     * 商品上下架
     * @param state
     * @param id
     * @return
     */
    @PostMapping(SHOP_REVISE_STATE_URL)
    private NetResult reviseState(@RequestParam int state,@RequestParam long id){
        if(!StringUtil.stateIsNull(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        int count = iPetCommodityService.updateState(state,id);
        if(count==1){
            if(state==1){
                return ResultGenerator.genSuccessResult("商品上架成功");
            }else if(state==0){
                return ResultGenerator.genSuccessResult("商品下架成功");
            }
        }
        return ResultGenerator.genFailResult("商品状态修改失败");
    }

    /**
     * 添加服务类
     * @param request
     * @param serve
     * @return
     */
    @PostMapping(SHOP_ADD_SERVE_URL)
    private NetResult saveServe(HttpServletRequest request,@RequestBody Serve serve){
        String token = request.getHeader("token");
        Employee admin =(Employee)redisTemplate.opsForValue().get(RedisKeyUtil.getTokenRedisKey(token));
        Shop shop = iShopService.checkPhone(admin.getPhone());
        logger.info(shop.getTel());
        if(shop==null){
            return ResultGenerator.genFailResult("店铺异常");
        }
        serve.setShop_id(shop.getId());
        String name = serve.getServe_name();
        if(StringUtil.isEmpty(name)){
            return ResultGenerator.genFailResult("项目名不能为空");
        }
        long type = serve.getType_id();
        Type t = typeService.findById(type);
        if(t==null){
            return ResultGenerator.genFailResult("项目类别不存在");
        }
        double price = serve.getPrice();
        if(price<=0){
            return ResultGenerator.genFailResult("项目价格异常");
        }
        int count = serveService.add(serve);
        if(count==1){
            return ResultGenerator.genSuccessResult(serve);
        }
        return ResultGenerator.genFailResult("添加服务失败");
    }

    /**
     *
     * @param state
     * @return
     */
    @GetMapping(SHOP_SERVE_REVISE_STATE)
    private NetResult serveReviseState(@RequestParam int state,@RequestParam long id){
        if(!StringUtil.stateIsNull(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        Serve serve = serveService.findById(id);
        if(serve==null){
            return ResultGenerator.genFailResult("该服务项目不存在");
        }
        int count = serveService.updateState(id,state);
        if(count==1){
            if(state==1){
                return ResultGenerator.genSuccessResult("上架成功");
            }
            if(state==0){
                return ResultGenerator.genSuccessResult("下架成功");
            }
        }
        return ResultGenerator.genFailResult("修改失败");
    }

    /**
     * 根据上下架的状态获得相关服务列表 （分页）
     * @param state
     * @param page
     * @param size
     * @return
     */
    @GetMapping(USER_SERVE_List)
    public NetResult USER_SERVE_List(@RequestParam int state,@RequestParam int page,@RequestParam int size) {
        if (!StringUtil.stateIsNull(state)){
            return ResultGenerator.genFailResult("状态码异常");
        }
        int offset = (page-1)*size;
        List<Serve>serves=serveService.listByState(state,size,offset);
        return ResultGenerator.genSuccessResult(serves);
    }

    @GetMapping(SHOP_LIST_URL)
    public NetResult list() {
        List< Shop > shops = iShopService.list();
        return ResultGenerator.genSuccessResult(shops);
    }

    @PostMapping(SHOP_PAGINATION_LIST_URL)
    public NetResult list(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        //分页功能 的页数 也就是page是从0开始还是从1不取决其它的人，只取决后台怎么写
        //如果后台的计算是从0开始。那别人调接口就是必须从0开始
        int count = iShopService.count(); // 获取总记录数
        //sql的 offset 字段不等于你的page，它代表的是从第几个数据开始取
        //当前传的是page = 1 ， pageSize = 10
        //那假如后台是page=1就代表第一页
        int offset = (page - 1) * pageSize; //第二页就是  从10开始?
        //那假如后台是page=0代表第一页
//        offset = page * 10;
        List< Shop > shops = iShopService.paginationList(offset, pageSize);
        ShopParam shopParam = new ShopParam();
        shopParam.shops = shops;
        shopParam.total = count;
        //一般分页的返回数据基本上就是{ data:[], count:所有的数据的size}
        //为什么要给count给别人。别人可以通过count取算有多少页，这个页不是后台算的
        return ResultGenerator.genSuccessResult(shopParam);
    }

    @PostMapping(SHOP_EDIT_URL)
    public NetResult edit(@RequestBody Shop shop) {
        try {
            iShopService.update(shop);
            return ResultGenerator.genSuccessResult("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @PostMapping(SHOP_PASS_URL)
    public NetResult pass(@RequestBody RequestData requestData) {
        try {
            int state = requestData.getState(); // 获取状态
            Shop shop = requestData.getShop(); // 获取店铺信息
            iShopService.updateState(shop.getId(), state);
            if (state == 1) {//如果申请成功 自动生成admin账号
                Employee employee = new Employee();
                employee.setUsername(shop.getName());
                employee.setPhone(shop.getTel());
                String password = MD5Util.MD5Encode("123456", "utf-8");
                employee.setPassword(password);
                iEmployeeService.add(employee);
                Employee e = iEmployeeService.login(employee.getPhone(), password);
                iShopService.addAdmin(shop, e.getId());
                return ResultGenerator.genSuccessResult("申请成功");
            } else if (state == 2) {
                return ResultGenerator.genSuccessResult("拒绝成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("申请失败");
        }
        return null;
    }

    @PostMapping(SHOP_REFUSE_URL)
    public NetResult refuse(@RequestBody RequestData requestData) {
        try {
            int state = requestData.getState(); // 获取状态
            Shop shop = requestData.getShop(); // 获取店铺信息
            iShopService.updateState(shop.getId(), state);
            return ResultGenerator.genSuccessResult("拒绝成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("申请失败");
        }
    }

    @PostMapping(SHOP_REMOVE_URL)
    public NetResult delete(@RequestBody Shop shop) {
        try {
            iShopService.delete(shop.getId());
            //删除用户 那么它对应的employee也要删除??
            return ResultGenerator.genSuccessResult("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_SHOP_ERROR, ErrorMessage.REMOVE_SHOP_ERROR + e.getMessage());
        }
    }
}
