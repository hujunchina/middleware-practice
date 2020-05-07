### 数据库操作

### 目录

1. 建立数据库和建表
2. 抢红包系统



### 1. 建立数据库和建表

#### 1.1 建立数据库

2020年5月5日 21:40:32

```mysql
create database redis_goods;
```

建立一个基于redis缓存的商品数据库，开始一点一点完成一个小的商城项目。

#### 1.2 建立商品表

```mysql
create table goods_item(
    id int(11) not null auto_increment,
    code varchar(255) default null comment 'goods tag code',
    name varchar(255) character set utf8mb4 default null comment 'goods name',
    create_time datetime default null,
    update_time datetime default null,
    primary key(id)
)engine=InnoDB default charset=utf8 comment='goods info table';
```

创建表，表名和字段均不用加引号，设置自增和主键。使用`InnoDB`引擎，可以建立聚簇索引，高效查询。

```
insert into goods_item values(1, "book10010", "Middleware Practice", NOW(), NOW());
```

插入一条数据，利用SQL函数NOW() 获得当前时间。

#### 1.3 Mybaits 逆向工程

其实很简单，只需要两个文件，一个是逆向工程配置文件 `generator.xml`，配置好生成 entity、mapper接口、mapper.xml 三个文件的地址，配置好数据库位置和要生成的数据表即可。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <!--配置数据库信息 -->
    <context id="DB2Tables" targetRuntime="MyBatis3">
        <commentGenerator>
            <!-- suppressAllComments属性值： true:自动生成实体类、SQL映射文件时没有注释 true:自动生成实体类、SQL映射文件，并附有注释 -->
            <property name="suppressAllComments" value="true" />
        </commentGenerator>
        <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/redis_goods?serverTimezone=GMT%2b8"
                        userId="root"
                        password="hujun">
        </jdbcConnection>
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>
        <!-- 指定javaBean的生成位置 -->
        <javaModelGenerator
                targetPackage="com.hujunchina.middleware.model.entity"
                targetProject="model/src/main/java">
            <property name="enableSubPackages" value="true" />
            <property name="trimStrings" value="true" />
        </javaModelGenerator>
        <!-- 指定sql映射文件生成位置 -->
        <sqlMapGenerator targetPackage="mappers"targetProject="model/src/main/resources">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>
        <!-- 指定dao接口的生成位置，mapper接口 -->
        <javaClientGenerator type="XMLMAPPER"
                             targetPackage="com.hujunchina.middleware.model.mapper"
                             targetProject="model/src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>
        <!-- 指定每个表的生成策略 -->
        <table tableName="red_detail"/>
        <table tableName="red_record"/>
        <table tableName="red_divide"/>
    </context>
</generatorConfiguration>
```

还有一个是 java 类，用于根据配置文件启动执行生成响应的文件。

```java
public class MyBatisGeneratorDemo {
    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("model/src/test/java/generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        myBatisGenerator.getGeneratedJavaFiles();
    }
}
```



### 2. 抢红包系统

#### 2.1 建立表

建立发红包记录表，把每次发红包，创建的红包都记录下来，这里的 uid 是发红包的人。uuid 表示红包的唯一标识，光 id 是不够的，我们需要一个以纳秒为时间 gap 的 uuid 标识。

money设置为 decimal(10,2) 表示一共10位数字，其中小数点2位，为了后边的计算，这里红包的钱的单位是分，几分钱的分。

```mysql
create table red_record(
    id int(11) not null auto_increment,
    uid int(11) not null,
    uuid varchar(255) not null,
    total int(11) not null,
    money decimal(10,2) default null,
    is_valid tinyint(4) default 1,
    create_time datetime default null,
    primary key(id)
)engine=InnoDB auto_increment=11 default charset=utf8;
```

建表红包明细金额表，创建好红包，不是立刻就发出去，而是在发之前，提前随机分配好钱并记录到数据库和 Redis 中，然后根据 id 发给用户。

```mysql
create table red_detail(
    id int(11) not null auto_increment,
    record_id varchar(255) not null,
    per_money decimal(10,2) default null,
    is_valid tinyint(4) default 1,
    create_time datetime default null,
    primary key(id)
)engine=InnoDB auto_increment=83 default charset=utf8;
```

建表抢红包记录表，这个表是用户抢到钱的表，上一张表仅仅有金钱份额，没有和用户联系。这张表把份额和抢到的用户联系起来，对后面查看手气最佳等记录作用。

```mysql
create table red_divide(
    id int(11) not null auto_increment,
    uid int(11) not null,
    uuid varchar(255) default null,
    money decimal(10,2) default null,
    divide_time datetime default null,
    is_valid tinyint(4) default 1,
    primary key(id)
)engine=InnoDB auto_increment=72 default charset=utf8;
```

为了提高并发，利用 Redis 需要多建一张红包明细金额表。