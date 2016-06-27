package com.xxo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class DemoSpout extends BaseRichSpout {
	  SpoutOutputCollector _collector;
	
	  List<String> data=new ArrayList<String>();
	  int index=-1;

	  @Override
	  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
	    _collector = collector;
	    data.add("1\t1\t9\t0.2");
	    data.add("1\t4\t5\t0.2");
	    data.add("1\t4\t6\t0.5");
	    data.add("1\t3\t7\t0.1");
	    //用户2的特征值
	    data.add("2\t5\t10\t0.2");
	    data.add("2\t5\t6\t0.1");
	    data.add("2\t5\t7\t0.3");
	    
	    
	
	  }

	  public void nextTuple() {
		
		 index++;
		 if(index<=6){
			 _collector.emit(new Values(data.get(index)));
		 }
	    
	  }

	  @Override
	  public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields("str"));
	  }


	}