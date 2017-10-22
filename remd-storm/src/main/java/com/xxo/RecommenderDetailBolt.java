package com.xxo;

import java.util.Map;
import org.apache.log4j.Logger;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class RecommenderDetailBolt extends BaseRichBolt  {
	

	//loggerfor4不能在此直接初始化
	private Logger logger;
	private String[] lines;
	private Integer userId;
	private Integer itemId;
	private Integer feature;
	private Double score;
	private OutputCollector _collector;
	
	

	@Override
	public void execute(Tuple tuple) {
		_collector.ack(tuple);
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
				this._collector.emit(new Values(userId,itemId,feature,score));
				
			}
		
	}
	
	
	private boolean notNullOrZero(Integer number){
		return number!=null&&number>0;
	}


	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector=collector;
		
	}


	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("userId","itemId","feature","score"));
		
	}
	

}
