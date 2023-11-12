package org.pet.home.utils;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/7
 **/
@Data
public class UserParam {
    public String phone;
    public String password;
    public String code;
    public int role;//0是用户 1是商铺
}
