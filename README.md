### 分布式中间件实战

### 项目目录

- 项目介绍
- 一个简单的Spring Boot 程序
- 加入 Redis 中间件
- Redis 抢红包系统



### 1. 项目介绍

学习《分布式中间件技术实战》动手项目，这本书讲了常见的中间件 Redis、RabbitMQ、ZooKeeper、Nginx等。非常适合我这种没有接触过大型项目的学生。老老实实学完本书。

#### 1.1 项目时间点

- 项目开始2020年5月4日 19:12:02
- 搭建 Redis 环境 2020年5月5日 17:34:11
- 测试 Redis 穿透 2020年5月6日 21:16:28
- Redis 红包系统的发红包部分 2020年5月7日 15:11:53



### 2. 一个简单的SpringBoot程序

#### 2.1 框架

- api module（提供其他中间件调用）
- model module（数据操作）
- server module（核心业务处理，路由处理）

其中api module中的pom.xml 导入依赖

```java
<dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>${jackson.version}</version>
        </dependency>
</dependencies>
```

model module 导入依赖

```
<dependencies>
        <dependency>
            <groupId>com.hujunchina</groupId>
            <artifactId>api</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis-spring-boot.version}</version>
        </dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper</artifactId>
            <version>5.1.11</version>
        </dependency>
</dependencies>
```

server module 导入依赖

```
<dependencies>
        <dependency>
            <groupId>com.hujunchina</groupId>
            <artifactId>model</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <!--日志 https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-log4j2 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
            <version>2.2.6.RELEASE</version>
        </dependency>
        <!--谷歌的扩展java库 https://mvnrepository.com/artifact/com.google.guava/guava -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>29.0-jre</version>
        </dependency>
        <!--数据库连接 https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.20</version>
        </dependency>
        <!--阿里巴巴的数据库连接池 https://mvnrepository.com/artifact/com.alibaba/druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.22</version>
        </dependency>
        <!--springboot全家桶 https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.2.6.RELEASE</version>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>ch.qos.logback</groupId>
                    <artifactId>logback-classic</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>log4j-over-slf4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--测试用 https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>2.2.6.RELEASE</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
</dependencies>
```

#### 2.2 测试

这里构建了一个 `goods` 路由响应控制器 `GoodsController` 

```java
@RestController
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @RequestMapping(value="info", method = RequestMethod.GET)
    public Goods info(Integer goodsNo, String goodsName){
        Goods goods = new Goods();
        goods.setGoodsNo(goodsNo);
        goods.setGoodsName(goodsName);
        return goods;
    }
}
```

和一个 `Goods` 实体类

```java
@Data
public class Goods {
    private Integer goodsNo;
    private String goodsName;
}
```

然后我们通过下面的 URL 访问

```
http://localhost:8088/middleware/goods/info?goodsNo=1&goodsName=boods
```

其中8088 和 `middleware` 是配置的端口和路由入口。

如果提示 `log4j-slf4j-impl cannot be present with log4j-to-slf4j` 错误，需要排除SpringBoot中自带的 log4j 组件。

```java
<exclusions>
    <exclusion>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
    <exclusion>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    </exclusion>
</exclusions>
```



### 3. 加入 Redis 中间件

#### 3.1 Redis 基本知识点

Redis 的到来，因为互联网 web1.0 时代 Web 应用大多是单机集成的，无法满足海量用户需求；在 web2.0 时代为了实现高并发，高QPS和解决数据库瓶颈，把数据缓存到内存中，提高读写效率。Redis 就是以内存为介质存储的 k-v 键值对的NoSQL非关系型数据库。

因为 Redis 采用了内存存储，再加上单线程操作和对IO的多路复用，使得Redis 速度非常快。

可以用于热点数据存储，排行榜，时限数据等。

#### 3.2 SpringBoot 集成 Redis

很简单，其实就是在 pom 中价格依赖。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

然后在文件 `application.properties` 中配置端口和 IP 。

