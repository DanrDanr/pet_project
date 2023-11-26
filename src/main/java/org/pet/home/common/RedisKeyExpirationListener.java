package org.pet.home.common;

import org.pet.home.service.impl.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/21
 **/
//@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private IOrderService orderService;
    private Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    @Autowired
    public RedisKeyExpirationListener(RedisMessageListenerContainer redisMessageListenerContainer,
                                      IOrderService orderService) {
        super(redisMessageListenerContainer);
        this.orderService=orderService;
    }

    /**
     * 针对 redis 数据失效事件，进行数据处理
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 拿到key
        logger.info(message.toString());
        logger.info( new String(pattern));
        String expiredKey = message.toString();
        String[] parts = expiredKey.split(":");
        if (parts.length == 2) {
            String orderId = parts[1].trim();
            logger.info(orderId);
            //把对应订单号状态修改成取消状态
            orderService.cancelOrder(2,orderId);
        } else {

        }
    }
}
