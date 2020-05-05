### 分布式中间件实战

### 项目目录

- 项目介绍
- 一个简单的Spring Boot 程序
- 加入 Redis 中间件



### 1. 项目介绍

学习《分布式中间件技术实战》动手项目，这本书讲了常见的中间件 Redis、RabbitMQ、ZooKeeper、Nginx等。非常适合我这种没有接触过大型项目的学生。老老实实学完本书。

#### 1.1 项目时间点

- 项目开始2020年5月4日 19:12:02



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

