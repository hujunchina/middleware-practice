package com.hujunchina.middleware.server.service.redis;

import com.hujunchina.middleware.model.entity.RedDetail;
import com.hujunchina.middleware.model.entity.RedRecord;
import com.hujunchina.middleware.model.mapper.RedDetailMapper;
import com.hujunchina.middleware.model.mapper.RedDivideMapper;
import com.hujunchina.middleware.model.mapper.RedRecordMapper;
import com.hujunchina.middleware.server.entity.RedPacket;
import com.hujunchina.middleware.server.service.IRedPacketService;
import com.hujunchina.middleware.server.service.IRedService;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Async
public class RedService implements IRedService {
    private static final Logger log = LoggerFactory.getLogger(RedService.class);
    @Autowired
    private RedRecordMapper redRecordMapper;
    @Autowired
    private RedDetailMapper redDetailMapper;
    @Autowired
    private RedDivideMapper redDivideMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void recordRedPacket(RedPacket redPacket, String redID, List<Integer> list) throws Exception {
//        发红包记录到数据库，谁发的红包
        RedRecord redRecord = new RedRecord();
        redRecord.setUid(redPacket.getUid());
        redRecord.setUuid(redID);
        redRecord.setTotal(redPacket.getTotal());
        redRecord.setMoney(BigDecimal.valueOf(redPacket.getAmount()));
        redRecord.setCreateTime(new Date());
        redRecordMapper.insertSelective(redRecord);
        log.info("---插入红包分配数据成功---{}",redRecord.getId());

//        红包分割明细记录到数据库，每个人分多少
        RedDetail redDetail;
        for(Integer part : list){
            redDetail = new RedDetail();
            redDetail.setRecordId(redID);
            redDetail.setPerMoney(BigDecimal.valueOf(part));
            redDetail.setCreateTime(new Date());
            redDetailMapper.insertSelective(redDetail);
            log.info("---红包明细插入成功---");
        }
    }

    @Override
    public void recordRobRedPacket(Integer uid, String redID, BigDecimal amount) throws Exception {
//         抢到红包记录到数据库，谁抢到了
    }
}
