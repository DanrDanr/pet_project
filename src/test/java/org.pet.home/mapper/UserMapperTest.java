package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.User;
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
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void loginTest(){

        System.out.println(userMapper.login("13367149414","e10adc3949ba59abbe56e057f20f883e"));
    }

    @Test
    public void register(){
        User user = new User();
        user.setPhone("13594323");
        user.setPassword("123456");
        userMapper.add(user);
    }

    @Test
    public void findByIdTest(){
        int user_id =11;
        System.out.println(userMapper.findById((long) user_id));
    }
}
