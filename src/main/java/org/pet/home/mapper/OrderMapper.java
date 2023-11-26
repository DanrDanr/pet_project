package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.entity.Employee;
import org.pet.home.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * private long id;
 * private String orderNumber;//商户网站唯一订单号
 * private BigDecimal amount;//支付金额
 * private int status=0;//订单状态 0未支付 1支付 2 订单失效
 * private long createTime;//订单创建时间
 * private long updateTime=0;//订单完成时间
 * private long user_id;//用户id
 * private long shop_id;//店铺id
 * private long petCommodity_id;//商品id
 * @author: 22866
 * @date: 2023/11/20
 **/
@Mapper
@Repository
public interface OrderMapper {
    /**
     * 添加订单
     * @param
     * @return
     */
    @Insert("insert into t_order(orderNumber,amount,createTime,user_id,shop_id,petCommodity_id)" +
            "values(#{orderNumber},#{amount},#{createTime},#{user_id},#{shop_id},#{petCommodity_id})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(Order order);

    /**
     * 查询某用户 某状态下的订单
     * @param status
     * @param user_id
     * @return
     */
    @Select("SELECT * FROM t_order WHERE status=#{status} and user_id=#{user_id}")
    List<Order> listUnpaidOrders(int status,long user_id);

    /**
     * 查询所有订单
     * @return
     */
    @Select("SELECT * FROM t_order where status=0")
    List<Order> unpaidOrder();


    @Update("update t_order set status=#{status} where orderNumber=#{orderNumber}")
    int cancelOrder(int status,String orderNumber);
}