```
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

#### 3.3 Redis 测试

这部分在`\server\src\test\java\com\hujunchina\RedisTest.java` 代码中。

直接使用 `RedisTemplate` 封装好的实体类操作 Redis 。通过该类生成 `ValueOpreations` 类操作get&set。

并通过 JackSon 提供的 ObjectMapper 类对类进行实例化和反实例化。

```java
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
```

#### 3.4 Redis 穿透测试

Redis 穿透指用户请求的数据在 Redis中没有缓存需要向数据库查询，这样频繁的操作等于 Redis 没有任何作用，每次访问都是查询数据库，数据库压力还是很大。解决的办法就是，第一次查询不到时，把结果以 Null 形式缓存在 Redis 中并设置过期时间。

按照 [数据库操作](./database.md) 建立表，然后插入数据。

新建 `CachePassController` 类用来响应 URL 请求。

```java
//  控制类
@RestController
public class CachePassController {
    private static final Logger log = LoggerFactory.getLogger(CachePassController.class);
    private static final String prefix = "cache/pass";
//    服务类
    @Autowired
    private CachePassService cachePassService;

    @RequestMapping(value = prefix+"/item/info", method = RequestMethod.GET)
    public Map<String, Object> getItem(@RequestParam String itemCode){
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("code", 0);
        resMap.put("msg", "success");
        try {
            resMap.put("data", cachePassService.getItemInfo(itemCode));
        }catch (Exception e){
            resMap.put("code", -1);
            resMap.put("msg", "failed:"+e.getMessage());
        }
        return resMap;
    }
}
```

新建 `CachePassServer` 用来处理具体业务服务。

```java
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
            if(result!=null & !Strings.isNullOrEmpty(result.toString())){
                item = objectMapper.readValue(result.toString(), Item.class);
            }
        }else{
            log.info("---缓存中不存在商品信息--从数据库中查找--商品编号:{}", itemCode);
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
```

这里当 item 为空时，插入一个新 key 为 “”。

接下来，使用 Jmeter 测试。首先添加一个 `ThreadGroup`，然后添加一个 `HTTPRequest` 请求测试。

这样可以设置多线程同时发送请求。在本机测试最高并发量是1200/s，在局域网下测试最高并发量是80/s。

性能还是很低的，如果考虑到路由器等硬件因素会更低。

PS: 如果局域网无法访问 SpringBoot，需要在 application.properties 里面设置 server.address=0.0.0.0，最后别忘了把防火墙关了。

### 4. Redis 抢红包系统

#### 4.1 业务逻辑或者流程

用户打开页面，输入金额和数量，发红包，其他用户点开页面，抢到红包，没抢到了可以查看红包记录。

INPUT：发红包用户ID，金额 Amount，数量 Total

OUTPUT：红包份数 List 数组，Redis 和数据库记录（包括红包生成单，红包分配单，红包抢购单）

VALID：输入输出合法性，用户抢时先检查 Redis 队列中是否为空。

大量请求：把红包事先公平的生成好，并存在Redis中，用Redis来扛流量，其他数据都异步延迟写入到数据库。

#### 4.2 红包分配算法：二倍均值算法

$$
money = M \div N *2
$$

其中M是总钱数，N是总人数，确定一个范围最小限是0.01元，最高为money元。

表示任何红包份额都不会超过总钱数的一半，比如100元，不会出现大于50元的红包。

```java
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
```

#### 4.3 红包唯一标识符设计

```java
//           红包唯一标识
String timeStamp = String.valueOf(System.nanoTime());
String redID = new StringBuffer(keyPrefix).append(redPacket.getUid()).
    				append(":").append(timeStamp).toString();
```

通过生成的时间来标识，时间精确到纳秒，不知道在上千万的QPS下会不会重复，待测试。

#### 4.4 数据表存储

```java
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
```

存储到数据库中。

##### 注意：

1. FastJson 在处理POST传入的参数时，如果是个对象，该对象的声明类一定要有空构造方法。因为FastJson需要使用空构造方法反序列化。
2. 数据库写入时如果不指明ID，会自动按增长分配，但不能立刻得到，需要查询一次才有。

