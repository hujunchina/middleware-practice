package com.hujunchina.middleware.server.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hujunchina.middleware.server.entity.RedPacket;
import com.hujunchina.middleware.server.service.IRedPacketService;
import com.hujunchina.middleware.server.service.IRedService;
import com.hujunchina.middleware.server.utils.RedPacketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        ValueOperations vo = redisTemplate.opsForValue();
        Object obj = vo.get(redID+uid+":rob");
        if(obj!=null){
            return new BigDecimal(obj.toString());
        }
        Boolean res = click(redID);
        if(res){
//            上分布式锁
            final String lockKey = redID+uid+"-lock";
            Boolean lock = vo.setIfAbsent(lockKey, redID);
            redisTemplate.expire(lockKey, 24L, TimeUnit.HOURS);
            try {
                if(lock) {
                    Object robMoney = redisTemplate.opsForList().rightPop(redID);
                    if (robMoney != null) {
                        String redTotalKey = redID + ":total";
                        Integer currTotal = vo.get(redTotalKey) != null ? Integer.valueOf(vo.get(redTotalKey).toString()) : 0;
                        vo.set(redTotalKey, currTotal - 1);
                        BigDecimal result = new BigDecimal(robMoney.toString()).divide(new BigDecimal(100));
                        redService.recordRobRedPacket(uid, redID, new BigDecimal(robMoney.toString()));
                        vo.set(redID + uid + ":rob", result, 24L, TimeUnit.HOURS);
                        log.info("用户uid={}, redID={}, 抢到了={}元", uid, redID, result);
                        return result;
                    }
                }
            }catch (Exception e){
                throw  new Exception("系统异常-抢红包-加分布式锁失败");
            }
        }
        return null;
    }

    private Boolean click(String redID){
        ValueOperations vo = redisTemplate.opsForValue();
        String redTotalKey = redID+":total";
        Object obj = vo.get(redTotalKey);
        if(obj!=null && Integer.valueOf(obj.toString())>0){
            return true;
        }
        return false;
    }

}
