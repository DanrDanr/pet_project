package org.pet.home.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/8
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void userLoginTest(){

    }
}
