### 项目部署和细节

### 目录

1. 安装 CentOS (或双系统)
2. 安装必备软件
3. 部署抢红包系统



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
- MYSQL |  由于Mysql被Oracle收购了，CentOS已不再提供安装，所以这个安装比较复杂，需要自己先更新镜像，然后禁用旧的模块，最后再安装。
  - `wget  https://repo.mysql.com//mysql80-community-release-el8-1.noarch.rpm`
  - `yum localinstall mysql80-community-release-el8-1.noarch.rpm`
  - `yum module disable mysql`
  - `yum install mysql-community-server`
  - `grant all on redis_goods.*  to hujun identified by hujunpasswd`

- Redis | `yum install redis` 即可，启动服务 `systemctl start redis`
- Git | `yum install git`
- 

