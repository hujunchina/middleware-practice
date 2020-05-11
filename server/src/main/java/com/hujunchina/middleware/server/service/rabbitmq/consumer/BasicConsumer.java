package com.hujunchina.middleware.server.service.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BasicConsumer {
    private static final Logger log = LoggerFactory.getLogger(BasicConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment env;

    @RabbitListener(queues = "${mq.basic.info.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload String msg){
        try{
           System.out.println("消费者");
           log.info("-基本消息模型-消费者-接收消息:{}", msg);
        }catch (Exception e){
           log.error("-基本消息模型-消费者-接收消息异常:{}", e.fillInStackTrace());
        }
    }
}
