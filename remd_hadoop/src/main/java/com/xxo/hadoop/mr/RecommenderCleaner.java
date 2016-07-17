package com.xxo.hadoop.mr;

import com.alibaba.fastjson.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;





import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by xiaoxiaomo on 2015/12/31.
 */
public class RecommenderCleaner {


    private static Logger logger=Logger.getLogger(RecommenderCleaner.class);

    private static DecimalFormat decimalFormat=new DecimalFormat("#.00");
    public static class RecommenderMapper
            extends Mapper<LongWritable,Text,Text,DoubleWritable>{

        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //1003    218.75.75.133   342e12e7-eb7f-4a8b-9764-9d18b2b7439e    f94e95f6-ed1a-4fc2-b9c3-bffc873a09c9    10709   {"ugctype":"fav","userId":"10709","item":"11"}       1454147875901
            //appid	ip	mid	seid	userid	param	time

            if (null!=value){

                String[] UgcLogs=value.toString().split("\t");
                //清理垃圾数据
                if (UgcLogs.length>=7){
                    try {
                        Integer userId=Integer.parseInt(UgcLogs[4]);
                        String param=UgcLogs[5];
                        JSONObject jsonObject = JSONObject.parseObject(param);
                        String itemId= (String) jsonObject.get("item");
                        if (itemId!=null){
                            String ugctype= (String) jsonObject.get("ugctype");
                            Double score;
                            //行为权重
                            switch (ugctype){
                                case "consumer":{score=1d;break;}
                                case "recharge":{score=2d;break;}
                                case "fav":{score=3d;break;}
                                default:score=0d;
                            }
                            context.write(new Text(userId+"\t"+itemId),new DoubleWritable(score));

                        }
                    }
                    catch (Exception ex){
                        logger.error(ex);
                    }
                }else{
                    if (UgcLogs.length==3){
                        context.write(new Text(UgcLogs[0]+"\t"+UgcLogs[1]),new DoubleWritable(Double.parseDouble(UgcLogs[2])));
                    }

                }
            }
        }
    }
    public static class RecommenderReducer
            extends Reducer<Text,DoubleWritable,NullWritable,Text> {
        ;
        Double result=0d;
        long max=0l;
        public enum Counters { MAX }
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {

            for (DoubleWritable val : values) {
            	result+=val.get();
                }
            if(context.getCounter(Counters.MAX).getValue()<result.longValue()) {
            	context.getCounter(Counters.MAX).setValue(result.longValue());
            }
            context.write(NullWritable.get(), new Text(String.format("%s\t%s",key.toString(),result)));
        }
    }

    
    public static class FilterMapper extends Mapper<LongWritable, Text, NullWritable, Text>{
    	
    	@Override
    	protected void map(LongWritable key, Text value,Context context)
    			throws IOException, InterruptedException {
    		long max=Long.parseLong(context.getConfiguration().get("recommender_score_max"));
    		String[] strs= value.toString().split("\t");
    		if(strs.length==3&&max>100){
    			context.write(NullWritable.get(), new Text(String.format("%s\t%s\t%s",strs[0],strs[1],decimalFormat.format(Double.parseDouble(strs[2])*100.00/max))));
    		}else{
    			context.write(NullWritable.get(), value);
    		}
    	}
    	
    	
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        if (args.length<3){
            logger.error("Usage: recommender <history_in> <in> <temp> <out>");
            System.exit(2);
        }

        Configuration configuration=new Configuration();

        Job job =Job.getInstance(configuration,"Recommender");
        job.setJarByClass(RecommenderCleaner.class);
        job.setMapperClass(RecommenderMapper.class);
        job.setReducerClass(RecommenderReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        //可以设置多输入路径
//        MultipleInputs.addInputPath( job , new Path(args[0]) , TextInputFormat.class);
//        MultipleInputs.addInputPath( job , new Path(args[1]) , DBInputFormat.class);
//        MultipleInputs.addInputPath( job , new Path(args[2]) , TextInputFormat.class);
        //history input
        for (int i = 0; i < args.length-2; i++) {
            FileInputFormat.addInputPath(job,new Path(args[i]));
        }
        //output
        FileOutputFormat.setOutputPath(job, new Path(args[args.length-2]));
        //System.exit(job.waitForCompletion(true) ? 0 : 1);
        boolean success=job.waitForCompletion(true);
        if(success){
        	configuration=new Configuration();
        	configuration.set("recommender_score_max", String.valueOf(job.getCounters()
        			.findCounter(RecommenderReducer.Counters.MAX).getValue()));
            Job filter =Job.getInstance(configuration,"RecommenderFilter");
            filter.setJarByClass(RecommenderCleaner.class);
            filter.setMapperClass(FilterMapper.class);
            filter.setOutputKeyClass(NullWritable.class);
            filter.setOutputValueClass(Text.class);
        	
            FileInputFormat.addInputPath(filter,new Path(args[args.length-2]));
            //output
            FileOutputFormat.setOutputPath(filter, new Path(args[args.length-1]));
            System.exit(filter.waitForCompletion(true) ? 0 : 1);
        }else{
        	System.exit(1);
        }
        /**
         *
         * max
         * sun
         *
         * (100*sun)/max
         *
         *
         *
         */
        
    }

}
