package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/20
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void addTest(){
        Order order = new Order();
        String s = UUID.randomUUID().toString();
        order.setOrderNumber(s);
        order.setCreateTime(System.currentTimeMillis());
        order.setUser_id(14);
        order.setShop_id(49);
        order.setAmount(BigDecimal.valueOf(98.3));
        order.setPetCommodity_id(35);
        orderMapper.add(order);
    }
}
