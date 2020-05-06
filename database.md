### 数据库操作

### 目录

1. 建立数据库和建表



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

