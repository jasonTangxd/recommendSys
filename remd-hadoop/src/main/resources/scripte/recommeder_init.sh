#!/bin/sh
source /etc/profile

####################source#########
####/source/ugctail源数据目录

######################local############
#把源数据从hdfs上读取到一个大文件里，此目录存储之。例如：recommender.log.$yesterday
echo "begin to mkdir -p /opt/data/temp/"
mkdir -p /opt/data/temp/
echo "mkdir -p /opt/data/temp/ ok"
#存放执行mr的jar包
mkdir -p /work/recommender

######################hdfs##############
#把local中的/opt/data/temp/*.log 存到hdfs上对应目录
hdfs dfs -mkdir -p /recommender/tmp
#将当天之前的历史的数据清理一次存放到此处，方便下次mr操作。
hdfs dfs -mkdir -p /recommender/data/history/
#将当天的数据和history的数据清洗后的最终数据存储目录供mahout读取
hdfs dfs -mkdir -p /recommender/result_temp/
hdfs dfs -mkdir -p /recommender/result/

#mahout存储处理之后的结果
hdfs dfs -mkdir -p /recommender/output/

#清洗今天之前的数据存入history目录
j=1
k=0
#初始化第一天统计前一天的结果目录
let init_date=j+1
stat_init=`date -d "$init_date days ago"  +"%Y%m%d"`

#初始化此文件夹，里面没有数据，主要是为了计算后一天job时传入此路径。
hdfs dfs -mkdir  /recommender/result/${stat_init}

while [ $j -gt $k ]; do
    let before=j+1
    stat_date=`date -d "$j days ago"  +"%Y%m%d"`
    stat_before=`date -d "$before days ago"  +"%Y%m%d"`

    hdfs dfs -test -e /source/ugctail/${stat_date}
    if [ $? -eq 0 ];then
        rm -rf /opt/data/temp/recommender.log.$stat_date
        #读取当天的tail日志解析到一个文件中
        hdfs dfs -text /source/ugctail/${stat_date}/* > /opt/data/temp/recommender.log.$stat_date
        #上传解析后的文件
        hdfs dfs -rm /recommender/tmp/recommender.log.$stat_date
        hdfs dfs -rm -r /recommender/result/${stat_date}
        hdfs dfs -rm -r /recommender/result_temp/${stat_date}
        hdfs dfs -put /opt/data/temp/recommender.log.$stat_date /recommender/tmp/recommender.log.$stat_date
        hadoop jar /work/recommender/remd-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar com.xiaoxiaomo.hadoop.mr.RecommendCleaner /recommender/tmp/recommender.log.$stat_date /recommender/result/${stat_before}/ /recommender/result_temp/${stat_date}/ /recommender/result/${stat_date}/
    else
        echo "/source/ugctail/${stat_date} is not exits"
    fi




    let j=j-1
done
#将最终的结果存入历史记录
let date_end=j+1
hdfs dfs -rm /recommender/data/history/*
stat_date_end=`date -d "$date_end days ago"  +"%Y%m%d"`
hdfs dfs -cp /recommender/result/${stat_date_end}/* /recommender/data/history/