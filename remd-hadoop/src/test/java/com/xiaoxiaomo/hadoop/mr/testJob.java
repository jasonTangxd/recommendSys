package com.xiaoxiaomo.hadoop.mr;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * ugchead.log
 *
 * Created by TangXD on 2017/10/23.
 */
public class testJob {

    public static void main(String[] args) {

        String value = "1003\t119.11.87.171\t1888db54-0a18-40e7-8d28-adb5c8f545f4\tc0a7b4f1-bb2f-489d-a434-500dfaa19f58\t20201\t{\"ugctype\":\"fav\",\"userId\":\"10608\",\"item\":\"10\"}\t1508729613158";

        String[] UgcLogs = value.toString().split("\t");
        //清理垃圾数据
        if ( UgcLogs.length >= 7 ){
            try {
                Integer userId = Integer.parseInt(UgcLogs[4]);
                String param = UgcLogs[5];  //param为一个json字符串
                JSONObject jsonObject = JSONObject.parseObject(param);
                String itemId =  (String) jsonObject.get("item");
                if ( itemId != null ){
                    String ugcType = (String) jsonObject.get("ugctype");
                    Double score;

                    //行为权重
                    switch ( ugcType ){
                        case "consumer": score = 1d ; break;
                        case "recharge": score = 2d ; break;
                        case "fav": score = 3d ; break;
                        default: score = 0d;
                    }
                    System.out.println(userId+"\t"+itemId+"\t"+score);

                }
            }
            catch (Exception e){
                System.err.println("清洗数据异常");
            }
        }else{
            if ( UgcLogs.length == 3){
                System.out.println(UgcLogs[0]+"\t"+UgcLogs[1]+"\t"+UgcLogs[2]);
            }

        }


    }
}
