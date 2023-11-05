package org.pet.home.entity;

import lombok.Data;


/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
@Data
public class Shop {
    private Long id;
    private String name;
    private String tel;
    private Long registerTime;
    private int state = 0;//0待审核状态 等待管理员审核 1审核成功 就要生成对应admin账号
    private String address;
    private String logo;
    private Employee admin;
}
