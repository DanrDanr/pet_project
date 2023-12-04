package org.pet.home.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/27
 **/
@Configuration
public class MyRabbitMQConfig {
    private static String EXCHANGE_NAME = "my_boot_fanout_exchange";
    private static String QUEUE_NAME = "my_boot_fanout_queue";

    /**
     * 声明交换机
     */
    @Bean
    public FanoutExchange exchange(){
        /**
         * 参数 第一个交换机名字  durable是否持久化 autoDelete是否自动删除
         */
        return new FanoutExchange(EXCHANGE_NAME,true,false);
    }
    /**
     * 声明队列
     */
    @Bean
    public Queue queue(){
        /**
         * 第一个参数队列名字  durable是否持久化
         *  exclusive是否独占 autoDelete是否自动删除
         */
        return new Queue(QUEUE_NAME,true,false,false);
    }
    /**
     * 声明绑定关系
     */
    @Bean
    public Binding queueBinding(Queue queue,FanoutExchange fanoutExchange){
        //把队列绑定到交换机中
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
}
