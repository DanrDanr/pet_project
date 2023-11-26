package org.pet.home.service.impl;

import org.pet.home.entity.Order;
import org.pet.home.mapper.OrderMapper;
import org.pet.home.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/20
 **/
@Service
public class IOrderService implements OrderService {
    private OrderMapper orderMapper;

    @Autowired
    public IOrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public int add(Order order) {
        return orderMapper.add(order);
    }

    @Override
    public List< Order > listUnpaidOrders(int status, long user_id) {
        return orderMapper.listUnpaidOrders(status, user_id);
    }

    @Override
    public List< Order > unpaidOrder() {
        return orderMapper.unpaidOrder();
    }

    @Override
    public int cancelOrder(int status, String orderNumber) {
        return orderMapper.cancelOrder(status, orderNumber);
    }
}
