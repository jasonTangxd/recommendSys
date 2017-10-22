#!/bin/sh 
#引入环境变量
source /etc/profile
#定义一个遍历起始日期的变量
j=1
#定义一个终结日期变量
k=0

######windows 下的脚本文件ff为doc在unix执行会有问题。
#查看文件格式 (:set ff)
#设置格式 方式：  (:set ff=unix)

#j k 来控制指定的日期 时间段
while [ $j -gt $k ]
do
stat_date=`date -d" $j days ago " +"%Y%m%d"`
echo $stat_date
hdfs dfs -test -e /clean/access/$stat_date
result=$?
if [ $result -eq 0 ] ; then
	hdfs dfs -rm -r /clean/access/$stat_date
fi

hadoop jar /opt/work/kfp-0.0.1-SNAPSHOT-jar-with-dependencies.jar crxykfp.kfp.mr.AccessLogCleaner /source/access/$stat_date /clean/access/$stat_date >/opt/work/logs/access.log.$stat_date 2>&1 &

#mr 清洗


#
#TODO

let j=j-1

done