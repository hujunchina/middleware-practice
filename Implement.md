### 项目部署和细节

### 目录

1. 安装 CentOS (或双系统)
2. 安装必备软件
3. 部署抢红包系统
4. 安装 RabbitMQ
5. RabbitMQ 收发消息



### 1. 安装 CentOS

安装双系统，其实很简单，我过度担心了。

电脑有多块硬盘，不用担心 Win10 会覆盖，只要在重启的时候选择哪个硬盘第一读取就行了。

选择安装盘时，会把整个盘都占用，所以在Win10分区对Centos没有用。

安装前需要把整个盘的文件都复制出来，防止格式化丢失。

其他的都很简单，完全不用考虑驱动问题。



### 2. 安装必备软件

- SSH  |  `yum install openssh`  会自动安装 openssh-server
- JAVA | `wget https://hujunchina.lanzous.com/iccr12j` 下载jre1.8，然后安装
- JAVA_HOME |  `echo JAVA_HOME="/usr/java/jdk-11.0.7/" > /etc/profile.d/java.sh` 
- MAVEN | `yum install maven`
  
  - [设置国内镜像](https://help.aliyun.com/document_detail/102512.html)  `vim /etc/maven/settings.xml`
- MYSQL |  由于Mysql被Oracle收购了，CentOS已不再提供安装，所以这个安装比较复杂，需要自己先更新镜像，然后禁用旧的模块，最后再安装。
  - `wget  https://repo.mysql.com//mysql80-community-release-el8-1.noarch.rpm`
  - `yum localinstall mysql80-community-release-el8-1.noarch.rpm`
  - `yum module disable mysql`
  - `yum install mysql-community-server`
  - `grant all on redis_goods.*  to hujun identified by hujunpasswd`

- Redis | `yum install redis` 即可，启动服务 `systemctl start redis`

- Git | `yum install git`

- [设置国内镜像](https://mirror.tuna.tsinghua.edu.cn/help/centos/) | `sudo vim /etc/yum.repos.d/CentOS-Base.repo` 改为下面的`baseurl`。

  ```repo
  [BaseOS]
  name=CentOS-$releasever - Base
  baseurl=https://mirrors.tuna.tsinghua.edu.cn/centos/$releasever/BaseOS/$basearch/os/
  #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=BaseOS&infra=$infra
  enabled=1
  gpgcheck=1
  gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-centosofficial
  ```


#### 2.2 问题

- 关闭防火墙 `systemctl stop firewalld`



### 3. 部署抢红包系统

#### 3.3 问题

- 一定要设置国内镜像，第一次 mvn install 需要下载很多东西。
- 测试不通过，需要指明启动类 `@SpingBootTest(classes=MainApplication.class)`
- 测试完成后，需要修改数据库用户名和密码，数据库名等

#### 3.4 测试

​	使用 Jmeter 压测，QPS 大致在120 左右，不是很良好。



### 4. 安装 RabbitMQ

#### 4.1 Win10 安装 RabbitMQ

1. 下载并安装 Erlang

   [下载地址](http://www.erlang.org/downloads), 安装成功后配置环境变量 `set ERLANG_HOME=C:\cmd\erlang`

2. [安装 RabbitMQ](http://www.rabbitmq.com/download.html), 直接安装

3. 配置激活 http 页面管理插件，直接用开始菜单的快捷方式启动cmd

   `rabbitmq-plugins.bat enable rabbitmq_management`

4. 创建用户

   查看用户 `rabbitmqctl.bat list_users`

   创建用户 `rabbitmqctl.bat add_user hujun hujun`

5. 访问管理

   浏览器输入 `localhost:15672` 登录管理

RabbitMQ 对 Win10 很友好，安装简单，命令执行更简单，直接提供了一个特殊的 CMD 窗口，和一些类 Linux命令，直接进行命令式管理。

#### 4.2 CentOS 安装 RabbitMQ

由于 centos 自带的仓库没有此程序，不能直接通过 yum 安装，需要手动设置安装镜像。

1. [安装 Erlang](https://github.com/rabbitmq/erlang-rpm)，创建一个 erlang 的仓库：

   ```sh
   # In /etc/yum.repos.d/rabbitmq_erlang.repo
   # sudo vim /etc/yum.repos.d/rabbitmq_erlang.repo
   [rabbitmq_erlang]
   name=rabbitmq_erlang
   baseurl=https://packagecloud.io/rabbitmq/erlang/el/8/$basearch
   repo_gpgcheck=1
   gpgcheck=1
   enabled=1
   # PackageCloud's repository key and RabbitMQ package signing key
   gpgkey=https://packagecloud.io/rabbitmq/erlang/gpgkey
          https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc
   sslverify=1
   sslcacert=/etc/pki/tls/certs/ca-bundle.crt
   metadata_expire=300
   
   [rabbitmq_erlang-source]
   name=rabbitmq_erlang-source
   baseurl=https://packagecloud.io/rabbitmq/erlang/el/8/SRPMS
   repo_gpgcheck=1
   gpgcheck=0
   enabled=1
   # PackageCloud's repository key and RabbitMQ package signing key
   gpgkey=https://packagecloud.io/rabbitmq/erlang/gpgkey
          https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc
   sslverify=1
   sslcacert=/etc/pki/tls/certs/ca-bundle.crt
   metadata_expire=300
   ```

   有了这个仓库就可以直接安装了，`yum install -y erlang`

2. [安装 RabbitMQ](https://packagecloud.io/rabbitmq/erlang/install#bash-rpm)，直接使用另一个仓库镜像，速度很快

   ```sh
   curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash
   ```

   这个镜像自动安装，然后更新，最后运行 `yum install rabbitmq-server` 即可。

3. [启动服务和管理](https://www.rabbitmq.com/install-rpm.html#running-rpm)

   启动守护进程，`chkconfig rabbitmq-server on`

   然后，`rabbitmq-server start`  启动服务，`rabbitmq-plugins enable management` 启动管理GUI。

   外网登录管理页面需要设置：`vi /usr/lib/rabbitmq/lib/rabbitmq_server-3.8.1/ebin/rabbit.app`

   然后把 `{loopback_users, [<<”guest”>>]} -> {loopback_users, []}`

4. 用户管理

   `rabbitmqctl add_user hujun hujun`

### 5. RabbitMQ 收发消息

#### 5.1 rabbitmq 配置

需要提前定义好mq 的相关配置和自定义队列名等

```json
#rabbitmq配置
spring.rabbitmq.virtual-host=/
spring.rabbitmq.host=127.0.0.1
spring.rabbitmq.port=5672
spring.rabbitmq.username=hujun
spring.rabbitmq.password=hujun
mq.env=local

#定义基本消息队列名称
mq.basic.info.queue.name=${mq.env}.middleware.mq.basic.info.queue
mq.basic.info.exchange.name=${mq.env}.middleware.mq.basic.info.exchange
mq.basic.info.routing.key.name=${mq.env}.middleware.mq.basic.info.routing.key
```

消息中间件三要素：队列、交换机、路由。

左边是程序中要统一用到的名称，右边是存储到mq中的变量名。

#### 5.2 rabbitmq 声明队列

```java
@Bean(name="basicQueue")
public Queue basicQueue(){
    return new Queue(env.getProperty("mq.basic.info.queue.name"), true);
}
//    交换机
@Bean
public DirectExchange basicExchange(){
    return new DirectExchange(env.getProperty("mq.basic.info.exchange.name"), true, false);
}
//    绑定
@Bean
public Binding basicBinding(){
    return BindingBuilder.bind(basicQueue()).
        to(basicExchange()).
        with(env.getProperty("mq.basic.info.routing.key.name"));
}
```

通过调用 BindingBuilder 静态类 bind 队列，to 交换机，with 路由。

#### 5.3 生产者

```java
public void sendMsg(String msg){
    if(!Strings.isNullOrEmpty(msg)){
        try{
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.basic.info.exchange.name"));
      rabbitTemplate.setRoutingKey(env.getProperty("mq.basic.info.routing.key.name"));
            rabbitTemplate.convertAndSend(msg);
            log.info("-基本消息模型-生产者-发送消息:{}", msg);
        }catch (Exception e){
            log.error("-基本消息模型-生产者-发送消息异常:{}", e.fillInStackTrace());
        }
    }
}
```

通过 RabbitTemplate 设置好队列的交换机和路由，并发送消息。

#### 5.4 消费者

```java
@RabbitListener(queues = "${mq.basic.info.queue.name}", containerFactory = "singleListenerContainer")
public void consumeMsg(@Payload String msg){
    try{
        System.out.println("消费者");
        log.info("-基本消息模型-消费者-接收消息:{}", msg);
    }catch (Exception e){
        log.error("-基本消息模型-消费者-接收消息异常:{}", e.fillInStackTrace());
    }
}
```

直接通过注解方式声明队列名。