package com.xiaoxiaomo.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by xiaoxiaomo on 2015/6/23.
 */
public class MahoutDemo01 {

    DataModel model1 ;
    DataModel model2 ;

    @Before
    public void InitData() throws IOException {

        String path = HelloMahout.class.getClassLoader().getResource("data/info.csv").getPath();

        //1. 数据模型File Model
        model1 = new FileDataModel(new File(path));

        //2. 每一个用户的喜好列表
        FastByIDMap<PreferenceArray> data = new FastByIDMap<>();

        //3. 组装第一个用户的喜好 4指用户偏好的物品个数
        GenericUserPreferenceArray array1 = new GenericUserPreferenceArray(4);
        array1.setUserID(0, 1);
        array1.setItemID(0, 101);
        array1.setValue(0, 5);

        array1.setUserID(1, 1);
        array1.setItemID(1, 102);
        array1.setValue(1, 4);

        array1.setUserID(2, 1);
        array1.setItemID(2, 103);
        array1.setValue(2, 2);

        array1.setUserID(3, 1);
        array1.setItemID(3, 105);
        array1.setValue( 3 , 5 );

        //3.1 组装第二个用户的喜好 ， 下面为3个物品偏好
        GenericUserPreferenceArray array2 = new GenericUserPreferenceArray(3);
        array2.set(  0 , new GenericPreference(2,101,5));
        array2.set(  1 , new GenericPreference(2,103,1));
        array2.set(  2 , new GenericPreference(2,104,2));

        //4. 将用户的偏好数据 组装到FastByIDMap
        data.put( 1 , array1 ) ;
        data.put( 2 , array2 ) ;

        model2 = new GenericDataModel(data) ;
    }

    @Test
    public void testSIm() throws TasteException {
        UserSimilarity userSimilarity = new CityBlockSimilarity(model1); //1/(1+d)
//        userSimilarity = new PearsonCorrelationSimilarity(model);

        //相似度
        double similarity = userSimilarity.userSimilarity(1, 2);
        System.out.println( similarity );

    }

    @Test
    public void testNei() throws TasteException {

        //相似度
        UserSimilarity userSimilarity = new CityBlockSimilarity(model1);
        System.out.println( "1 2 -> " +  userSimilarity.userSimilarity( 1 ,2 ));
        System.out.println( "1 3 -> " +  userSimilarity.userSimilarity( 1 ,3 ));
        System.out.println( "1 4 -> " +  userSimilarity.userSimilarity( 1 ,4 ));
        System.out.println( "1 5 -> " +  userSimilarity.userSimilarity( 1 ,5 ));



        //下面制定3个邻居 ， 邻居是根据相似度来算的
        System.out.println("=======================================================");
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(4, userSimilarity, model1); //1. 固定几个

        neighborhood = new ThresholdUserNeighborhood( 0.5 , userSimilarity , model1 ) ;//2. 固定相似度

        long[] longs = neighborhood.getUserNeighborhood(1);  //用户用户1的n个邻居
        for (long aLong : longs) {
            System.out.println( aLong );
        }
    }

    @Test
    public void testCommend() throws TasteException {
//        UserSimilarity userSimilarity = new CityBlockSimilarity(model1);
        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model1);
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(2, userSimilarity, model1); //1. 固定几个

        //搭建推荐对象
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model1, neighborhood, userSimilarity);

        List<RecommendedItem> recommend = recommender.recommend(1, 3);
        for (RecommendedItem item : recommend) {
            System.out.println(item);
        }

    }


    /**
     * 校验
     * @throws TasteException
     */
    @Test
    public void evaluteTest() throws TasteException {

        GenericRecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();

        //RecommenderBuilder
        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model1);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, userSimilarity, model1); //1. 固定几个

            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return new GenericUserBasedRecommender( dataModel , neighborhood ,userSimilarity );
            }
        };

        /**
         * RecommenderBuilder recommenderBuilder 构造Recommender对象
         * DataModelBuilder dataModelBuilder 构造model对象
         * DataModel dataModel 模型
         * IDRescorer rescorer 改变值
         * int at 每个用户推荐2个
         * double relevanceThreshold 这是一个阈值，不传为平均值加标准方差
         * double evaluationPercentage 取所有数据的多少。1.0表示100%即所有的数据
         */
        IRStatistics statistics = evaluator.evaluate(
                recommenderBuilder,
                null,
                model1,
                null,
                2 ,
                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                1.0);


        double precision = statistics.getPrecision(); //查准
        double recall = statistics.getRecall(); //查全
        System.out.println( String.format( "查准：%s , 查全：%s" , precision ,recall ) );
    }


    /**
     * 校验 ： 评分器
     * @throws TasteException
     */
    @Test
    public void evaluteScoreTest() throws TasteException {
        AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        //RecommenderBuilder
        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model1);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, userSimilarity, model1); //1. 固定几个

            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return new GenericUserBasedRecommender( dataModel , neighborhood ,userSimilarity );
            }
        };
        double evaluate = evaluator.evaluate(recommenderBuilder, null, model1, 0.7, 1.0);

    }

}
