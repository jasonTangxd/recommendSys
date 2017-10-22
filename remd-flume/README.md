## remd-flume 数据采集项目 


### 数据的采集
- xiaoxiaomo08、xiaoxiaomo09两台机器实时生产日志主要类型为`access.log`、`ugcheader.log`、`ugctail.log` , **要求**：
1. 把xiaoxiaomo08、xiaoxiaomo09 机器中的access.log、ugcheader.log、ugctail.log 汇总到C机器上然后统一收集到hdfs和Kafka中。
2. 在hdfs中要求的目录为：用作离线统计。
**/source/access/2016-01-01/**
**/source/ugcheader/2016-01-01/**
**/source/ugctail/2016-01-01/**
3. Kafka分topic , 用作后续分析，比如这块也做成实时数据分析等。

### 画图
![项目结构图](http://img.xiaoxiaomo.com/blog/img/20160522195635.jpg)

### 准备


- **在xiaoxiaomo10中启动服务**
1. 启动zookeeper : zkServer.sh start
2. 启动kafka : nohup kafka-server-start.sh /data//kafka-0.8.2.2/config/server.properties >>/data/data/kafka/logs/kafka-server.log 2>&1 &
3. 启动hdfs : start-dfs.sh 
4. 这里查看一下xiaoxiaomo10的进程 : 
``` bash
[root@xiaoxiaomo10 apache-flume-1.6.0-bin]# jps
1776 Jps
1415 NameNode
1662 SecondaryNameNode
1277 Kafka
1501 DataNode
1241 QuorumPeerMain
```

- **创建topic**
``` bash
[root@xiaoxiaomo10 kafka]# kafka-topics.sh --create --zookeeper xiaoxiaomo10:2181 --replication-factor 1 --partition 1 --topic access 
[root@xiaoxiaomo10 kafka]# kafka-topics.sh --create --zookeeper xiaoxiaomo10:2181 --replication-factor 1 --partition 1 --topic ugchead 
[root@xiaoxiaomo10 kafka]# kafka-topics.sh --create --zookeeper xiaoxiaomo10:2181 --replication-factor 1 --partition 1 --topic ugctail 
[root@xiaoxiaomo10 kafka]# kafka-topics.sh --list --zookeeper xiaoxiaomo10:2181   ###查看
access
ugchead
ugctail
```

### xiaoxiaomo10机器(汇总节点，汇总节点可以做一下故障转移)
``` bash
[root@xiaoxiaomo10 apache-flume-1.6.0-bin]# vim conf/hdfs_kafka.conf

#################################### xiaoxiaomo10机器 #########################################
#################################### 两个channel、两个sink ##########################

# Name the components on this agent
a1.sources = r1
a1.sinks = kfk fs
a1.channels = c1 c2

# varo source
a1.sources.r1.type = avro
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 44444

# source r1定义拦截器，为消息添加时间戳
a1.sources.r1.interceptors = i1
a1.sources.r1.interceptors.i1.type = org.apache.flume.interceptor.TimestampInterceptor$Builder

# kfk sink
a1.sinks.kfk.type = org.apache.flume.sink.kafka.KafkaSink
#a1.sinks.kfk.topic = mytopic
a1.sinks.kfk.brokerList = xiaoxiaomo10:9092


# hdfs sink
a1.sinks.fs.type = hdfs
a1.sinks.fs.hdfs.path = hdfs://xiaoxiaomo10:9000/source/%{type}/%Y%m%d
a1.sinks.fs.hdfs.filePrefix = events-
a1.sinks.fs.hdfs.fileType = DataStream
#a1.sinks.fs.hdfs.fileType = CompressedStream
#a1.sinks.fs.hdfs.codeC = gzip
#不按照条数生成文件
a1.sinks.fs.hdfs.rollCount = 0
#如果压缩存储的话HDFS上的文件达到64M时生成一个文件注意是压缩前大小为64生成一个文件，然后压缩存储。
a1.sinks.fs.hdfs.rollSize = 67108864
a1.sinks.fs.hdfs.rollInterval = 0


# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 10000
a1.channels.c1.transactionCapacity = 1000

a1.channels.c2.type = memory
a1.channels.c2.capacity = 10000
a1.channels.c2.transactionCapacity = 1000

# Bind the source and sink to the channel
a1.sources.r1.channels = c1 c2
a1.sinks.kfk.channel = c1
a1.sinks.fs.channel = c2
```
- 启动
```
[root@xiaoxiaomo10 ~]# cd /opt/apache-flume/
[root@xiaoxiaomo10 apache-flume-1.6.0-bin]# bin/flume-ng agent --conf conf/ --conf-file conf/hdfs_kafka.conf --name a1 -Dflume.root.logger=INFO,console &
......
......
......Component type: SINK, name: kfk started  ##启动成功
```

### **xiaoxiaomo08 xiaoxiaomo09机器，采集web数据**
``` bash
[root@xiaoxiaomo08 apache-flume-1.6.0-bin]# vim conf/hdfs_kafka.conf

#################################### xiaoxiaomo08机器 #########################################
#################################### 3个source #####################################
#################################### 2个拦截器 ######################################
# Name the components on this agent
a1.sources = access ugchead ugctail
a1.sinks = k1
a1.channels = c1

# 三个sources
a1.sources.access.type = exec
a1.sources.access.command = tail -F /opt/data/access.log

a1.sources.ugchead.type = exec
a1.sources.ugchead.command = tail -F /opt/data/ugchead.log

a1.sources.ugctail.type = exec
a1.sources.ugctail.command = tail -F /opt/data/ugctail.log

# sink
a1.sinks.k1.type = avro
a1.sinks.k1.hostname = xiaoxiaomo10
a1.sinks.k1.port = 44444

# channel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 10000
a1.channels.c1.transactionCapacity = 1000

# interceptor
a1.sources.access.interceptors = i1 i2
a1.sources.access.interceptors.i1.type=static
a1.sources.access.interceptors.i1.key = type
a1.sources.access.interceptors.i1.value = access
a1.sources.access.interceptors.i2.type=static
a1.sources.access.interceptors.i2.key = topic
a1.sources.access.interceptors.i2.value = access

a1.sources.ugchead.interceptors = i1 i2
a1.sources.ugchead.interceptors.i1.type=static
a1.sources.ugchead.interceptors.i1.key = type
a1.sources.ugchead.interceptors.i1.value = ugchead
a1.sources.ugchead.interceptors.i2.type=static
a1.sources.ugchead.interceptors.i2.key = topic
a1.sources.ugchead.interceptors.i2.value = ugchead

a1.sources.ugctail.interceptors = i1 i2
a1.sources.ugctail.interceptors.i1.type=static
a1.sources.ugctail.interceptors.i1.key = type
a1.sources.ugctail.interceptors.i1.value = ugctail
a1.sources.ugctail.interceptors.i2.type=static
a1.sources.ugctail.interceptors.i2.key = topic
a1.sources.ugctail.interceptors.i2.value = ugctail

# Bind the source and sink to the channel
a1.sources.access.channels = c1
a1.sources.ugchead.channels = c1
a1.sources.ugctail.channels = c1
a1.sinks.k1.channel = c1

```

- 启动xiaoxiaomo08 xiaoxiaomo09机器
``` bash
[root@xiaoxiaomo08 apache-flume-1.6.0-bin]# nohup bin/flume-ng agent --conf conf/ --conf-file conf/hdfs_kafka.conf --name a1 -Dflume.root.logger=INFO,console &
```

### 验证功能

```
1. 查看**hdfs**的情况
``` bash
[root@xiaoxiaomo10 ~]# hdfs dfs -text /source/ugchead/20171022/* | more 
1001	221.8.9.6 80	be83f3fd-a218-4f98-91d8-6b4f0bb4558b	750b6203-4a7d-42d5-82e4-906415b70f63	10207	{"ugctype":"consumer",
"userId":"40604","coin":"10","number":"2"}	1508669808170
1003	218.75.100.114	ea11f1d2-680d-4645-a52e-74d5f2317dfd	8109eda1-aeac-43fe-94b1-85d2d1934913	20101	{"ugctype":"fav","user
Id":"40604","item":"13"}	1508669808170
......
```

2. **kafka**消费者
``` bash
########################## 这里查看一下access ###############################
[root@xiaoxiaomo09 ~]# /opt/kafka/bin/kafka-console-consumer.sh --zookeeper xiaoxiaomo10:2181 --topic access  --from-beginning 
1001	218.26.219.186	070c8525-b857-414d-98b6-13134da08401	10201	0	GET /tologin HTTP/1.1	408	/update/pass	Mozilla/5.0 (Windows; U; Windows NT 5.1)Gecko/20070803 Firefox/1.5.0.12	1508669808170
......
```

