package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.Employee;
import org.pet.home.entity.Shop;
import org.pet.home.service.IEmployeeService;
import org.pet.home.service.IShopService;
import org.pet.home.utils.*;
import org.springframework.web.bind.annotation.*;

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

    private IShopService iShopService;
    private IEmployeeService iEmployeeService;

    public ShopController(IShopService iShopService,IEmployeeService iEmployeeService) {
        this.iShopService = iShopService;
        this.iEmployeeService = iEmployeeService;

    }

    @ApiOperation("注册店铺")
    @PostMapping("/register")
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
        if (StringUtil.isEmpty(shop.getLogo())) {
            return ResultGenerator.genErrorResult(NetCode.LOGO_NULL, ErrorMessage.LOGO_NULL);
        }
        if(shop.getAdmin() == null){
            Employee employee = new Employee();
            employee.setId(0L);
            shop.setAdmin(employee);
        }
        shop.setRegisterTime(System.currentTimeMillis());
        int count = iShopService.add(shop);
        if(count!=1){
            return ResultGenerator.genFailResult("添加店铺失败");
        }
        return ResultGenerator.genSuccessResult(shop);
    }

    @GetMapping("/list")
    public NetResult list(){
        List<Shop> shops = iShopService.list();
        return ResultGenerator.genSuccessResult(shops);
    }
    @PostMapping("/paginationList")
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
        ShopUtil shopUtil = new ShopUtil();
        shopUtil.shops=shops;
        shopUtil.total = count;
        //一般分页的返回数据基本上就是{ data:[], count:所有的数据的size}
        //为什么要给count给别人。别人可以通过count取算有多少页，这个页不是后台算的
        return ResultGenerator.genSuccessResult(shopUtil);
    }

    @PostMapping("/edit")
    public NetResult edit(@RequestBody Shop shop){
        try {
            iShopService.update(shop);
            return ResultGenerator.genSuccessResult("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @PostMapping("/pass")
    public NetResult pass(@RequestBody RequestData requestData){
        try {
            int state = requestData.getState(); // 获取状态
            Shop shop = requestData.getShop(); // 获取店铺信息
            iShopService.updateState(shop.getId(),state);
            return ResultGenerator.genSuccessResult("申请成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genFailResult("申请失败");
        }
    }

    @PostMapping("/refuse")
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

    @PostMapping("/remove")
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
