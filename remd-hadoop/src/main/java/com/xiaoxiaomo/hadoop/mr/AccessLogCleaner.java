package com.xiaoxiaomo.hadoop.mr;

import com.xiaoxiaomo.hadoop.entity.UserAccessLogEntity;
import com.xiaoxiaomo.hadoop.utils.RedisUtils;
import com.xiaoxiaomo.hadoop.entity.UserAgentEntity;
import com.xiaoxiaomo.hadoop.utils.UserAgentUtils;
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

public class AccessLogCleaner {

	private static Logger logger=Logger.getLogger(AccessLogCleaner.class);
	
	public static void main(String[] args) throws Exception {
		
		if (args.length<2) {
			logger.error("args need at least 2");
			//异常退出
			System.exit(2);
		}
		
		
		Configuration conf=new Configuration();
		Job job=Job.getInstance(conf, "accessLogClean");
		job.setJarByClass(AccessLogCleaner.class);
		job.setMapperClass(AccessMapper.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		//输入
		for (int i = 0; i < args.length-1; i++) {
			FileInputFormat.addInputPath(job, new Path(args[i]));
		}
		//输出 
		FileOutputFormat.setOutputPath(job, new Path(args[args.length-1]));
		
		//程序退出标识
		System.exit(job.waitForCompletion(true)?0:1);
		
		
	}
	
	public static class AccessMapper extends Mapper<LongWritable, Text, NullWritable, Text>{
	
		private String line="";
		private String[] strs=null;
		private String[] requestStr=null;
		private String[] ipStr=null;
		private String ipInfo=null;
		private Integer appId=0;
		private Integer userId=0;
		private Integer loginType=0;
		private Integer status=0;
		private Long time=0l;
		private UserAgentEntity userAgent=null;
		private NullWritable nu=NullWritable.get();
		private Text result=new Text();
		private DateFormat format;
		private Jedis jedis;
		@Override
		protected void setup(
				Context context)
				throws IOException, InterruptedException {
			//初始化工具
			format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			jedis= RedisUtils.getJedis();
		}
		
		@Override
		protected void map(LongWritable key, Text value,
				Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			//appid	ip	mid	userid	login_type	request		status	http_referer	user_agent	time

			if (null!=value&&value.toString().length()>0) {
				line=value.toString();
				strs=line.split("\t");
				//过滤错误日志
				if(strs.length==10){
					//转换aappID
					try {
						appId=Integer.parseInt(strs[0]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					try {
						userId=Integer.parseInt(strs[3]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					try {
						loginType=Integer.parseInt(strs[4]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					try {
						status=Integer.parseInt(strs[6]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					try {
						time=Long.parseLong(strs[9]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					
					UserAccessLogEntity log=new UserAccessLogEntity(appId, strs[1], strs[2], userId, loginType, strs[5], status, strs[7], strs[8], time);
					
					//解析request
					if(null!=log.getRequest()&&log.getRequest().split(" ").length==3){
						requestStr=log.getRequest().split(" ");
						log.setMethod(requestStr[0]);
						log.setPath(requestStr[1]);
						log.setHttpVersion(requestStr[2]);
					}
					//解析useragent
					userAgent = UserAgentUtils.getUserAgent(log.getUserAgent());
					if(userAgent!=null){
						log.setIeType(userAgent.getBrowserType());
					}
					//time
					log.setDateTime(format.format(new Date(log.getTime())));
					//解析ip
					ipInfo=jedis.get("ip:"+log.getIp());
					if(ipInfo!=null&&ipInfo.split("\t").length==2){
						ipStr=ipInfo.split("\t");
						log.setProvince(ipStr[0]);
						log.setCity(ipStr[1]);
					}
					
					result.set(log.toString());
					context.write(nu,result);
				}
				
				
				
			}
			
			
		}
		
	
		
		@Override
		protected void cleanup(
				Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			logger=null;
			format=null;
			jedis.close();
		}
		
		
		
	}
	
	
	
}
