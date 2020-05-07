package com.hujunchina.middleware.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedPacketUtils {
    /**
     * 二倍均值法分红包
     * @param totalAmount 总金额
     * @param totalPeopleNum 总人数
     * @return 分的份数列表
     */
    public static List<Integer> divideRedPacket(Integer totalAmount, Integer totalPeopleNum){
        List<Integer> amountList = new ArrayList<>();
        if(totalAmount>0 && totalPeopleNum>0){
            Integer restAmount = totalAmount;
            Integer restPeopleNum = totalPeopleNum;
            Random random = new Random();
            for(int i=0; i<totalPeopleNum-1; i++){
                int amount = random.nextInt(restAmount/restPeopleNum*2-1)+1;
                restAmount -= amount;
                restPeopleNum--;
                amountList.add(amount);
            }
            amountList.add(restAmount);
        }
        return amountList;
    }
}
