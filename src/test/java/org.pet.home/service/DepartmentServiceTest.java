package org.pet.home.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Department;
import org.pet.home.entity.Employee;
import org.pet.home.service.impl.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLOutput;
import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/29
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void add(){
        Department department = new Department();
        department.setSn("AD");
        department.setName("广告部2组");

        Employee employee = new Employee();
        employee.setId(1L);
        department.setManager(employee);
        department.setParent(new Department());

//        departmentService.add(department);

        System.out.println(department);
    }

    @Test
    public void testTree() throws JsonProcessingException {
        List< Department >departmentList = departmentService.getDepartmentTreeData();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(departmentList);
        System.out.println(json);
    }

    @Test
    public void listTest(){
        System.out.println(departmentService.getDepartmentTreeData());
    }
}
