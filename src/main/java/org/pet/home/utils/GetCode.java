package org.pet.home.utils;

import org.springframework.stereotype.Component;

import java.util.Random;


/**
 * @description:
 * @author: 22866
 * @date: 2023/11/9
 **/
@Component
public class GetCode {

    public GetCode() {
    }

    public String sendCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            code.append(random.nextInt(10)); // 生成0到9之间的随机整数并添加到字符串中
        }
        return code.toString();
    }
}
