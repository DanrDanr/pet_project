package org.pet.home.service;

import org.apache.ibatis.annotations.Select;
import org.pet.home.entity.Order;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/20
 **/
public interface OrderService {
    int add(Order order);
    List<Order> listUnpaidOrders(int status, long user_id);
    List<Order> unpaidOrder();
    int cancelOrder(int status,String orderNumber);
}
