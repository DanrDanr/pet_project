package org.pet.home.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 员工表
 * @author: 22866
 * @date: 2023/10/26
 **/
@Data
public class Employee implements Serializable {
    /*主键*/
    private Long id;
    /* 对应部门id 关联表 department 中的id */
    private Long did;
    /*员工名称*/
    private String username;
    /*员工邮箱*/
    private String email;
    /*员工手机号码*/
    private String phone;
    /*员工密码*/
    private String password;
    /*员工年龄*/
    private int age;
    /* 部门 状态0正常，-1 停用*/
    private int state;
    /* 所属部门 */
    private Department department;
    /* 令牌方便查看 */
    private String token;
}
