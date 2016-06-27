package com.xxo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class RecommenderBolt extends BaseRichBolt  {
	

	//loggerfor4不能在此直接初始化
	private Logger logger;
	private String[] lines;
	private Integer userId;
	private Integer itemId;
	private Integer feature;
	private Double score;
	private Connection connection;
	private DateFormat dateFormat;
	//用户的行为特征评分信息
	private ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> userFeatureScore;
	//数据库里存储的用户的特征评分信息
	private ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> userDbFeatureScore;
	//物品的特征矩阵
	private ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> itemFeatureScore;
	//用户产生行为的物品集合
	private ConcurrentMap<Integer, ConcurrentMap<Integer, Integer>> userItemFilter;
	private String dateTimeStr=null;
	private  OutputCollector _collector;
	
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		  Map<String, Object> map = new HashMap<>();
		  map.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
		return map;
	}

	@Override
	public void execute(Tuple tuple) {
		_collector.ack(tuple);
		//跨天将map清空
		if(!dateTimeStr.equals(dateFormat.format(new Date()))){
			userFeatureScore.clear();
			itemFeatureScore.clear();
			userItemFilter.clear();
			//将当天的日期更新新的时间戳
			dateTimeStr=dateFormat.format(new Date());
		}
		//统计阶段
		if(tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)){
			try {
				statData();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		else{
			//组装数据   
			//userId	itemId	feature score
			lines=tuple.getStringByField("str").split("\t");
			try {
				userId=Integer.parseInt(lines[0]);
				itemId=Integer.parseInt(lines[1]);
				feature=Integer.parseInt(lines[2]);
				score=Double.parseDouble(lines[3]);	
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if(notNullOrZero(userId)&&notNullOrZero(itemId)
					&&notNullOrZero(feature)&&score!=null&&score>0){
				//将从kafka中消费的用户的特征数据收集在userFeatureScore中，组成用户向量。
				GatherUtil.gatherUserData(userId, feature, score,userFeatureScore);
				//将从kafka中消费的用户和物品的数据收集在userItemFilter，记录用户产生行为的物品，方便之后的推荐过滤。
				GatherUtil.gatherUserItemData(userId, itemId,userItemFilter);
			}
		}	
	}
	
	
/**
 * 统计阶段
 * @throws Exception
 */
	private void statData() throws Exception{
		try {
			
			//01 获取用户特征向量 userFeature+db
			userDbFeatureScore=getUserFeatureFromDb();
			//组合 只组合在userFeatureScore里存在的user
			for (Integer _user : userFeatureScore.keySet()) {
				ConcurrentMap<Integer, Double> _featureScore = userDbFeatureScore.get(_user);
				if(_featureScore!=null){
					for (Integer _feature : _featureScore.keySet()) {
						//把用户取出的userDbFeatureScore组合到用户特征向量中，便于后期计算
						GatherUtil.gatherUserData(_user, _feature	, _featureScore.get(_feature), userFeatureScore);
					}
				}
				
			}
			//02 获取物品特征值矩阵
			itemFeatureScore=getItemFeatureFromDb();
			
			//03用户特征向量和物品特征矩阵乘积
			for (Integer user : userFeatureScore.keySet()) {
				//存储此用户的物品和得分
				ConcurrentMap<Integer, Double> itemTop=new ConcurrentHashMap<Integer, Double>();
				
				ConcurrentMap<Integer, Double> featureScoreForUser = userFeatureScore.get(user);
				
				
				for (Integer item : itemFeatureScore.keySet()) {
					//过滤已经评价过的物品
					if(userItemFilter.containsKey(user)&&userItemFilter.get(user).containsKey(item)){
						//结束当前循环 继续下一次循环
						continue;
					}
					ConcurrentMap<Integer, Double> featureScoreForItem=itemFeatureScore.get(item);
					//根据该用户的每个特征值和值。 结合特征值和物品的权重计算物品的 推荐分数
					double scoreSum=0d;
					for (Integer feature : featureScoreForUser.keySet()) {
						//如果有此特征
						if(featureScoreForItem.containsKey(feature)){
							scoreSum+=featureScoreForUser.get(feature)*featureScoreForItem.get(feature);
						}
					}
					
					//排序取出top10
					itemTop.put(item, scoreSum);
					if(itemTop.size()>10){
						//保留map中最大的10个数据
						removeLittleItem(itemTop);
					}
				}
				Statement statement = connection.createStatement();
				//讲结果插入到mysql数据库
				for (Integer item : itemTop.keySet()) {
					if(itemTop.get(item)>0){
						statement.execute(String.format("insert into t_user_item_top (user_id,item_id,score,time) values(%s,%s,%s,now()); "
							,user,item,itemTop.get(item)));
					}
				}
				statement.close();
				
				
			}
			
			//将userFeatureScore里的db部分去除掉，避免下次再次读取的重复问题
			//组合的逆操作
			for (Integer _user : userFeatureScore.keySet()) {
				ConcurrentMap<Integer, Double> _featureScore = userDbFeatureScore.get(_user);
				if(_featureScore!=null){
					for (Integer _feature : _featureScore.keySet()) {
						GatherUtil.removeUserData(_user, _feature	, _featureScore.get(_feature), userFeatureScore);
					}
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		
		
	}
	
	

	

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		
		this._collector=collector;
		userFeatureScore=new ConcurrentHashMap<Integer, ConcurrentMap<Integer,Double>>();
		userItemFilter=new ConcurrentHashMap<Integer, ConcurrentMap<Integer,Integer>>();
		itemFeatureScore=new ConcurrentHashMap<Integer, ConcurrentMap<Integer,Double>>();
		dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		dateTimeStr=dateFormat.format(new Date());
		logger=Logger.getLogger(RecommenderBolt.class);
		Properties prop=new Properties();
		try {
//			mysql.driverClassName	mysql.url	mysql.username	mysql.password
			prop.load(RecommenderBolt.class.getClassLoader().getResourceAsStream("jdbc.properties"));
			Class.forName(prop.getProperty("mysql.driverClassName"));
			connection = DriverManager.getConnection(prop.getProperty("mysql.url")
					, prop.getProperty("mysql.username")
					, prop.getProperty("mysql.password"));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * 从中DB获取用户的特征信息
	 * @return
	 * @throws Exception
	 */
	private ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> getUserFeatureFromDb() throws Exception{
		ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> _userDbFeatureScore=new ConcurrentHashMap<Integer, ConcurrentMap<Integer,Double>>();
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("select * from t_user_feature");
		while (rs.next()) {
			if(userFeatureScore.containsKey(rs.getInt("u_id"))){
				GatherUtil.gatherUserData(rs.getInt("u_id"),rs.getInt("feature_id"),rs.getDouble("score"),_userDbFeatureScore);
			}
		}
		statement.close();
		return _userDbFeatureScore;
	}
	/**
	 * 从中DB获取物品的特征信息
	 * @return
	 * @throws Exception
	 */
	private ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> getItemFeatureFromDb() throws Exception{
		ConcurrentMap<Integer, ConcurrentMap<Integer, Double>> _itemDbFeatureScore=new ConcurrentHashMap<Integer, ConcurrentMap<Integer,Double>>();
		Statement statement = connection.createStatement();
		ResultSet rs=statement.executeQuery("select * from t_feature_item");
		while (rs.next()) {
			GatherUtil.gatherItemData(rs.getInt("item_id"),rs.getInt("feature_id"),rs.getDouble("weight"),_itemDbFeatureScore);
		}
		statement.close();
		return _itemDbFeatureScore;
	}
	
	/**
	 * 删除map中最小的元素
	 * @param top
	 */
	private void removeLittleItem(ConcurrentMap<Integer, Double> top){
		Integer littleIndex=0;
		Double littleScore=0d;
		for (Integer key : top.keySet()) {
			if(top.get(key)<littleScore){
				littleIndex=key;
				littleScore=top.get(key);
			}
		}
		top.remove(littleIndex);
		
	}
	/**
	 * 对int类型的判断是否为空等
	 * @param number
	 * @return
	 */
	private boolean notNullOrZero(Integer number){
		return number!=null&&number>0;
	}
	

}
