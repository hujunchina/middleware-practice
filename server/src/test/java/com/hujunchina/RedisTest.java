package com.hujunchina;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hujunchina.entity.Goods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
// 单元测试类，用于测试redis是否正常运行
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest

public class RedisTest {
    private static final Logger log = LoggerFactory.getLogger(RedisTest.class);

    @Autowired
    private RedisTemplate redisTemplate;

//    使用fastjson的序列化和反序列化工具
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void StringToCache(){
        log.info("-----redis 将字符串写入缓存-----");
        final String key = "redis:template:one:string";
        final String value = "odd-string";
//        redis 通用操作组件
        ValueOperations valueOperations = redisTemplate.opsForValue();
        log.info("-----开始写入字符串:{}-----", key);
        valueOperations.set(key, value);
//        从缓存得到
        Object result = valueOperations.get(key);
        log.info("-----得到数据:{}-----", result);
    }

    @Test
    public void ObjectToCache() throws JsonProcessingException {
        log.info("-----redis 将对象序列化到缓存-----");
        final Goods goods = new Goods(12345, "goodsName");
        final String key = "redis:template:two:object";
        final String value = objectMapper.writeValueAsString(goods);

        ValueOperations vo = redisTemplate.opsForValue();
        vo.set(key, value);
        log.info("写入了对象goods,{}", value);

        Object result = vo.get(key);
        if(result!=null){
            Goods resultGoods = objectMapper.readValue(result.toString(), Goods.class);
            log.info("得到对象goods,{}", goods);
        }
    }

}
