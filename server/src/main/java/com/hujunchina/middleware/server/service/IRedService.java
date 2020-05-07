package com.hujunchina.middleware.server.service;

import com.hujunchina.middleware.server.entity.RedPacket;

import java.math.BigDecimal;
import java.util.List;

public interface IRedService {
//    只对数据写入数据库
    void recordRedPacket(RedPacket redPacket, String redID, List<Integer> list) throws Exception;
    void recordRobRedPacket(Integer uid, String redID, BigDecimal amount) throws Exception;
}
