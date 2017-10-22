package com.xiaoxiaomo.storm;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
/**
 * Created by xiaoxiaomo on 2015/2/23.
 */
public class RecommenderTopology {

    public static void main(String[] args) throws Exception {

       /* topic名字
        当前spout的唯一标识Id （以下代称$spout_id）
        zookeeper上用于存储当前处理到哪个Offset了 （以下代称$zk_root)
        当前topic中数据如何解码

        后面两个的目的，其实是在zookeeper上建立一个 $zk_root/$spout_id 的节点，其值是一个map，存放了当前Spout处理的Offset的信息。
        */

        //这个地方其实就是kafka配置文件里边的zookeeper.connect这个参数
        String brokerZkStr = "xxo07:2181";
        ZkHosts zkHosts = new ZkHosts(brokerZkStr);
        String topic = "recommender";
        //汇报offset信息的root路径
        String offsetZkRoot = "/recommender20160627";
        //存储该spoutid的消费offset信息,譬如以topoName来命名
        String offsetZkId  = "recommenderTopology0627";


        SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, topic, offsetZkRoot, offsetZkId);
        //就是告诉KafkaSpout如何去解码数据，生成Storm内部传递数据
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());


        KafkaSpout spout = new KafkaSpout(kafkaConfig);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", spout, 5);
        //builder.setBolt("bolt", new RecommenderBolt(), 1).shuffleGrouping("spout");
        builder.setBolt("detailbolt", new RecommenderDetailBolt(), 3).shuffleGrouping("spout");
        builder.setBolt("statBolt", new RecommenderStatBolt(),3).fieldsGrouping("detailbolt", new Fields("userId"));
        
        
        
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
           cluster.submitTopology("recommenderTopology", config, builder.createTopology());
        }

    }

}