package com.xiaoxiaomo.hadoop.mr;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;




/**
 *
 * 用户行为数据清洗
 *
 * hadoop jar remd-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar com.xiaoxiaomo.hadoop.mr.RecommendJobTest.RecommendJobTest /user/root/ugctail.log /recommender/result/20171022/
 *
 * Created by xiaoxiaomo on 2015/12/31.
 */
public class RecommendJobTest extends Configured implements Tool {

	private static Logger LOG = Logger.getLogger(RecommendJobTest.class);

	@Override
	public int run(String[] args) throws Exception {

		// conf优化设置
		Configuration conf = getConf();
		conf.setBoolean("mapred.map.tasks.speculative.execution", false);
		conf.setBoolean("mapred.reduce.tasks.speculative.execution", false);
		conf.setStrings("mapred.child.java.opts","-Xmx1024m");
		conf.setInt("mapred.map.tasks",1); //自己的测试机设置小一点


		Job job = Job.getInstance(conf,"Recommend");
		job.setNumReduceTasks(1);
		job.setJarByClass(RecommendJobTest.class);

		job.setMapperClass(RecommendJobTest.RecommenderMapper.class);
		job.setReducerClass(RecommendJobTest.RecommenderReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);


		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		//history input
		FileInputFormat.addInputPath(job,new Path(args[0]));

		//output
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}


	public static class RecommenderMapper extends Mapper<LongWritable,Text,Text,DoubleWritable>{


		/**
		 * 1003    218.75.75.133   342e12e7-eb7f-4a8b-9764-9d18b2b7439e    f94e95f6-ed1a-4fc2-b9c3-bffc873a09c9    10709   {"ugctype":"fav","userId":"10709","item":"11"}       1454147875901
		 * appid	ip	mid	seid	userid	param	time
		 *
		 * @param key
		 * @param value
		 * @param context
		 * @throws IOException
		 * @throws InterruptedException
		 */
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			if ( null == value ) {    // 清理null数据
				return ;
			}

			String[] UgcLogs = value.toString().split("\t");
			//清理垃圾数据
			if ( UgcLogs.length >= 7 ){
				try {
					Integer userId = Integer.parseInt(UgcLogs[4]);
					String param = UgcLogs[5];  //param为一个json字符串
					JSONObject jsonObject = JSONObject.parseObject(param);
					String itemId =  (String) jsonObject.get("item");
					if ( itemId != null ){
						String ugcType = (String) jsonObject.get("ugctype");
						Double score;

						//行为权重
						switch ( ugcType ){
							case "consumer": score = 1d ; break;
							case "recharge": score = 2d ; break;
							case "fav": score = 3d ; break;
							default: score = 0d;
						}
						context.write(new Text(userId+"\t"+itemId),new DoubleWritable(score));

					}
				}
				catch (Exception e){
					LOG.error("清洗数据异常",e);
				}
			}else{
				if ( UgcLogs.length == 3){
					context.write(new Text(UgcLogs[0]+"\t"+UgcLogs[1]),new DoubleWritable(Double.parseDouble(UgcLogs[2])));
				}

			}
		}
	}
	public static class RecommenderReducer
			extends Reducer<Text,DoubleWritable,Text,DoubleWritable> {

		Double result = 0d;
		long max = 0l;
		public enum Counters { MAX }
		protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {

			for ( DoubleWritable val : values ) {
				result += val.get();
			}

			if ( context.getCounter(RecommendJobTest.RecommenderReducer.Counters.MAX).getValue() < result.longValue() ) {
				context.getCounter(RecommendJobTest.RecommenderReducer.Counters.MAX).setValue(result.longValue());
			}
			context.write(key,new DoubleWritable(result));
		}
	}




	public static void main(String[] args)  throws Exception {
		if ( args.length < 2 ){
			LOG.error("Usage: recommend <in> <out>");
			System.exit(2);
		}
		int exitCode = ToolRunner.run(new RecommendJobTest(), args);
		System.exit(exitCode);
	}

}
