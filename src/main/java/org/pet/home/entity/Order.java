package org.pet.home.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/20
 **/
@Data
public class Order implements Serializable {
private long id;
private String orderNumber;//商户网站唯一订单号
private BigDecimal amount;//支付金额
private int status=0;//订单状态 0未支付 1支付 2取消订单
private long createTime;//订单创建时间
private long updateTime=0;//订单完成时间
private long user_id;//用户id
private long shop_id;//店铺id
private long petCommodity_id;//商品id
}
