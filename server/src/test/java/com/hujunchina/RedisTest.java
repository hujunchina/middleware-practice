package com.hujunchina;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hujunchina.middleware.server.entity.Goods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

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
    public void stringToCache(){
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
    public void objectToCache() throws JsonProcessingException {
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

    @Test
    public void listType(){
        List<Goods> repo = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            repo.add(new Goods(i, "repo-goods"));
        }
//        存数据
        final String key = "redis:template:three:list";
        ListOperations lo = redisTemplate.opsForList();
        for(Goods goods : repo){
            lo.leftPush(key, goods);
        }
//        取数据
        Object result = lo.rightPop(key);
        while(result!=null){
            System.out.println(result);
            result = lo.rightPop(key);
        }
    }

    @Test
    public void setType(){
        List<String> repo = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            repo.add("repo-set-element");
        }
//        存数据
        final String key = "redis:template:four:set";
        SetOperations so = redisTemplate.opsForSet();
        for(String str : repo ){
            so.add(key, str);
        }
//       取数据
        Object result = so.pop(key);
        while(result!=null){
            System.out.println(result);
            result = so.pop(key);
        }
    }

    @Test
    public void zsetType(){
        List<Goods> repo = new ArrayList<>();
        repo.add(new Goods(10300, "book10000"));
        repo.add(new Goods(10220, "book10020"));
        repo.add(new Goods(10130, "book10030"));
        repo.add(new Goods(10040, "book10040"));
        repo.add(new Goods(10050, "book10050"));
        repo.add(new Goods(10060, "book10060"));
        repo.add(new Goods(10070, "book10070"));
//        存数据
        final String key = "redis:template:five:zset";
        ZSetOperations zso = redisTemplate.opsForZSet();
        for(Goods good : repo){
//            正常插入，最后一个是排序的score，按书号排序
//            必须要实现序列化
            zso.add(key, good, good.getGoodsNo());
        }
//        取数据
        Long size = zso.size(key);
        Set<Goods> result = zso.range(key, 0L, size);
        for(Goods good : result){
            System.out.println(good);
        }
    }

    @Test
    public void hashType() throws JsonProcessingException {
        Map<String, Goods> field = new HashMap<>();
        field.put("book1", new Goods(1001, "goods1"));
        field.put("book2", new Goods(1001, "goods2"));
        field.put("book3", new Goods(1001, "goods3"));
        field.put("book4", new Goods(1001, "goods4"));
        field.put("book5", new Goods(1001, "goods5"));
        field.put("book6", new Goods(1001, "goods6"));
//        存数据
        final String key = "redis:template:six:hash";
        HashOperations ho = redisTemplate.opsForHash();
        ObjectMapper om = new ObjectMapper();
        for(String str : field.keySet()){
            ho.put(key, str, om.writeValueAsString(field.get(str)));
        }
//        取数据
        for(String str: field.keySet()){
            Object result = ho.get(key, str);
            log.info("--{}",om.readValue(result.toString(), Goods.class));
        }
    }
}
