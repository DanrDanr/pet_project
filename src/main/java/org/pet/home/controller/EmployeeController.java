package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.Department;
import org.pet.home.entity.Employee;
import org.pet.home.service.IDepartmentService;
import org.pet.home.service.IEmployeeService;
import org.pet.home.utils.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/1
 **/
@Api(tags = "员工接口文档")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private IDepartmentService iDepartmentService;
    private IEmployeeService iEmployeeService;

    public EmployeeController(IDepartmentService iDepartmentService, IEmployeeService iEmployeeService) {
        this.iDepartmentService = iDepartmentService;
        this.iEmployeeService = iEmployeeService;
    }

    @ApiOperation("添加员工")
    @PostMapping("/add")
    public NetResult add( @RequestBody Employee employee) {
        if (StringUtil.isEmpty(employee.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        if (StringUtil.isEmpty(employee.getEmail())) {
            return ResultGenerator.genErrorResult(NetCode.EMAIL_NULL, ErrorMessage.EMAIL_NULL);
        }
        if (StringUtil.isEmpty(employee.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL, ErrorMessage.USERNAME_NULL);
        }
        if (StringUtil.isEmpty(employee.getPassword())) {
            //如果密码为空设置默认密码
            employee.setPassword(MD5Util.MD5Encode("123456", "UTF-8"));
        }
        //给密码加密
        employee.setPassword(MD5Util.MD5Encode(employee.getPassword(),"UTF-8"));

        Department department = iDepartmentService.find(employee.getDid());
        if (department == null) {
            return ResultGenerator.genErrorResult(NetCode.DEPARTMENT_ID_INVALID, ErrorMessage.DEPARTMENT_ID_INVALID);
        }
        Employee e = iEmployeeService.checkPhone(employee.getPhone());
        if(e!=null){
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPANCY, ErrorMessage.PHONE_OCCUPANCY);
        }
        boolean result = iEmployeeService.add(employee);
        if (!result) {
            return ResultGenerator.genFailResult("添加员工失败");
        }
        return ResultGenerator.genSuccessResult(employee);
    }

    @GetMapping("/list")
    public NetResult list(){
        List<Employee>employees = iEmployeeService.list();
        return ResultGenerator.genSuccessResult(employees);
    }

    @PostMapping("/delete")
    public NetResult delete(@RequestBody Map<String, String >data) {
        Long id = Long.valueOf(data.get("id"));
        try {
            iEmployeeService.remove(id);
            return ResultGenerator.genSuccessResult(id);
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_EMPLOYEE_ERROR,ErrorMessage.REMOVE_EMPLOYEE_ERROR+e.getMessage());
        }
    }

    @PostMapping("/update")
    public NetResult update(@RequestBody Employee employee){
        if (StringUtil.isEmpty(employee.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_NULL, ErrorMessage.PHONE_NULL);
        }
        if (StringUtil.isEmpty(employee.getEmail())) {
            return ResultGenerator.genErrorResult(NetCode.EMAIL_NULL, ErrorMessage.EMAIL_NULL);
        }
        if (StringUtil.isEmpty(employee.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL, ErrorMessage.USERNAME_NULL);
        }
        if (StringUtil.isEmpty(employee.getPassword())) {
            //如果密码为空设置默认密码
            employee.setPassword(MD5Util.MD5Encode("123456", "UTF-8"));
        }
        Department department = iDepartmentService.find(employee.getDid());
        if (department == null) {
            return ResultGenerator.genErrorResult(NetCode.DEPARTMENT_ID_INVALID, ErrorMessage.DEPARTMENT_ID_INVALID);
        }
        Employee e = iEmployeeService.checkPhone(employee.getPhone());

        iEmployeeService.update(employee);
        return ResultGenerator.genSuccessResult(employee);
    }

    @PostMapping("/login")
    public NetResult login(@RequestBody Employee employee){
        if (StringUtil.isEmpty(employee.getUsername())){
            return ResultGenerator.genErrorResult(NetCode.USERNAME_NULL,ErrorMessage.USERNAME_NULL);
        }
        if (StringUtil.isEmpty(employee.getPassword())){
            return ResultGenerator.genErrorResult(NetCode.USER_PASSWORD_NULL,ErrorMessage.USER_PASSWORD_NULL);
        }
        employee.setPassword(MD5Util.MD5Encode(employee.getPassword(),"utf-8"));
        Employee e = iEmployeeService.login(employee);
        if(e!=null){
            return ResultGenerator.genSuccessResult("登录成功");
        }
        return ResultGenerator.genFailResult("账号或密码错误");
    }
}
