package com.xxo.web;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 初始化数据
 */
public interface InitData {

    //应用ID
    Map<Integer,Integer> appid = ImmutableMap.<Integer,Integer>builder()
            .put(0,1000) //web
            .put(1,1001) //android
            .put(2,1002) //ios
            .put(3,1003) //ipad
            .build();

    // IP
    Map<Integer,String> ip = ImmutableMap.<Integer, String>builder()
            .put(0,"61.172.219.96")
            .put(1,"211.155.231.99")
            .put(2,"218.75.100.117")
            .put(3,"211.167.218.22")
            .put(4,"60.12.227.208")
            .put(5,"221.8.9.6 80")
            .put(6,"218.26.219.186")
            .put(7,"222.68.207.11")
            .put(8,"61.53.137.50")
            .put(9,"218.75.75.133")
            .put(10,"221.201.216.11")
            .put(11,"125.39.129.67")
            .put(12,"220.191.55.211")
            .put(13,"125.70.229.30")
            .put(14,"220.191.55.160")
            .put(15,"202.98.11.101")
            .put(16,"59.76.81.3 808")
            .put(17,"119.11.87.171")
            .put(18,"121.9.221.188")
            .put(19,"221.195.119.115")
            .build();

    // 用户ID
    Map<Integer,Integer> userid = ImmutableMap.<Integer,Integer>builder()
            .put(0,20101)
            .put(1,10201)
            .put(2,10025)
            .put(3,20201)
            .put(4,21217)
            .put(5,30303)
            .put(6,40601)
            .put(7,10207)
            .put(8,10608)
            .put(9,10709)
            .put(10,0)
            .put(11,0)
            .build();

    // 请求协议
    Map<Integer,String> request = ImmutableMap.<Integer,String>builder()
            .put(0,"GET /tologin HTTP/1.1")
            .put(1,"GET /userList HTTP/1.1")
            .put(2,"GET /user/add HTTP/1.1")
            .put(3,"POST /updateById?id = 21 HTTP/1.1")
            .put(4,"GET /top HTTP/1.1")
            .put(5,"GET /tologin HTTP/1.0")
            .put(6,"GET /index HTTP/1.1")
            .put(7,"POST /stat HTTP/1.1")
            .put(8,"GET /getDataById HTTP/1.0")
            .put(9,"GET /tologin HTTP/1.1")
            .put(10,"POST /check/init HTTP/1.1")
            .put(11,"GET /check/detail HTTP/1.1")
            .put(12,"GET /top HTTP/1.0")
            .put(13,"POST /passpword/getById?id = 11 HTTP/1.1")
            .put(14,"GET /update/pass HTTP/1.0")
            .build();

    // 请求响应状态码
    Map<Integer,Integer> status = ImmutableMap.<Integer,Integer>builder()
            .put(0,200) //200 ok
            .put(1,404) //404 not found
            .put(2,408) //408 Request Timeout
            .put(3,500) // 500 Internal Server Error
            .put(4,504) //504 Gateway Timeout
            .build();

    // 请求url
    Map<Integer,String> http_referer = ImmutableMap.<Integer,String>builder()
            .put(0,"/tologin")
            .put(1,"/userList")
            .put(2,"/user/add")
            .put(3,"/updateById?id=21")
            .put(4,"/top")
            .put(5,"/tologin")
            .put(6,"/index")
            .put(7,"/stat")
            .put(8,"/getDataById")
            .put(9,"/tologin")
            .put(10,"/check/init")
            .put(11,"/check/detail")
            .put(12,"/top")
            .put(13,"/passpword/getById?id=11")
            .put(14,"/update/pass")
            .build();

    // 用户agent
    Map<Integer,String> user_agent = ImmutableMap.<Integer, String>builder()
            .put(0,"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT6.0)")
            .put(1,"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT5.2)")
            .put(2,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT5.1)")
            .put(3,"Mozilla/4.0 (compatible; MSIE 5.0; WindowsNT)")
            .put(4,"Mozilla/5.0 (Windows; U; Windows NT 5.2)Gecko/2008070208 Firefox/3.0.1")
            .put(5,"Mozilla/5.0 (Windows; U; Windows NT 5.1)Gecko/20070309 Firefox/2.0.0.3")
            .put(6,"Mozilla/5.0 (Windows; U; Windows NT 5.1)Gecko/20070803 Firefox/1.5.0.12")
            .put(7,"Mozilla/5.0 (Windows; U; Windows NT 5.2)AppleWebKit/525.13 (KHTML, like Gecko) Version/3.1Safari/525.13")
            .put(8,"Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML,like Gecko) Chrome/0.2.149.27 Safari/525.13")
            .put(9,"Mozilla/5.0 (iPhone; U; CPU like Mac OS X)AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93Safari/419.3")
            .build();

    // 请求参数
    Map<Integer,String> param = ImmutableMap.<Integer, String>builder()
            .put(0,"{\"ugctype\":\"consumer\",\"userId\":\"10025\",\"coin\":\"50\",\"number\":\"2\"}")
            .put(1,"{\"ugctype\":\"consumer\",\"userId\":\"21217\",\"coin\":\"20\",\"number\":\"3\"}")
            .put(2,"{\"ugctype\":\"consumer\",\"userId\":\"40601\",\"coin\":\"10\",\"number\":\"2\"}")
            .put(3,"{\"ugctype\":\"recharge\",\"userId\":\"21217\",\"rmb\":\"50\",\"number\":\"10\"}")
            .put(4,"{\"ugctype\":\"recharge\",\"userId\":\"40601\",\"rmb\":\"100\",\"number\":\"20\"}")
            .put(5,"{\"ugctype\":\"recharge\",\"userId\":\"10025\",\"rmb\":\"100\",\"number\":\"20\"}")
            .put(6,"{\"ugctype\":\"fav\",\"userId\":\"10608\",\"item\":\"10\"}")
            .put(7,"{\"ugctype\":\"fav\",\"userId\":\"10709\",\"item\":\"11\"}")
            .put(8,"{\"ugctype\":\"fav\",\"userId\":\"10207\",\"item\":\"12\"}")
            .put(9,"{\"ugctype\":\"fav\",\"userId\":\"40604\",\"item\":\"13\"}")
            .build();
}

