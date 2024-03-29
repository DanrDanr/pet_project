package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Department;
import org.pet.home.entity.Employee;
import org.pet.home.utils.Extype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/26
 **/

@RunWith(SpringRunner.class)
@SpringBootTest
public class DepartmentMapperTest {

  @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    public void savaDepartmentTest(){
        Department department = new Department();
        department.setSn("AD");
        department.setName("广告部2组");

        Employee employee = new Employee();
        employee.setId(1L);
        department.setManager(employee);
        department.setParent(new Department());

        departmentMapper.add(department);

        System.out.println(department);
    }

    @Test
    public void findTest() {
        Department department = departmentMapper.find(19L);
        System.out.println(department);
    }
    @Test
    public void list(){
        System.out.println(departmentMapper.findAll());
    }
    @Test
    public void typeTest(){
        List< Extype > extypes = departmentMapper.findTypes();
        System.out.println(extypes);
    }
}
