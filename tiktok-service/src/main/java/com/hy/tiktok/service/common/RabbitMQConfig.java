package com.hy.tiktok.service.common;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/28 13:11
 */

@Configuration
public class RabbitMQConfig {

    public static final String MSG_SEND_EXCHANGE = "msg_send_exchange";
    public static final String MSG_SEND_QUEUE = "msg_send_queue";
    public static final String MSG_SEND_ROUTINGKEY = "msg.send.*";
    //*：代表一个占位符  #：代表多个占位符

    @Bean(MSG_SEND_EXCHANGE)
    public Exchange msgExchange(){
        return ExchangeBuilder
                .topicExchange(MSG_SEND_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean(MSG_SEND_QUEUE)
    public Queue msgQueue(){
        return new Queue(MSG_SEND_QUEUE);
    }

    @Bean
    public Binding msgSendBinding(@Qualifier(MSG_SEND_EXCHANGE) Exchange msgExchange,
                              @Qualifier(MSG_SEND_QUEUE) Queue msgQueue){
        return BindingBuilder
                .bind(msgQueue)
                .to(msgExchange)
                .with(MSG_SEND_ROUTINGKEY)
                .noargs();
    }

}
