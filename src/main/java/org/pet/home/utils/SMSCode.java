package org.pet.home.utils;

import org.springframework.stereotype.Component;

import java.util.Random;


/**
 * @description:
 * @author: 22866
 * @date: 2023/11/9
 **/

public class SMSCode {

    public static final String getSMSCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            code.append(random.nextInt(10)); // 生成0到9之间的随机整数并添加到字符串中
        }
        //TODO 后期修改回去
//        code.toString()
        return "123456";
    }
}
