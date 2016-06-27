package com.xxo.hadoop.mr;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class RecommendJobTest {

	//日志打印对象
	private static Logger logger=Logger.getLogger(RecommendJobTest.class);
	
	
	public static class RecommendMapper extends Mapper<LongWritable, Text, Text, DoubleWritable>{
		
		private String[] lines;
		private JSONObject parseObject;
		private Integer userId;
		private Integer itemId;
		private String ugcType;
		private Double score;
		private Text userItem=new Text();
		private DoubleWritable scoreValue=new DoubleWritable();
		
		
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			//1000    125.70.229.30   7bbfa6e0-e2cc-4f82-9ac8-a6bc1d203324    617247c1-e653-4217-b52f-b49a1640cb38    30303   {"ugctype":"fav","userId":"10608","item":"10"}      1466697610466
			if(value!=null){
				lines=value.toString().split("\t");
				//如果输入数据为未清洗的日志数据
				if(lines.length>=7){
					//"ugctype":"fav","userId":"10608","item":"10"
					parseObject = JSONObject.parseObject(lines[5]);
					//resolve userId
					try {
						userId=parseObject.getInteger("userId");
					} catch (Exception e) {
						logger.error(e.getMessage());
						userId=null;
					}
					//resolve itemId  param注意是item
					try {
						itemId=parseObject.getInteger("item");
					} catch (Exception e) {
						logger.error(e.getMessage());
						itemId=null;
					}
					if(userId!=null&&userId!=0&&itemId!=null&&itemId!=0){
						
						//resolve ugcType
						ugcType=parseObject.getString("ugctype");
						//根据ugcType 设置权重
						switch (ugcType) {
						case "fav":score=3d; break;
						case "recharge":score=2d; break;
						case "consumer":score=1d; break;
						default:score=0d;break;
						}
						
						userItem.set(String.format("%s\t%s", userId,itemId));
						scoreValue.set(score);
						context.write(userItem, scoreValue);
					}
				}else{
					
					//如果输入的是历史的已经清洗之后的数据
					if(lines.length==3){
						userItem.set(String.format("%s\t%s", lines[0],lines[1]));
						try {
							score=Double.parseDouble(lines[2]);
						} catch (Exception e) {
							logger.error(e.getMessage());
							score=0d;
						}
						 //TODO 可以在这个地方为历史的信息加入权重
						scoreValue.set(score);
						context.write(userItem, scoreValue);
					}
				}
				
			}
			
		}
	}
	
	
	public static class RecommendReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable>{
		
		private static Double scores=0d;
		private static DoubleWritable scoreValues=new DoubleWritable();
		
		//计数器 记录userItem的评分的最大值 便于最后的控制在100以内。
		public enum Counts{MAX}
		//public enum CountNew{LITTLE}
	
		@Override
		protected void reduce(Text userItem, Iterable<DoubleWritable> userItemScores,Context context)
				throws IOException, InterruptedException {
			
			
			//重置全局变量scores
			scores=0d;
			// TODO Auto-generated method stub
			for (DoubleWritable userItemScore : userItemScores) {
				scores+=userItemScore.get();
			}
			Counter counter = context.getCounter(Counts.MAX);
			
		
			if(counter.getValue()<scores.longValue()){
				counter.setValue(scores.longValue());
			}
			scoreValues.set(scores);
			context.write(userItem, scoreValues);
		}
	}
	
	
	//数据的分值控制
	public static class FilterMapper extends Mapper<LongWritable, Text, NullWritable, Text>{
		
		private long recommend_score_max;
		private String[] lines;
		private Integer userId;
		private Integer itemId;
		private Double score;
		private DecimalFormat format;
		private Text result=new Text();
		
		@Override
		protected void setup(
				Context context)
				throws IOException, InterruptedException {
			recommend_score_max=Long.parseLong(context.getConfiguration().get("recommend_score_max"));
			format=new DecimalFormat("#.00");
		}
		
		
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			if(value!=null&&value.toString()!=null){
				lines=value.toString().split("\t");
				if(lines.length==3){
					
					try {
						userId=Integer.parseInt(lines[0]);
					} catch (Exception e) {
						userId=null;
						logger.error(e.getMessage());
					}
					try {
						itemId=Integer.parseInt(lines[1]);
					} catch (Exception e) {
						itemId=null;
						logger.error(e.getMessage());
					}
					try {
						score=Double.parseDouble(lines[2]);
					} catch (Exception e) {
						score=null;
						logger.error(e.getMessage());
					}
					if(userId!=null&&userId!=0&&itemId!=null&&itemId!=0&&score!=null){
						if(recommend_score_max>100){
							result.set(String.format("%s\t%s\t%s", userId,itemId,format.format((100*score)/recommend_score_max)));
							context.write(NullWritable.get(), result);
						}else{
							//如果最大值小于等于100则源数据返回
							context.write(NullWritable.get(), value);
						}
					}
				}
			}	
		}
	
	}
	
	
	public static void main(String[] args) throws Exception {
		
		int argLen=args.length;
		if(argLen<3){
			logger.error("need atleast more then 3 args    inputdir  ouputdir tempdir");
			System.exit(2);
		}
		
		
		Job job=Job.getInstance(new Configuration(), "recommendJob");
		job.setJarByClass(RecommendJobTest.class);
		job.setMapperClass(RecommendMapper.class);
		job.setReducerClass(RecommendReducer.class);
		
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		
		//输入 inputdir
		for (int i = 0; i < argLen-2; i++) {
			FileInputFormat.addInputPath(job, new Path(args[i]));
		}
		//输出到temp目录里 tempdir
		FileOutputFormat.setOutputPath(job, new Path(args[argLen-1]));
		
		boolean isSucess = job.waitForCompletion(true);
		//System.exit(isSucess?0:1);
		
		if(isSucess){
			
			
			
			long scoreMax=job.getCounters().findCounter(RecommendReducer.Counts.MAX).getValue();
			
			
			Configuration configuration = new Configuration();
			configuration.set("recommend_score_max", String.valueOf(scoreMax));
			Job jobFilter=Job.getInstance(configuration, "recommendFilterJob");
			
			jobFilter.setJarByClass(RecommendJobTest.class);
			jobFilter.setMapperClass(FilterMapper.class);
			
			jobFilter.setOutputKeyClass(NullWritable.class);
			jobFilter.setOutputValueClass(Text.class);
			
			//此输入是前一个job的输出目录即 tempdir
			FileInputFormat.addInputPath(jobFilter, new Path(args[argLen-1]));
			//输出  ouputdir
			FileOutputFormat.setOutputPath(jobFilter, new Path(args[argLen-2]));
			
			//提交job
			System.exit(jobFilter.waitForCompletion(true)?0:1);
			
			
			
		}
		
		
		
	}
	
	
}
