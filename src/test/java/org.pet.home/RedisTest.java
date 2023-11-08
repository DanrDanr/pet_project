package org.pet.home;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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
    private StringRedisTemplate redisTemplate;

    @Test
    public void setRedis(){
        redisTemplate.opsForValue().set("name","花花");

        redisTemplate.opsForValue().set("password","123456",60, TimeUnit.SECONDS);
        System.out.println("缓存成功");
    }

    @Test
    public void getRedis(){
        String name = redisTemplate.opsForValue().get("name");
        String password = redisTemplate.opsForValue().get("password");

        System.out.println("缓存的name数据是："+name);
        System.out.println("缓存的password数据是："+password);
    }
}
