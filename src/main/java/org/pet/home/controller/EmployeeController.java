package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pet.home.common.ErrorMessage;
import org.pet.home.entity.Department;
import org.pet.home.entity.Employee;
import org.pet.home.service.IDepartmentService;
import org.pet.home.service.IEmployeeService;
import org.pet.home.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private static final String EMPLOYEE_ADD_URL = "/add";
    private static final String EMPLOYEE_DELETE_URL = "/delete";
    private static final String EMPLOYEE_UPDATE_URL = "/update";
    private static final String EMPLOYEE_LIST_URL = "/list";

    private RedisTemplate redisTemplate;

    private IDepartmentService iDepartmentService;
    private IEmployeeService iEmployeeService;

    @Autowired
    public EmployeeController(IDepartmentService iDepartmentService, IEmployeeService iEmployeeService,
                              RedisTemplate redisTemplate) {
        this.iDepartmentService = iDepartmentService;
        this.iEmployeeService = iEmployeeService;
        this.redisTemplate = redisTemplate;

    }

    @ApiOperation("添加员工")
    @PostMapping(EMPLOYEE_ADD_URL)
    public NetResult add(@RequestBody Employee employee) {
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
        employee.setPassword(MD5Util.MD5Encode(employee.getPassword(), "UTF-8"));

        Department department = iDepartmentService.find(employee.getDid());
        if (department == null) {
            return ResultGenerator.genErrorResult(NetCode.DEPARTMENT_ID_INVALID, ErrorMessage.DEPARTMENT_ID_INVALID);
        }
        Employee e = iEmployeeService.checkPhone(employee.getPhone());
        if (e != null) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_OCCUPANCY, ErrorMessage.PHONE_OCCUPANCY);
        }
        boolean result = iEmployeeService.add(employee);
        if (!result) {
            return ResultGenerator.genFailResult("添加员工失败");
        }
        return ResultGenerator.genSuccessResult(employee);
    }

    @GetMapping(EMPLOYEE_LIST_URL)
    public NetResult list() {
        List< Employee > employees = iEmployeeService.list();
        return ResultGenerator.genSuccessResult(employees);
    }

    @PostMapping(EMPLOYEE_DELETE_URL)
    public NetResult delete(@RequestBody Map< String, String > data) {
        Long id = Long.valueOf(data.get("id"));
        try {
            iEmployeeService.remove(id);
            return ResultGenerator.genSuccessResult(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_EMPLOYEE_ERROR, ErrorMessage.REMOVE_EMPLOYEE_ERROR + e.getMessage());
        }
    }

    @PostMapping(EMPLOYEE_UPDATE_URL)
    public NetResult update(@RequestBody Employee employee) {
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
}
