package com.hujunchina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hujunchina.middleware.server.MainApplication;
import com.hujunchina.middleware.server.service.rabbitmq.publisher.BasicPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 单元测试类，用于测试redis是否正常运行
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class BasicRabbitMQTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BasicPublisher basicPublisher;
    @Test
    public void test1() throws Exception{
//        不要有包含中文的标点等
        String msg = new String("BasicRabbitMQTest-hujun");
        basicPublisher.sendMsg(msg);
    }
}
