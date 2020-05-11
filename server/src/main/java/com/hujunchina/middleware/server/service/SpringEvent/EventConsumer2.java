package com.hujunchina.middleware.server.service.SpringEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component //加入IOC容器
@EnableAsync //允许异步执行
public class EventConsumer2 implements ApplicationListener<LoginEvent> {
    // 日志
    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @Override
    @Async
    public void onApplicationEvent(LoginEvent loginEvent) {
        log.info("Spring-事件驱动模型2-收到消息:{}", loginEvent);
        // 收到消息时的异步处理，如写入数据库等
    }
}
