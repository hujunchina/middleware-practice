package com.hujunchina.middleware.server.service.SpringEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

// 消息生产者
@Component
public class EventProducer {
    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    public void sendMsg() throws Exception{
        // 产生一个消息
        LoginEvent event = new LoginEvent(this, "hujun-login",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                "127.0.0.1");
        // 发送消息
        publisher.publishEvent(event);
        log.info("Spring-事件驱动模型-消息已发送-:{}", event);
    }
}
