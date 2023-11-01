package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pet.home.entity.Department;
import org.pet.home.entity.Employee;
import org.pet.home.service.IDepartmentService;
import org.pet.home.service.IEmployeeService;
import org.pet.home.utils.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public  EmployeeController(IDepartmentService iDepartmentService,IEmployeeService iEmployeeService){
        this.iDepartmentService = iDepartmentService;
        this.iEmployeeService = iEmployeeService;
    }

    @ApiOperation("添加员工")
    @PostMapping("/add")
    public NetResult add(@RequestBody Employee employee){
     if(StringUtil.isEmpty(employee.getPhone())){
         return  ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不能为空");
     }
        if(StringUtil.isEmpty(employee.getEmail())){
            return  ResultGenerator.genErrorResult(NetCode.EMAIL_INVALID,"邮箱不能为空");
        }
        if(StringUtil.isEmpty(employee.getUsername())){
            return  ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID,"用户名不能为空");
        }
        if(StringUtil.isEmpty(employee.getPassword())){
            //如果密码为空设置默认密码
            employee.setPassword(MD5Util.MD5Encode("123456","UTF-8"));
        }
        Department department = iDepartmentService.find(employee.getDid());
        if(department==null){
            return  ResultGenerator.genErrorResult(NetCode.DEPARTMENT_ID_INVALID,"部门id异常");
        }
        boolean result = iEmployeeService.add(employee);
        if(!result){
            return  ResultGenerator.genFailResult("添加员工失败");
        }
     return ResultGenerator.genSuccessResult(employee);
    }

    public void delete(){

    }


}
