package org.pet.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/27
 **/
@Component
public class MyConsumer {
    private Logger logger = LoggerFactory.getLogger(MyConsumer.class);
    /**
     * 监听队列:当队列有消息则监听器工作 处理接收到的消息
     */
    @RabbitListener(queues="my_boot_fanout_queue")
    public void process(Message message){
        byte[]body=message.getBody();
        String messageStr = new String(body, StandardCharsets.UTF_8);
        logger.info("接收到的消息: {}", messageStr);
    }
}
