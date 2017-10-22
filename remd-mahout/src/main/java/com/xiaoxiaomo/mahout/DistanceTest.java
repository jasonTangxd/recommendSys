package com.xiaoxiaomo.mahout;

import org.junit.Before;
import org.junit.Test;

/**
 * 距离算法
 * Created by xiaoxiaomo on 2015/6/22.
 */
public class DistanceTest {

    private int[] a;    //用户a
    private int[] b;    //用户b
    private int[] c;    //用户c


    @Before
    public void init(){
        /**
         * 用户abc对六个事物不同程度的喜好（1-5），5表示最喜欢
         */
        a=new int[]{5,4,2,1,5,5};
        b=new int[]{5,3,1,2,1,1};
        c=new int[]{5,3,4,1,4,3};
    }

    @Test
    public void test(){
        System.out.println(ErluD( a , b ) );
        System.out.println(min( a , b , 2 ) );

        System.out.println(manhattan( a , b ) );
        System.out.println(min(a, b, 1) );

    }


    /**
     * 欧几里得距离
     * 维度特征的差的绝对数值
     * {1,2,3},长度是√1²+2²+3²=√14
     * @param x 用户x的属性
     * @param y 用户y的属性
     */
    private double ErluD( int[] x , int[] y ){
        double result = 0 ;
        for (int i = 0; i < x.length; i++) {
            result += Math.pow(x[i] - y[i], 2); //维度特征差值的平方
        }
        return  Math.sqrt(result)  ;
    }

    /**
     * 曼哈顿距离
     * 多个维度上的距离进行求和后的结果
     * @param x 用户x的属性
     * @param y 用户y的属性
     * @return
     */
    private double manhattan( int[] x , int[] y ){
        double result = 0 ;
        for (int i = 0; i < x.length; i++) {
            result += Math.abs(x[i] - y[i]) ; //绝对值
        }
        return result ;
    }


    /**
     * 闵可夫斯基距离
     * P = 2 ,欧氏距离
     * p = 1 ,曼哈顿距离
     * @param x 用户x的属性
     * @param y 用户y的属性
     * @param p 调节的值
     * @return
     */
    private double min( int[] x , int[] y  , int p ){
        double result = 0 ;
        for (int i = 0; i < x.length; i++) {
            int abs = Math.abs(x[i] - y[i]);//绝对值
            result += Math.pow(abs, p);
        }
        return  Math.pow( result , 1.0/p )  ;
    }




}
