package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.*;
import org.pet.home.service.IEmployeeService;
import org.pet.home.service.IPetFindMasterService;
import org.pet.home.service.IShopService;
import org.pet.home.service.IUserService;
import org.pet.home.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/26
 **/
@Api(tags = "shop接口文档")
@RestController
@RequestMapping("/shop")
public class  ShopController {

    private static final String SHOP_REGISTER_URL = "/register";
    private static final String SHOP_PASS_URL = "/pass";
    private static final String SHOP_EDIT_URL = "/edit";
    private static final String SHOP_PAGINATION_LIST_URL = "/paginationList";
    private static final String SHOP_LIST_URL = "/list";
    private static final String SHOP_PET_LIST_URL = "/petList";
    private static final String SHOP_SURE_PET_URL = "/surePetTask";
    private static final String SHOP_REFUSE_URL = "/refuse";
    private static final String SHOP_REMOVE_URL = "/remove";

    private IShopService iShopService;
    private IEmployeeService iEmployeeService;

    private IPetFindMasterService iPetFindMasterService;

    private IUserService iUserService;

    @Autowired
    public ShopController(IShopService iShopService, IEmployeeService iEmployeeService, IPetFindMasterService iPetFindMasterService, IUserService iUserService) {
        this.iShopService = iShopService;
        this.iEmployeeService = iEmployeeService;
        this.iPetFindMasterService = iPetFindMasterService;
        this.iUserService = iUserService;
    }

    @ApiOperation("注册店铺")
    @PostMapping(SHOP_REGISTER_URL)
    public NetResult showRegister(@RequestBody Shop shop){
        if (StringUtil.isEmpty(shop.getName())) {
            return ResultGenerator.genErrorResult(NetCode.SHOP_NAME_NULL, ErrorMessage.SHOP_NAME_NULL);
        }
        if (StringUtil.isEmpty(shop.getTel())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        if (StringUtil.isEmpty(shop.getAddress())) {
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_NULL, ErrorMessage.ADDRESS_NULL);
        }
        if(shop.getAdmin() == null){
            Employee employee = new Employee();
            employee.setId(0L);
            shop.setAdmin(employee);
        }
        Shop s = iShopService.checkPhone(shop.getTel());
        if(s!=null){
            return ResultGenerator.genFailResult("该号码已注册，请换一个号码");
        }
        shop.setRegisterTime(System.currentTimeMillis());
        int count = iShopService.add(shop);
        if(count!=1){
            return ResultGenerator.genFailResult("添加店铺失败");
        }
        return ResultGenerator.genSuccessResult(shop);
    }

    /**
     * 店铺获取待处理或者已处理列表
     * @param state
     * @return
     */
    @GetMapping(SHOP_PET_LIST_URL)
    public NetResult petList(@RequestParam int state){
        List<PetFindMaster>petFindMasters = iPetFindMasterService.findByState(state);
        return ResultGenerator.genSuccessResult(petFindMasters);
    }

    /**
     * 确认订单
     * @return
     */
    @PostMapping(SHOP_SURE_PET_URL)
    public NetResult surePetTask(@RequestParam int state,@RequestParam long petFindMaster_id) throws Exception {
        int count = iPetFindMasterService.updateState(state,petFindMaster_id);
        if (count!=1){
            return ResultGenerator.genFailResult("订单异常");
        }
        //订单确认 发送信息
        PetFindMaster petFindMaster =iPetFindMasterService.findById(petFindMaster_id);
        Employee admin = iEmployeeService.findById(petFindMaster.getEmployee_id());
        User user = iUserService.findById(petFindMaster.getUser_id());
        shopSendUser(admin.getPhone(),user.getUsername());
        return ResultGenerator.genSuccessResult(petFindMaster);
    }
    /**
     * 店铺发给用户通知订单已审核
     * @param phone
     * @param name
     * @param
     */
    private SmsMsg shopSendUser(String phone, String name) throws Exception {
        String host = "https://gyyyx1.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "25948b3da7cd41699b37c71c2a70070c";
        Map< String, String > headers = new HashMap< String, String >();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map< String, String > querys = new HashMap< String, String >();
        querys.put("mobile", phone);
        querys.put("templateId", "0f7b6dcf69a64acea4278fad09a31aee");
        querys.put("smsSignId", "1596868d15704706bee87cca32639de7");
        querys.put("param", "**name**:" + name);
        Map< String, String > bodys = new HashMap< String, String >();

        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        SmsMsg smsMsg = SmsMsg.fromJsonString(responseString);
        return smsMsg;

    }

    @GetMapping(SHOP_LIST_URL)
    public NetResult list(){
        List<Shop> shops = iShopService.list();
        return ResultGenerator.genSuccessResult(shops);
    }
    @PostMapping(SHOP_PAGINATION_LIST_URL)
    public NetResult list(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize){
        //分页功能 的页数 也就是page是从0开始还是从1不取决其它的人，只取决后台怎么写
        //如果后台的计算是从0开始。那别人调接口就是必须从0开始
        int count = iShopService.count(); // 获取总记录数
        //sql的 offset 字段不等于你的page，它代表的是从第几个数据开始取
        //当前传的是page = 1 ， pageSize = 10
        //那假如后台是page=1就代表第一页
        int offset = (page-1) * pageSize; //第二页就是  从10开始?
        //那假如后台是page=0代表第一页
//        offset = page * 10;
        List<Shop> shops = iShopService.paginationList(offset,pageSize);
        ShopParam shopParam = new ShopParam();
        shopParam.shops=shops;
        shopParam.total = count;
        //一般分页的返回数据基本上就是{ data:[], count:所有的数据的size}
        //为什么要给count给别人。别人可以通过count取算有多少页，这个页不是后台算的
        return ResultGenerator.genSuccessResult(shopParam);
    }

    @PostMapping(SHOP_EDIT_URL)
    public NetResult edit(@RequestBody Shop shop){
        try {
            iShopService.update(shop);
            return ResultGenerator.genSuccessResult("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @PostMapping(SHOP_PASS_URL)
    public NetResult pass(@RequestBody RequestData requestData){
        try {
            int state = requestData.getState(); // 获取状态
            Shop shop = requestData.getShop(); // 获取店铺信息
            iShopService.updateState(shop.getId(),state);
            if(state==1){//如果申请成功 自动生成admin账号
                Employee employee = new Employee();
                employee.setUsername(shop.getName());
                employee.setPhone(shop.getTel());
                String password = MD5Util.MD5Encode("123456","utf-8");
                employee.setPassword(password);
                iEmployeeService.add(employee);
                Employee e = iEmployeeService.login(employee.getPhone(),password);
                iShopService.addAdmin(shop,e.getId());
                return ResultGenerator.genSuccessResult("申请成功");
            }else if(state==2){
                return ResultGenerator.genSuccessResult("拒绝成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genFailResult("申请失败");
        }
        return null;
    }

    @PostMapping(SHOP_REFUSE_URL)
    public NetResult refuse(@RequestBody RequestData requestData){
        try {
            int state = requestData.getState(); // 获取状态
            Shop shop = requestData.getShop(); // 获取店铺信息
            iShopService.updateState(shop.getId(),state);
            return ResultGenerator.genSuccessResult("拒绝成功");
        }catch (Exception e){
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
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_SHOP_ERROR,ErrorMessage.REMOVE_SHOP_ERROR+e.getMessage());
        }
    }
}
