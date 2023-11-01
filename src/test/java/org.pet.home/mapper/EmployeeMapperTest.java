package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Employee;
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
public class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    public void addEmployeeTest(){
        Employee e = new Employee();
        e.setUsername("wula");
        e.setEmail("888888@qq.com");
        e.setPhone("13339913684");
        e.setState(0);
        e.setPassword("666666");
        e.setAge(25);
        e.setDid(3L);
        System.out.println(employeeMapper.add(e));

    }
}
