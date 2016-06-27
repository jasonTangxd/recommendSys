#!/bin/sh
source /etc/profile

#input /source/ugctail/20160113/* #output /result/ugctail/20160113/*
#crontab 设置
# 凌晨 0:11
#
i=1
j=0

#while循环执行多次job
while [ $i -gt $j ]; do
    stat_date=`date -d "$i days ago"  +"%Y%m%d"`

    stat_date_dir=`date -d "$i days ago"  +"%Y-%m-%d"`

    #delete old temp data
    rm -rf /opt/data/temp/Tail.log.$stat_date

    hdfs dfs -text /source/ugctail/${stat_date}/* > /opt/data/temp/Tail.log.$stat_date

    hdfs dfs -put /opt/data/temp/Tail.log.$stat_date /source/ugctail/Tail.log.$stat_date

    echo "hadoop jar /work/recommender/recommender-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar cn.zenith.recommender.mr.UgcLogCleaner /source/ugctail/Tail.log.$stat_date /result/ugctail/${stat_date}/"

    #delete hive data
    hdfs dfs -rm -r /warehouse/hive/warehouse/ent.db/t_origin_ugc_tail_online/datecol=$stat_date_dir

    #load data to hive
    echo `hive -e "use ent;load data inpath '/result/ugctail/${stat_date}/*' into table ent.t_origin_ugc_tail_online partition(datecol='$stat_date_dir')"`

    #delete old temp data
    rm -rf /opt/data/temp/Tail.log.$stat_date

    let i=i-1
done



