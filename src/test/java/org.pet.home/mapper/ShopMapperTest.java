package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Employee;
import org.pet.home.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopMapperTest {

    @Autowired
    private ShopMapper shopMapper;

    @Test
    public void addShopTest(){
        Shop shop = new Shop();
        shop.setName("电子商务");
        shop.setTel("13339913684");
        shop.setRegisterTime(new Date().getTime());
        shop.setAddress("街道口");
        shop.setState(0);
        Employee employee = new Employee();
        employee.setId(1L);
        shop.setAdmin(employee);
        shop.setId(1L);

        shopMapper.add(shop);
        System.out.println(shop);
    }

    @Test
    public void listTest(){
        System.out.println(shopMapper.list());
    }
}
