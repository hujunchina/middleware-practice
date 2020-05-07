package com.hujunchina.middleware.server.service;

import com.hujunchina.middleware.server.entity.RedPacket;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

public interface IRedPacketService {
//    业务细节+redis处理
    String handOut(RedPacket redPacket) throws Exception;
    BigDecimal rob(Integer uid, String redID) throws Exception;
}
