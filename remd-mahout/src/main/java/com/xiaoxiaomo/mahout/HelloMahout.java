package com.xiaoxiaomo.mahout;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;

/**
 * Created by xiaoxiaomo on 2015/6/22.
 */
public class HelloMahout {
	private static Logger logger=Logger.getLogger(HelloMahout.class);

	public static void main(String[] args) {

		try {
			String path = HelloMahout.class.getClassLoader().getResource("data/info.csv").getPath();

			//读取数据
			DataModel model = new FileDataModel(new File(path));
			// 相似度
			UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
			// 邻域
			UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(3,userSimilarity, model);
			// 构建推荐引擎
			Recommender recommender = new GenericUserBasedRecommender(model,userNeighborhood, userSimilarity);
			// 进行推荐
			List<RecommendedItem> recommend = recommender.recommend(1, 5);
			for (RecommendedItem item : recommend) {
				logger.info(item);
			}
		} catch (Exception e) {
			logger.error(e.getMessage() , e );
		}

		
	}
	
}
