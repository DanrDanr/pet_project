package org.pet.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Employee;
import org.pet.home.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/26
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void setRedis(){
        redisTemplate.opsForValue().set("name","花花");

        redisTemplate.opsForValue().set("password","123456",60, TimeUnit.SECONDS);
        System.out.println("缓存成功");
    }

    @Test
    public void getRedis() {
     Employee s = (Employee) redisTemplate.opsForValue().get("df5a8009-ca85-418a-83ff-e08cc41bc8b7");
        System.out.println(s);
    }
}
