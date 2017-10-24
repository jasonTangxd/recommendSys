#!/bin/sh 
#引入环境变量
source /etc/profile
source /opt/work/mysqlConn.conf

#30 0 * * * sh /root/exportUserInfo.sh > /root/logs/exportUserInfo.log 2>&1 &

#定义一个遍历起始日期的变量
j=1
#定义一个终结日期变量
k=0

#j k 来控制指定的日期 时间段
while [ $j -gt $k ]
do
yesterday=`date -d" $j days ago " +"%Y-%m-%d"`
echo $yesterday

#设置一个临时变量并赋值为 j-1
let temp=j-1
stat_date=`date -d" $temp days ago " +"%Y-%m-%d"`

echo $stat_date
#sqoop导入hdfs
sqoop import \
--connect jdbc:mysql://$stat_ipaddr:$stat_port/$stat_dbname?charset=utf-8 \
--username $stat_uname \
--password $stat_upwd \
--target-dir /t_user_info/$yesterday \
--query "select * from t_user_info where time >='$yesterday' and time <'$stat_date' and \$CONDITIONS  " \
--fields-terminated-by '\t' -m 2 \
--split-by id \
--delete-target-dir

let j=j-1

done