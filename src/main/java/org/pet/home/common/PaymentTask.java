package org.pet.home.common;

import org.pet.home.entity.Order;
import org.pet.home.service.impl.IOrderService;
import org.pet.home.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 定时任务
 * 通过定时任务关闭订单，是一种成本很低，实现也很容易的方案。
 * 通过简单的几行代码，写一个定时任务，定期扫描数据库中的订单，如果时间过期，就将其状态更新为关闭即可。
 * 缺点：
 * 时间可能不够精确。由于定时任务扫描的间隔是固定的，所以可能造成一些订单已经过期了一段时间才被扫描到，订单关闭的时间比正常时间晚一些。
 * 增加了数据库的压力。随着订单的数量越来越多，扫描的成本也会越来越大，执行时间也会被拉长，可能导致某些应该被关闭的订单迟迟没有被关闭。
 * @author: 22866
 * @date: 2023/11/20
 **/
//@Component
public class PaymentTask {
    private IOrderService orderService;
    private RedisTemplate redisTemplate;

    @Autowired
    public PaymentTask(IOrderService orderService, RedisTemplate redisTemplate) {
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedDelay = 1000)  // 每秒执行一次
    public void cancelUnpaidOrders() {
        // 查询所有未支付的订单
        List< Order > orders = orderService.unpaidOrder();
        // 遍历订单列表，检查支付截止时间并取消超时未支付的订单
        for (Order order:orders) {
            String orderNumber = order.getOrderNumber();
            Long expireTime = (Long) redisTemplate.opsForValue().get(RedisKeyUtil.getOrderRedisKey(orderNumber));
            if (expireTime != null) {  // 判断expireTime是否为空
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp > expireTime) {
                    //如果当前时间大于订单过期时间则取消订单
                    orderService.cancelOrder(2, order.getOrderNumber());
                    // 清除支付截止时间
                    redisTemplate.delete(RedisKeyUtil.getOrderRedisKey(orderNumber));
                }
            }
        }
    }

}

