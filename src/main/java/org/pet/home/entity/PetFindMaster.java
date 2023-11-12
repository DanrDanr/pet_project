package org.pet.home.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/11
 **/
@Data
public class PetFindMaster {
    private Long id;
    private String petName;
    private int sex;//0是母的 1是公的
    private String address;
    private Long createTime;
    private BigDecimal price;//存价格以免丢失精度
    private int birth;
    private int isInoculation;//0是没接种 1是接种了
    private int state;//0是上架了 1是没上架
    private Long user_id;
    private Long shop_id;
    private Long employee_id;
}
