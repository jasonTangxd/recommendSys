package com.xiaoxiaomo.hadoop.mr;

import com.xiaoxiaomo.hadoop.entity.UgcLogEntity;
import com.xiaoxiaomo.hadoop.utils.RedisUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaoxiaomo on 2015/12/31.
 */
public class UgcLogCleaner {


    private static Logger logger=Logger.getLogger(UgcLogCleaner.class);

    public static class UgcMapper extends Mapper<LongWritable,Text,NullWritable,Text>{

    	private String ipInfo=null;
		private Integer appId=0;
		private Integer userId=0;
		private Long time=0l;
		private NullWritable nu=NullWritable.get();
		private Text result=new Text();
		private DateFormat format;
		private Jedis jedis;
		private String[] strs=null;
		private String[] ipStr=null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
        	format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jedis= RedisUtils.getJedis();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //1003    218.75.75.133   342e12e7-eb7f-4a8b-9764-9d18b2b7439e    f94e95f6-ed1a-4fc2-b9c3-bffc873a09c9    10709   {"ugctype":"fav","userId":"10709","item":"11"}      1       1454147875901
            //appid	ip	mid	seid	userid	param	time

            if (null!=value){

                strs=value.toString().split("\t");
                //清理垃圾数据
                if (strs.length==7){
                	//转换aappID
					try {
						appId=Integer.parseInt(strs[0]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					try {
						userId=Integer.parseInt(strs[4]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					
					try {
						time=Long.parseLong(strs[6]);
					} catch (Exception e) {
						logger.error(e.getMessage());
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
					ipInfo=jedis.get("ip:"+log.getIp());
					if(ipInfo!=null&&ipInfo.split("\t").length==2){
						ipStr=ipInfo.split("\t");
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
                        logger.error(ex);
                    }
                }
            }
   
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            format=null;
            jedis.close();
            logger=null;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        if (args.length<2){
            logger.error("Usage: ugcLog <in> [<in>...] <out>");
            System.exit(2);
        }

        Configuration configuration=new Configuration();
        Job job =Job.getInstance(configuration,"ugcLog");

        job.setJarByClass(UgcLogCleaner.class);
        job.setMapperClass(UgcMapper.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
