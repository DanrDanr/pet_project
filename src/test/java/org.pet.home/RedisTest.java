package org.pet.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    public void getRedis() throws JsonProcessingException {
        String userString = "User(id=13, username=Danr, email=2286684456@qq.com, phone=13367149414, password=e10adc3949ba59abbe56e057f20f883e, state=0, age=0, createtime=null, headImg=null, token=null)";
        // 通过字符串处理获取用户的 id
        int startIndex = userString.indexOf("id=") + 3; // 获取 id 的起始位置
        int endIndex = userString.indexOf(",", startIndex); // 获取 id 的结束位置
        String idString = userString.substring(startIndex, endIndex); // 提取 id 的字符串表示
        Long userId = Long.parseLong(idString); // 将 id 字符串转换为 Long 类型
        System.out.println(userId);
    }
}
