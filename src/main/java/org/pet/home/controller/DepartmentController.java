package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.pet.home.entity.Department;
import org.pet.home.service.IDepartmentService;
import org.pet.home.utils.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/27
 **/
@Api(tags = "部门接口文档")
@RestController
@RequestMapping("/department")
public class DepartmentController {

    private IDepartmentService iDepartmentService;

    public DepartmentController(IDepartmentService iDepartmentService){
        this.iDepartmentService = iDepartmentService;
    }


    @ApiOperation("添加部门")
    @PostMapping("/create")
    public NetResult add(@RequestBody Department  department){
        try {
            iDepartmentService.add(department);
            return ResultGenerator.genSuccessResult(department);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.CREATE_DEPARTMENT_ERROR, "创建对象失败");
        }
    }

    @ApiOperation("添加部门")
    @PostMapping("/add")
    public NetResult add(@RequestBody  DepartmentParam  departmentParam){
        System.out.println("添加"+departmentParam);
        try {
            Department department = new Department();
            department.setSn(departmentParam.getSn());
            department.setName(departmentParam.getName());

            long parent_id = departmentParam.getParentId();
            Department parentDepartment = new Department();
            parentDepartment.setId(parent_id);
            department.setParent(parentDepartment);

            iDepartmentService.add(department);
            return ResultGenerator.genSuccessResult(department);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.CREATE_DEPARTMENT_ERROR, "创建对象失败");
        }
    }

    @PostMapping("/delete")
    public NetResult delete(Long id){
        try {
            iDepartmentService.remove(id);
            return ResultGenerator.genSuccessResult(id);
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_DEPARTMENT_ERROR,"删除部门失败！"+e.getMessage());
        }
    }

    @PostMapping("/update")
    public NetResult update(@RequestBody Department department){
        try {
            iDepartmentService.update(department);
            return ResultGenerator.genSuccessResult();
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.UPDATE_DEPARTMENT_ERROR,"更新部门失败！"+e.getMessage());
        }
    }

    @GetMapping("/get")
    public NetResult get(Long id){
        Department department = iDepartmentService.find(id);
        return ResultGenerator.genSuccessResult(department);
    }

    @GetMapping("/list")
    public NetResult list(){
        List<Department> department = iDepartmentService.findAll();
        return ResultGenerator.genSuccessResult(department);
    }

    @GetMapping("/tree")
    public NetResult tree(){
        List<Department> department = iDepartmentService.getDepartmentTreeData();
        return ResultGenerator.genSuccessResult(department);
    }

    @GetMapping("/type")
    public NetResult getExChoiceList(){
        List< Extype > extypes = iDepartmentService.findTypes();
        System.out.println(extypes);
        return ResultGenerator.genSuccessResult(extypes);
    }
}
