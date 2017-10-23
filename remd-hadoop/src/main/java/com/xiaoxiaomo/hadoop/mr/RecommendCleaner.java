package com.xiaoxiaomo.hadoop.mr;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 *
 * 用户行为数据清洗
 *
 * 测试数据，使用remd-web中的 ugctail.log
 *
 * hadoop jar remd-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar com.xiaoxiaomo.hadoop.mr.RecommendCleaner /source/ugctail/20171022/events-.1508671857111.tmp /recommender/result_temp/20171022/ /recommender/result/20171022/
 *
 * Created by xiaoxiaomo on 2015/12/31.
 */
public class RecommendCleaner extends Configured implements Tool {

    private static Logger LOG = Logger.getLogger(RecommendCleaner.class);
    private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

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
        job.setJarByClass(RecommendCleaner.class);

        job.setMapperClass(RecommendMapper.class);
        job.setReducerClass(RecommendReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);


        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

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

        boolean success = job.waitForCompletion(true);
        if( !success ) {
            System.exit(1);
        }

        conf = new Configuration();
        long scoreMax=job.getCounters().findCounter(RecommendReducer.Counts.MAX).getValue();

        conf.set("recommend_score_max", String.valueOf(scoreMax));

        Job filter =Job.getInstance(conf,"RecommenderFilter");

        filter.setJarByClass(RecommendCleaner.class);
        filter.setMapperClass(FilterMapper.class);
        filter.setOutputKeyClass(NullWritable.class);
        filter.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(filter,new Path(args[args.length-2]));

        //output
        FileOutputFormat.setOutputPath(filter, new Path(args[args.length-1]));
        return filter.waitForCompletion(true) ? 0 : 1;
    }


    public static class RecommendMapper extends Mapper<LongWritable,Text,Text,DoubleWritable>{

        private String[] UgcLogs;
        private JSONObject parseObject;
        private Integer userId;
        private Integer itemId;
        private String ugcType;
        private Double score;
        private Text userItem = new Text();
        private DoubleWritable scoreValue = new DoubleWritable();

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

            UgcLogs = value.toString().split("\t");
            //清理垃圾数据
            if ( UgcLogs.length >= 7 ){
                try {
                    parseObject = JSONObject.parseObject(UgcLogs[5]); //为一个json字符串
                    userId = parseObject.getInteger("userId");
                    itemId = parseObject.getInteger("item");
                    if ( userId != null && itemId != null  ){
                        ugcType = parseObject.getString("ugctype");
                        //行为权重
                        switch ( ugcType ){
                            case "consumer":
                                score = 1d ;
                                break;
                            case "recharge":
                                score = 2d ;
                                break;
                            case "fav":
                                score = 3d ;
                                break;
                            default:
                                score = 0d;
                        }
                        userItem.set(String.format("%s\t%s", userId,itemId));
                        scoreValue.set(score);
                        context.write(userItem, scoreValue);
                    }
                }
                catch (Exception e){
                    LOG.error("清洗数据异常",e);
                }
            }else{

                //如果输入的是历史的已经清洗之后的数据
                if( UgcLogs.length == 3 ){
                    userItem.set(String.format("%s\t%s", UgcLogs[0],UgcLogs[1]));
                    try {
                        score=Double.parseDouble(UgcLogs[2]);
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                        score=0d;
                    }
                    scoreValue.set(score);
                    context.write(userItem, scoreValue);
                }

            }
        }
    }
    public static class RecommendReducer
            extends Reducer<Text,DoubleWritable,Text,DoubleWritable> {

        private static Double scores = 0d; // 评分
        public enum Counts{MAX}

        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {

            //重置全局变量scores
            scores = 0d;
            for ( DoubleWritable val : values ) {
                scores += val.get();
            }

            // 计数
            Counter counter = context.getCounter(Counts.MAX);
            if ( counter.getValue() < scores.longValue() ) {
                counter.setValue(scores.longValue());
            }
            context.write(key,new DoubleWritable(scores));
        }
    }

    
    public static class FilterMapper extends Mapper<LongWritable, Text, NullWritable, Text>{

        private long recommend_score_max;
        @Override
        protected void setup(
                Context context)
                throws IOException, InterruptedException {
            recommend_score_max=Long.parseLong(context.getConfiguration().get("recommend_score_max"));
        }

    	@Override
    	protected void map(LongWritable key, Text value,Context context)
    			throws IOException, InterruptedException {
    		String[] strs = value.toString().split("\t");
    		if(strs.length == 3 && recommend_score_max > 100){
    			context.write(NullWritable.get(), new Text(String.format("%s\t%s\t%s",strs[0],strs[1],decimalFormat.format(Double.parseDouble(strs[2])*100.00/recommend_score_max))));
    		}else{
    			context.write(NullWritable.get(), value);
    		}
    	}
    	
    	
    }

    public static void main(String[] args)  throws Exception {
        if ( args.length < 3 ){
            LOG.error("Usage: recommend <history_in> <in> <temp> <out>");
            System.exit(2);
        }
        int exitCode = ToolRunner.run(new RecommendCleaner(), args);
        System.exit(exitCode);
    }

}
