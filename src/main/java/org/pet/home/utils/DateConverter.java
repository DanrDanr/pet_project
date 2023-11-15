package org.pet.home.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/14
 **/
public class DateConverter {
    public static long TimeConversion(String time){
        // 设置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        long timestamp = 0;
        try {
            // 将字符串日期转换为Date对象
            Date date = dateFormat.parse(time);

            // 获取时间戳
            timestamp = date.getTime();

            // 打印时间戳
            System.out.println("时间戳: " + timestamp);
        } catch (Exception e) {
            System.out.println("日期格式不正确，请输入类似“xxxx年xx月xx日”的日期格式。");
        }
        return timestamp;
    }
}
