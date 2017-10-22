package com.xxo;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.IOException;
import java.util.*;

/**
 * Created by xiaoxiaomo on 2015/1/27.
 */
public class ProducerDemo {
    static Map<Integer, Integer> userId = new HashMap<Integer, Integer>();
    static Map<Integer, Double> score = new HashMap<Integer, Double>();

    static String topic = "recommender";


    public static void main(String[] args) throws IOException {


        //准备配置文件
        Properties prop = new Properties();
        prop.load(ProducerDemo.class.getClassLoader().getResourceAsStream("producer.properties"));
        //创建生产者对象
        Producer<String, String> stringProducer = new Producer<>(new ProducerConfig(prop));

        //组装消息
        //userId	itemId	feature score
        List<KeyedMessage<String, String>> messageList = new ArrayList<>();
        //用户1的特征值
        messageList.add(new KeyedMessage<>(topic, "1", "1\t1\t9\t0.2"));
        messageList.add(new KeyedMessage<>(topic, "1", "1\t4\t5\t0.2"));
        messageList.add(new KeyedMessage<>(topic, "1", "1\t4\t6\t0.5"));
        messageList.add(new KeyedMessage<>(topic, "1", "1\t3\t7\t0.1"));
        //用户2的特征值
        messageList.add(new KeyedMessage<>(topic, "2", "2\t5\t10\t0.2"));
        messageList.add(new KeyedMessage<>(topic, "2", "2\t5\t6\t0.1"));
        messageList.add(new KeyedMessage<>(topic, "2", "2\t5\t7\t0.3"));


        stringProducer.send(messageList);
        //关闭连接
        stringProducer.close();

    }


}
