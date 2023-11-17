package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate redisTemplate;

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
        long user_id =13;
        User user = userMapper.findById(user_id);
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, user, 30, TimeUnit.MINUTES);
        User user1 = (User) redisTemplate.opsForValue().get(token);
        System.out.println(user1);
    }
}
