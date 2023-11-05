package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
            return ResultGenerator.genErrorResult(NetCode.SHOP_NAME_INVALID, "店铺名不能为空");
        }
        if (StringUtil.isEmpty(shop.getTel())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        if (StringUtil.isEmpty(shop.getAddress())) {
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_INVALID, "地址不能为空");
        }
        if (StringUtil.isEmpty(shop.getLogo())) {
            return ResultGenerator.genErrorResult(NetCode.LOGO_INVALID, "LOGO不能为空");
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
        int offset = (page - 1) * pageSize; // 计算偏移量
        int count = iShopService.count(); // 获取总记录数
        List<Shop> shops = iShopService.paginationList(offset,pageSize);
        ShopUtil shopUtil = new ShopUtil();
        shopUtil.shops=shops;
        if(count % pageSize > 0) {
            shopUtil.total = (int) Math.ceil((double) count / pageSize);
        } else {
            shopUtil.total = count / pageSize;
        }
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
            return ResultGenerator.genErrorResult(NetCode.REMOVE_SHOP_ERROR,"删除失败！"+e.getMessage());
        }
    }
}