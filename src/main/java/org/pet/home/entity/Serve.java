package org.pet.home.entity;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
@Data
public class Serve {
    private long id;
    private String serve_name;//服务项目名字
    private double price;//价格
    private int state=0;//0是未上架 1是上架
    private int sales=0;//销量
    private long shop_id;//对应店铺id
    private long type_id;//对应服务类型id
}
