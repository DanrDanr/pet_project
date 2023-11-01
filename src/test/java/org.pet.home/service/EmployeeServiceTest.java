package org.pet.home.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Employee;
import org.pet.home.service.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/1
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeServiceTest {
    @Autowired
    private EmployeeService employeeService;

    @Test
    public void deleteTest(){
        employeeService.remove(15L);
    }

    @Test
    public void updateTest(){
        Employee e = new Employee();
        e.setId(3L);
        e.setUsername("wula");
        e.setEmail("888888@qq.com");
        e.setPhone("13339913684");
        e.setState(0);
        e.setPassword("123456");
        e.setAge(25);
        e.setDid(3L);
        employeeService.update(e);
//        System.out.println(e.getDepartment());
    }
}
