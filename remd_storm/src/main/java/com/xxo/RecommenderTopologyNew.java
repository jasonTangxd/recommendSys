package com.xxo;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
/**
 * Created by zenith on 2016/2/23.
 */
public class RecommenderTopologyNew {

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new DemoSpout(), 1);
        //builder.setBolt("bolt", new RecommenderBolt(), 1).shuffleGrouping("spout");
        builder.setBolt("detailBolt", new RecommenderDetailBolt(), 1).shuffleGrouping("spout");
        builder.setBolt("statBolt", new RecommenderStatBolt(), 2).fieldsGrouping("detailBolt", new Fields("userId"));
        Config config = new Config();
        //这一行代码表示让spout每10秒发送一个特殊的tuple 此tuple的 SourceComponent就是 SYSTEM_COMPONENT_ID = "__system"
        //config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
        
        config.setDebug(true);


        //提交远程
        if (args != null && args.length > 0) {
        	config.setNumWorkers(3); // use three worker processes
          StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        }
        else {
        	//提交本地模式
    	   LocalCluster cluster = new LocalCluster();
    	   config.setNumWorkers(3);
           cluster.submitTopology("recommenderTopology", config, builder.createTopology());
        }

    }

}