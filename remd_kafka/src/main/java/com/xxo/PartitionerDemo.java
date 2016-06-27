package com.xxo;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by zenith on 2016/1/27.
 */
public class PartitionerDemo implements Partitioner {

	
    private VerifiableProperties verifiableProperties;

    public PartitionerDemo(VerifiableProperties verifiableProperties) {
        this.verifiableProperties=verifiableProperties;
    }

    public int partition(Object key, int numPartitions) {

        String strKey= (String) key;
        //根据userid的hashCode分区
        return strKey.hashCode()%numPartitions;
    }
}
