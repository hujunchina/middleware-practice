package com.hujunchina.middleware.server.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.hujunchina.middleware.model.entity.Item;
import com.hujunchina.middleware.model.mapper.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class CachePassService {
    private static final Logger log = LoggerFactory.getLogger(CachePassService.class);
    private static final String prefix = "item:";
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public ItemMapper itemMapper;
    @Autowired
    public RedisTemplate redisTemplate;

    public Item getItemInfo(String itemCode) throws JsonProcessingException {
        Item item = null;
        final String key = prefix+itemCode;
        ValueOperations vo = redisTemplate.opsForValue();
        if(redisTemplate.hasKey(key)){
            log.info("---获取商品的信息--缓存中商品的信息--商品编号:{}", itemCode);
            Object result = vo.get(key);
            if(result!=null & ! Strings.isNullOrEmpty(result.toString())){
                item = objectMapper.readValue(result.toString(), Item.class);
            }
        }else{
            log.info("---获取商品信息--缓存中不存在商品信息--从数据库中查找--商品编号:{}", itemCode);
            item = itemMapper.selectByCode(itemCode);
            if(item!=null){
                vo.set(key, objectMapper.writeValueAsString(item));
            }else{
                vo.set(key, "", 30L, TimeUnit.SECONDS);
            }
        }
        return item;
    }
}
