package org.pet.home.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.service.impl.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopServiceTest {
    @Autowired
    private ShopService shopService;

    @Test
    public void listTest(){

        System.out.println(shopService.list());
    }
}
