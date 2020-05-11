package com.hujunchina;

import com.hujunchina.middleware.server.MainApplication;
import com.hujunchina.middleware.server.service.SpringEvent.EventProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 单元测试类，用于测试redis是否正常运行
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class SpringEventTest {
    @Autowired
    private EventProducer producer;

    @Test
    public void testSendMsg() throws Exception{
        for (int i = 0; i < 5; i++) {
            producer.sendMsg();
        }
    }
}
