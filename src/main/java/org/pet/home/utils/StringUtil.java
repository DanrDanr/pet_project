package org.pet.home.utils;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/1
 **/
public class StringUtil {

    /**
     * 判断字符串 s 是否为空 或者为null
     * @param s
     * @return
     */
    public static boolean isEmpty(String s){
        return s == null || s.isEmpty() || s.equals("null");
    }
    public static boolean state(int s){
        return s ==0 || s==1;
    }
    public static boolean isNullOrNullStr(String s){
        return s == null || s.equals("null");
    }
}
