package com.xxo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GatherUtil {

	/**
	 * 向指定的map组装用户向量
	 * @param _userId
	 * @param _feature
	 * @param _score
	 * @param dataMap
	 */
	public static void gatherUserData(Integer _userId,Integer _feature,Double _score
			, ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> dataMap){
		
		//如果包含用户ID
		if(dataMap.containsKey(_userId)){
			//获取用户向量
			  ConcurrentMap<Integer, Double> featureScore = dataMap.get(_userId);
			  if(featureScore.containsKey(_feature)){
				  //获取此用户的这个特征值的分数
				  featureScore.put(_feature, featureScore.get(_feature)+_score);
			  }else{
				  featureScore.put(_feature, _score);
			  }
		}else{
			ConcurrentMap<Integer,Double> map=new  ConcurrentHashMap<Integer, Double>();
			map.put(_feature, _score);
			dataMap.put(_userId,map );
		}
	}
	
	
	/**
	 * 向指定的map组装物品向量
	 * @param _itemId
	 * @param _feature
	 * @param _score
	 * @param dataMap
	 */
	public static void gatherItemData(Integer _itemId,Integer _feature,Double _score
			,ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> dataMap ){
		
		//如果包含用户ID
		if(dataMap.containsKey(_itemId)){
			//获取用户向量
			  ConcurrentMap<Integer, Double> featureScore = dataMap.get(_itemId);
			  featureScore.put(_feature, _score);
		}else{
			ConcurrentMap<Integer,Double> map=new  ConcurrentHashMap<Integer, Double>();
			map.put(_feature, _score);
			dataMap.put(_itemId,map );
		}
	}
	/**
	 * 向指定的map组装用户物品对应关系
	 * @param _userId
	 * @param _item
	 * @param dataMap
	 */
	public static void gatherUserItemData(Integer _userId,Integer _item, ConcurrentMap<Integer, ConcurrentMap<Integer, Integer>> dataMap){
		//如果包含用户ID
		if(dataMap.containsKey(_userId)){
			//获取用户向量
			ConcurrentMap<Integer, Integer> items=dataMap.get(_userId);
			items.put(_item, 1);
		}else{
			ConcurrentMap<Integer,Integer> map=new  ConcurrentHashMap<Integer, Integer>();
			map.put(_item, 1);
			dataMap.put(_userId, map);
		}
		
	}
	
	/**
	 * 从map中删除指定的数值的数据
	 * @param _userId
	 * @param _feature
	 * @param _score
	 * @param dataMap
	 */
	public static void removeUserData(Integer _userId,Integer _feature,Double _score
			, ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> dataMap){
		
		//如果包含用户ID
		if(dataMap.containsKey(_userId)){
			//获取用户向量
			  ConcurrentMap<Integer, Double> featureScore = dataMap.get(_userId);
			  if(featureScore.containsKey(_feature)){
				  if(featureScore.get(_feature)-_score<=0){
					  featureScore.remove(_feature);
				  }else{
					  featureScore.put(_feature, featureScore.get(_feature)-_score);
				  }
			  }
		}
	}
	
}
