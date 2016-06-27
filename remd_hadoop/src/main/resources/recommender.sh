#!/bin/sh
source /etc/profile
#引入mysql连接
source /opt/conf/mysqlconn.conf
#define hadoop export mysql method
function handle(){
        date=$1
        path="/recommender/output/${date}/"
        echo $path
        hdfs dfs -test -e ${path}
        if [ $? -eq 0 ];then
            echo "开始执行exp4mysql ${date}"
               sqoop export \
	       --connect jdbc:mysql://$stat_ipaddr:$stat_port/$stat_dbname \
	       --username $stat_uname \
	       --password $stat_upwd \
	       --table t_user_item_top_offline  \
	       --export-dir $path \
	       --columns user_id,recommender \
	       --input-fields-terminated-by '\t' \
	       --input-lines-terminated-by '\n' 2>&1
            echo "结束执行exp4mysql ${date}"
        else
            echo "${path} is not exits"
        fi
}

j=1
k=0

while [ $j -gt $k ]; do
    let now=j-1
    stat_date=`date -d "$now days ago"  +"%Y%m%d"`
    stat_date_dir=`date -d "$now days ago"  +"%Y-%m-%d"`
    yesterday=`date -d "$j days ago"  +"%Y%m%d"`

    hour=`date +%H`
    if [ "$hour" == "00" ];
    then
    #计算前一整天的数据和历史数据并存入历史数据中一份
    #delete old temp data
    rm -rf /opt/data/temp/recommender.log.$yesterday
    
    #########################################将零散的hdfs的文件汇总 便于计算 begin###########################
    #读取前一天的tail日志解析到一个文件中
    hdfs dfs -text /source/ugctail/${yesterday}/* > /opt/data/temp/recommender.log.$yesterday
    #上传解析后的文件
    hdfs dfs -rm /recommender/tmp/recommender.log.$yesterday
    hdfs dfs -rm -r /recommender/result/${stat_date}
    hdfs dfs -put /opt/data/temp/recommender.log.$yesterday /recommender/tmp/recommender.log.$yesterday
    #########################################将零散的hdfs的文件汇总 便于计算 end ###########################
    
    hadoop jar /work/recommender/recommender-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar cn.zenith.recommender.mr.RecommenderCleaner /recommender/tmp/recommender.log.$yesterday /recommender/data/history/ /recommender/result_temp/${stat_date}/ /recommender/result/${stat_date}/
    #将结果覆盖下history目录
    hdfs dfs -rm /recommender/data/history/*
    hdfs dfs -cp /recommender/result/${stat_date}/* /recommender/data/history/
    else
    #非整点操作

    #计算当天的数据和历史的数据
    #delete old temp data
    rm -rf /opt/data/temp/recommender.log.$stat_date
    #读取当天的tail日志解析到一个文件中
    hdfs dfs -text /source/ugctail/${stat_date}/* > /opt/data/temp/recommender.log.$stat_date
    #上传解析后的文件
    hdfs dfs -rm /recommender/tmp/recommender.log.$stat_date
    hdfs dfs -rm -r /recommender/result/${stat_date}
    hdfs dfs -put /opt/data/temp/recommender.log.$stat_date /recommender/tmp/recommender.log.$stat_date
    
    hadoop jar /work/recommender/recommender-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar cn.zenith.recommender.mr.RecommenderCleaner /recommender/tmp/recommender.log.$stat_date /recommender/data/history/ /recommender/result_temp/${stat_date}/ /recommender/result/${stat_date}/
    fi
    #执行mahout计算结果
    hdfs dfs -rm -r /recommender/tmp/${stat_date}/tmp
    hdfs dfs -rm -r /recommender/output/${stat_date}
    mahout recommenditembased --input /recommender/result/${stat_date}/ --output /recommender/output/${stat_date}/ -s SIMILARITY_PEARSON_CORRELATION --tempDir /recommender/tmp/${stat_date}/tmp

    #导入mysql
    #delete
    mysql -h$stat_ipaddr -P$stat_port -u$stat_uname -p$stat_upwd -D$stat_dbname -e "TRUNCATE table t_user_item_top_offline;";
    #load
    handle ${stat_date_dir}

    let j=j-1
done