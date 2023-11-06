package org.pet.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.pet.home.common.ErrorMessage;
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

    private static final String DEPARTMENT_CREATE_URL = "/create";
    private static final String DEPARTMENT_ADD_URL = "/add";
    private static final String DEPARTMENT_DELETE_URL = "/delete";
    private static final String DEPARTMENT_UPDATE_URL = "/update";
    private static final String DEPARTMENT_GET_URL = "/get";
    private static final String DEPARTMENT_LIST_URL = "/list";
    private static final String DEPARTMENT_TREE_URL = "/tree";
    private static final String DEPARTMENT_NAME_TYPE_URL = "/type";

    private IDepartmentService iDepartmentService;

    public DepartmentController(IDepartmentService iDepartmentService){
        this.iDepartmentService = iDepartmentService;
    }


    @ApiOperation("添加部门")
    @PostMapping(DEPARTMENT_CREATE_URL)
    public NetResult add(@RequestBody Department  department){
        try {
            iDepartmentService.add(department);
            return ResultGenerator.genSuccessResult(department);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.CREATE_DEPARTMENT_ERROR, ErrorMessage.CREATE_DEPARTMENT_ERROR);
        }
    }

    @ApiOperation("添加部门")
    @PostMapping(DEPARTMENT_ADD_URL)
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
            return ResultGenerator.genErrorResult(NetCode.CREATE_DEPARTMENT_ERROR, ErrorMessage.CREATE_DEPARTMENT_ERROR);
        }
    }

    @PostMapping(DEPARTMENT_DELETE_URL)
    public NetResult delete(Long id){
        try {
            iDepartmentService.remove(id);
            return ResultGenerator.genSuccessResult(id);
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_DEPARTMENT_ERROR,ErrorMessage.REMOVE_DEPARTMENT_ERROR+e.getMessage());
        }
    }

    @PostMapping(DEPARTMENT_UPDATE_URL)
    public NetResult update(@RequestBody Department department){
        try {
            iDepartmentService.update(department);
            return ResultGenerator.genSuccessResult();
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.UPDATE_DEPARTMENT_ERROR,ErrorMessage.UPDATE_DEPARTMENT_ERROR+e.getMessage());
        }
    }

    @GetMapping(DEPARTMENT_GET_URL)
    public NetResult get(Long id){
        Department department = iDepartmentService.find(id);
        return ResultGenerator.genSuccessResult(department);
    }

    @GetMapping(DEPARTMENT_LIST_URL)
    public NetResult list(){
        List<Department> department = iDepartmentService.findAll();
        return ResultGenerator.genSuccessResult(department);
    }

    @GetMapping(DEPARTMENT_TREE_URL)
    public NetResult tree(){
        List<Department> department = iDepartmentService.getDepartmentTreeData();
        return ResultGenerator.genSuccessResult(department);
    }

    @GetMapping(DEPARTMENT_NAME_TYPE_URL)
    public NetResult getExChoiceList(){
        List< Extype > extypes = iDepartmentService.findTypes();
        System.out.println(extypes);
        return ResultGenerator.genSuccessResult(extypes);
    }
}
