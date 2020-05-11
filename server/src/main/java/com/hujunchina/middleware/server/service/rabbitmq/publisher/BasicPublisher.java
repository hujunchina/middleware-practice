package com.hujunchina.middleware.server.service.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BasicPublisher {
    private static final Logger log = LoggerFactory.getLogger(BasicPublisher.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    public void sendMsg(String msg){
        if(!Strings.isNullOrEmpty(msg)){
            try{
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("mq.basic.info.exchange.name"));
                rabbitTemplate.setRoutingKey(env.getProperty("mq.basic.info.routing.key.name"));
                Message message = MessageBuilder.withBody(msg.getBytes("utf-8")).build();
                rabbitTemplate.convertAndSend(msg);
                log.info("-基本消息模型-生产者-发送消息:{}", msg);
            }catch (Exception e){
                log.error("-基本消息模型-生产者-发送消息异常:{}", e.fillInStackTrace());
            }
        }
    }

}
