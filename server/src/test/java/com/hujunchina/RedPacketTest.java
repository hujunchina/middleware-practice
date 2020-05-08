package com.hujunchina;

import com.hujunchina.middleware.server.MainApplication;
import com.hujunchina.middleware.server.utils.RedPacketUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

// 单元测试类，用于测试redis是否正常运行
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class RedPacketTest {
    @Test
    public void dividePacketTest(){
        int totalAmount = 10000;
        int totalPeopleNum = 10;
        List<Integer> redpackets = RedPacketUtils.divideRedPacket(totalAmount, totalPeopleNum);
        double total = 0.0;
        for (Integer part : redpackets){
            double money = part*1.0/100;
            total+=money;
            System.out.print(money+"yuan ");
        }
        System.out.println();
        System.out.println("totoal:"+total);
//        System.out.println(Arrays.toString(redpackets.toArray()));
    }
}
