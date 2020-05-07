package com.hujunchina;

import com.hujunchina.middleware.server.utils.RedPacketUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
