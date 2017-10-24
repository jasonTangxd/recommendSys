package com.xiaoxiaomo.hadoop.mr;

import com.xiaoxiaomo.hadoop.entity.UgcLogEntity;
import com.xiaoxiaomo.hadoop.utils.IPUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * hadoop jar remd-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar com.xiaoxiaomo.hadoop.mr.UgcLogCleaner /user/root/ugchead.log  /result/ugctail/20171023/
 *
 * Created by xiaoxiaomo on 2015/12/31.
 */
public class UgcLogCleaner extends Configured implements Tool {

    private static Logger LOG = Logger.getLogger(UgcLogCleaner.class);

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        Job job =Job.getInstance(conf,"ugcLog");

        job.setJarByClass(UgcLogCleaner.class);
        job.setMapperClass(UgcMapper.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class UgcMapper extends Mapper<LongWritable,Text,NullWritable,Text>{

        private String ipInfo = null;
        private Integer appId = 0;
        private Integer userId = 0;
        private Long time = 0l;
        private NullWritable nu = NullWritable.get();
        private Text result = new Text();
        private DateFormat format;
        private Jedis jedis;
        private String[] strs = null;
        private String[] ipStr = null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            jedis= RedisUtils.getJedis();
            IPUtils.load(IPUtils.class.getClassLoader().getResource("data/17monipdb.dat").getPath());
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //1003    218.75.75.133   342e12e7-eb7f-4a8b-9764-9d18b2b7439e    f94e95f6-ed1a-4fc2-b9c3-bffc873a09c9    10709   {"ugctype":"fav","userId":"10709","item":"11"}      1454147875901
            //appid	ip	mid	seid	userid	param	time

            if ( null != value ){

                strs=value.toString().split("\t");
                //清理垃圾数据
                if ( strs.length >= 7 ){
                    //转换aappID
                    try {
                        appId=Integer.parseInt(strs[0]);
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
                    try {
                        userId=Integer.parseInt(strs[4]);
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
					
                    try {
                        time=Long.parseLong(strs[6]);
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
                	
                    try {
                        //创建log
                        UgcLogEntity log=new UgcLogEntity(appId
                                ,strs[1]
                                ,strs[2]
                                ,strs[3]
                                ,userId
                                ,strs[5]
                                ,time);
                        //解析ip
//					    ipInfo = jedis.get("ip:"+log.getIp()); //IP可以初始化到Redis TODO
                        ipStr = IPUtils.find(log.getIp());
                        if( ipStr != null && ipStr.length >= 2 ){
//                            ipStr=ipInfo.split("\t");
                            log.setProvince(ipStr[0]);
                            log.setCity(ipStr[1]);
                        }
                        //time
                        if (null!=log.getTime()){
                            log.setDateTime(format.format(new Date(log.getTime())));
                        }
                    
                        result.set(log.toString());
                        context.write(nu,result);
                    }
                    catch (Exception ex){
                        LOG.error(ex);
                    }
                }
            }
   
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            format=null;
//            jedis.close();
            LOG=null;
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length<2){
            LOG.error("Usage: ugcLog <in> [<in>...] <out>");
            System.exit(2);
        }
        int exitCode = ToolRunner.run(new UgcLogCleaner(), args);
        System.exit(exitCode);

    }

}
