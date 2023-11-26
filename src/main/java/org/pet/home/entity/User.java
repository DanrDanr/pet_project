package org.pet.home.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/7
 **/
@Data
public class User implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private int state;
    private int age;
    private Long createtime;
    private String headImg;
    private String token;
    private double balance;
}
