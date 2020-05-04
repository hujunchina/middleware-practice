### 分布式中间件实战

### 项目目录

- 项目介绍
- 一个简单的Spring Boot 程序
- 。。。



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

