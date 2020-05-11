### 分布式中间件实战

### 项目目录

- 项目介绍
- 一个简单的Spring Boot 程序
- 加入 Redis 中间件
- Redis 抢红包系统
- Spring 事件驱动模型
- Rabbitmq 项目



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

### 5. 抢红包第二部分

即用户抢红包业务，上一节实现了红包的创建和提前分配，这节实现如何抢红包。

用户入口即点开红包，需要参数确定用户和红包，进行红包分配，然后返回结果。

INPUT：uesID，redID

OUTPUT：BaseResponse （封装好的 Json 来实现 RESTfulAPI）

中间过程有：1. 检测合法性（红包个数和是否抢过）2. 更新redis中红包总数和红包份额 3. 把信息写入数据库

#### 5.1 路由部分

```java
@RequestMapping(value = prefix+"/rob", method = RequestMethod.GET)
public BaseResponse rob(@RequestParam Integer uid, @RequestParam String redID){
    BaseResponse response = new BaseResponse(StatusCode.Success);
    try{
        BigDecimal result = redPacketService.rob(uid, redID);
        if( result!=null ){
            response.setData(result);
        }else{
            response = new BaseResponse(StatusCode.Fail.getCode(), "红包被抢完了");
        }
    }catch (Exception e){
        log.error("抢红包异常: userID = {}, redID = {}", uid,redID, e.fillInStackTrace());
        response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
    }
    return response;
}
```

构造input输入格式，调用服务类的rob，让其处理中间过程，最后判断并返回结果。

#### 5.2 Redis 部分

```java
private Boolean click(String redID){
    ValueOperations vo = redisTemplate.opsForValue();
    String redTotalKey = redID+":total";
    Object obj = vo.get(redTotalKey);
    if(obj!=null && Integer.valueOf(obj.toString())>0){
        return true;
    }
    return false;
}
```

首先定义一个判断红包总数的函数，处理红包被抢完了情况。

```java
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
```

这里第一步，从redis中获取redID+uid键值判断是否领取过。而后更新total并插入自己领取的键hasRobbed。

一切顺利的话，开始写入数据库。

有一个问题是，高并发下用户存在超抢问题。一个用户可能抢到两次红包，因为一个线程执行抢方法时，还没来得及写入hasRobbed到redis，另一个线程开始判断hasRobbed，导致hasRobbed设置失败，另一个线程抢红包。

这时需要加一个分布式锁，即一个redis的key，hasEntry，比hasRobbed更超前设置到redis中即可。不能提前设置hasRobbed，因为后面执行不一定抢到红包。

```java
@Override
public void recordRobRedPacket(Integer uid, String redID, BigDecimal amount) throws Exception {
//         抢到红包记录到数据库，谁抢到了
RedDivide redDivide = new RedDivide();
redDivide.setUid(uid);
redDivide.setUuid(redID);
redDivide.setMoney(amount);
redDivide.setDivideTime(new Date());
redDivideMapper.insertSelective(redDivide);
}
```

将抢到的结果写入数据库，持久化。

#### 5.3 Jmeter 压测

秒级高并发，每秒请求达到成千上万。我们使用apache的开源项目 Jmeter 进行压测。

![image-20200508171549416](C:\code\github\middleware-practice\img\image-20200508171549416.png)

结构如上图，设置一个线程组，里面包含一个Http请求和CSV数据配置和结果查看树。

本机上测试，秒级请求10000个，服务器可以正常处理请求。

### 6. Spring 事件驱动模型

事件=消息（数据等），驱动=到来（动力），事件驱动=以数据或消息的到来有无为信号来通知其他进程做事情。

相比于传统的直接调用，事件驱动更加有意识，只有满足条件时就调用。

#### 6.1 用户登录事件

把用户登录类作为一个消息事件 LoginEvent，继承 Spring 自带的 ApplicationEvent，这样才能是事件驱动的类型，不然不被接受无法把一个普通类加到发布者中。

```java
public class LoginEvent extends ApplicationEvetn implements Serializable{}
```

然后，仿照线程的生产者-消费者模型，事件驱动也有事件的生产（发布者）ApplicationEventPublisher 接口，中的 publishEvent 方法发送消息。

```java
@Autowired
private ApplicationEventPublisher publisher;
public.publishEvent(loginEvent);
```

这里使用了异步通信方式发送消息。

最后，消费者（监听者）ApplicationListener 接口，重写消费时间方法 onApplicationEvent 即可完成自己如何消费处理消息事件了。

```java
public class EventConsumer implements ApplicationListener<LoginEvent>{
    @Override
    @Async
    public void onApplicationEvent(LoginEvent loginEvent) {
        log.info("Spring-事件驱动模型-收到消息:{}", loginEvent);
        // 收到消息时的异步处理，如写入数据库等
    }
}
```

#### 6.2 底层原理

Spring 的事件驱动模型很有意识，如果设置了异步通信方式，底层会使用 线程池来保存一个一个消息，并使用 linkedblockingqueue 作为阻塞队列。

当有消息 publisher 时，就是把消息放入到线程池中 excutoer.execute()。

当消费消息 listener 时，线程池会根据用户重写的方法来执行具体的语句。

那么，是怎么知道有哪些消费者呢？如果有多个消费者怎么办？

测试了，多个监听者都可以收到消息。只要有消息 publish 出来就能接受到消息。

Spring Context 加载初始化完成（refresh）后会再次检测应用中的 `ApplicationListener`，并且注册，此时会将我们实现的 `ApplicationListener` 就会加入到 `SimpleApplicationEventMulticaster` 维护的 Listener 集合中，这个集合是 ConcurrentHashMap。

### 7. RabbitMQ 项目

#### 7.1 RabbitMQ 简单收发消息

见 [implement.md](./implement.md)