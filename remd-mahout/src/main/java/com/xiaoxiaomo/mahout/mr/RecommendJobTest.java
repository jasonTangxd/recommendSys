package com.xiaoxiaomo.mahout.mr;

import com.alibaba.fastjson.JSONObject;
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

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 推荐项目 - 离线项目清洗
 * Created by xiaoxiaomo on 2015/6/24.
 */
public class RecommendJobTest {

    private static Logger logger = Logger.getLogger( RecommendJobTest.class ) ;
    public static class recommendMapper extends Mapper<LongWritable , Text , Text , DoubleWritable>{

        private String[] lines ;
        private JSONObject object  ;
        private String ugcType ;
        private Integer userId ;
        private Integer item ;
        private Double score ;

        //输出类型
        private Text k2 = new Text();
        private DoubleWritable  v2 = new DoubleWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if( value != null ){
                //切分
                //1003    218.75.75.133   342e12e7-eb7f-4a8b-9764-9d18b2b7439e    f94e95f6-ed1a-4fc2-b9c3-bffc873a09c9    10709   {"ugctype":"fav","userId":"10709","item":"11"}       1454147875901
                //appid	ip	mid	seid	userid	param	time
                lines = value.toString().split("\t");
                if( lines.length >=7 ){
                    //{"ugctype":"fav","userId":"10709","item":"11"}
                    object =  JSONObject.parseObject(lines[5]) ;

                    //Key : userId和item
                    userId = object.containsKey( "userId" )?object.getInteger("userId"):null ;
                    item = object.containsKey( "item" )?object.getInteger("item"):null ;

                    //Value : 通过类型设置权重(分数)
                    ugcType = object.getString("ugctype") ;
                    switch ( ugcType ){
                        case "fav" : score = 3d ; break;
                        case "recharge" : score = 2d ; break;
                        case "consumer" : score = 1d ; break;
                        default:score = 0d ;
                    }

                    k2.set( new String( String.format( "%s\t%s" , userId , item ) )  );
                    v2.set( score );
                    context.write( k2 , v2 );
                } else {
                    if( lines.length == 3  ){
                        k2.set(new String( String.format( "%s\t%s", lines[0],lines[1] ) ));
                        v2.set( Double.valueOf( lines[3] ) );
                        context.write( k2 ,v2 );
                    }
                }

            }
        }
    }


    public static class recommendFilterMapper extends Mapper<LongWritable , Text , NullWritable , Text> {

        private Long recommend_score_max ;
        private String[] lines ;
        private Integer userId ;
        private Integer itemId ;
        private Double score ;
        private DecimalFormat format ;
        private Text values = new Text() ;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            recommend_score_max = Long.valueOf(
                    context.getConfiguration().get("recommend_score_max")) ;
            format = new DecimalFormat("#.00") ;
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            lines = value.toString().split("\t") ;

            if( lines.length == 3 ){
                try{
                    userId = Integer.parseInt( lines[0] ) ;
                }catch (Exception e){
                    userId = null ;
                    logger.error( e.getMessage() );
                }
                try{
                    itemId = Integer.parseInt( lines[1] ) ;
                }catch (Exception e){
                    itemId = null ;
                    logger.error( e.getMessage() );
                }
                try{
                    score = Double.parseDouble( lines[2] ) ;
                }catch (Exception e){
                    score = null ;
                    logger.error( e.getMessage() );
                }
            }

            //
            if( userId != null && itemId != null && score != null ){
                if( recommend_score_max > 100 ){

                    values.set(String.format("%s\t%s\t%s", userId, itemId, format.format( (100*score)/recommend_score_max )) );
                    context.write(NullWritable.get(), values);
                }else{
                    //如果最大值小于等于100则原数据返回
                    context.write(NullWritable.get(), value);
                }
            }


        }
    }

    public static class recommendReduce extends Reducer< Text , DoubleWritable , Text , DoubleWritable >{

        private Double scores = 0d ;
        private DoubleWritable scoresValues = new DoubleWritable() ;
        //计数器
        private enum Counts{MAX}

        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {

            scores = 0d ; //重置
            for (DoubleWritable score : values) {
                scores += score.get() ;
            }

            //找到当前评分的最大值
            Counter counter = context.getCounter(Counts.MAX);
            if( counter.getValue() < scores.longValue()  ){
                counter.setValue( scores.longValue() );
            }

            scoresValues.set(scores);
            context.write( key , scoresValues );
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        int aLeng = args.length ;
        if( aLeng < 3 ){
            logger.error( "参数异常,need alteast more than 3 args input output temmdir" );
            System.exit(2);
        }

        //1. 获取一个Job,并设置
        Job job = Job.getInstance(new Configuration(), "RecommendJob");
        job.setJarByClass(RecommendJobTest.class);

        job.setMapperClass(recommendMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setReducerClass(recommendReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass( DoubleWritable.class );

        //输入 . 支持多个输入路径
        for (int i = 0; i < aLeng -2 ; i++) {
            FileInputFormat.addInputPath( job , new Path(args[i]));
        }

        //输出
        FileOutputFormat.setOutputPath(job, new Path(args[aLeng - 1]));

        boolean isSucess = job.waitForCompletion(true);


        //jobFilter
        if( isSucess ){
            //1. 获取一个Job,并设置
            Configuration conf = new Configuration();
            Job jobFilter = Job.getInstance(conf, "RecommendFilterJob");
//            job.getCounters().findCounter( RecommendM )
//            conf.set( "recommend_score_max" , String.valueOf() );

            jobFilter.setJarByClass(RecommendJobTest.class);

            jobFilter.setMapperClass(recommendFilterMapper.class);
            jobFilter.setMapOutputKeyClass(Text.class);
            jobFilter.setMapOutputValueClass(DoubleWritable.class);

            //此输入是前一个job的输出目录
            FileInputFormat.addInputPath( jobFilter , new Path( args[aLeng - 2] ));
            //输出outputDir
            FileOutputFormat.setOutputPath( jobFilter , new Path( args[aLeng - 1] ));

            isSucess = job.waitForCompletion(true);
//        System.exit( isSucess ? 0 : 1 );

        }

    }
}
