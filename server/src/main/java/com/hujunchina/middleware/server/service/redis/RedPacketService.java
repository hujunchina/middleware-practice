package com.hujunchina.middleware.server.service.redis;

import com.hujunchina.middleware.server.entity.RedPacket;
import com.hujunchina.middleware.server.service.IRedPacketService;
import com.hujunchina.middleware.server.service.IRedService;
import com.hujunchina.middleware.server.utils.RedPacketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RedPacketService implements IRedPacketService {
    private static final Logger log = LoggerFactory.getLogger(RedPacketService.class);
    private static final String keyPrefix = "redis:red:packet";
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IRedService redService;

    @Override
    public String handOut(RedPacket redPacket) throws Exception {
//        检查红包数量合理性
        if(redPacket.getTotal()>0 && redPacket.getAmount()>0){
//            分割红包
            List<Integer> list = RedPacketUtils.divideRedPacket(redPacket.getAmount(),redPacket.getTotal());
//           红包唯一标识
            String timeStamp = String.valueOf(System.nanoTime());
            String redID = new StringBuffer(keyPrefix).append(redPacket.getUid()).append(":").append(timeStamp).toString();
            log.info("redID: {}", redID);
//            红包数据插入到 redis
            redisTemplate.opsForList().leftPushAll(redID, list);
//            红包总个数插入到 redis
            String redTotalKey = redID+":total";
            redisTemplate.opsForValue().set(redTotalKey, redPacket.getTotal());
//            红包数据写入到 数据库中
            redService.recordRedPacket(redPacket, redID, list);
            return redID;
        }else{
            throw new Exception("分发红包，参数不合法");
        }
    }

    @Override
    public BigDecimal rob(Integer uid, String redID) throws Exception {
        return null;
    }

}
