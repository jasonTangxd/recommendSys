package com.xiaoxiaomo.web;

import org.apache.log4j.Logger;

import java.util.*;

/**
 *
 * 模拟用户请求
 *
 * Created by xiaoxiaomo on 2016/08/30.
 */
public class SimulationRequest {

    private static final Map<Integer, String> mid;
    static
    {
        mid = new HashMap<Integer, String>();
        for (int i  =  0; i < 20; i++) {
            mid.put(i, UUID.randomUUID().toString());
        }
    }

    /**
     *  模拟用户访问，日志格式如下：
     *
     *      appid	ip	mid	userid	login_type	request		status	http_referer	user_agent	time
     *      1001	121.9.221.188	5ef3821f-42eb-4711-b175-72d94d37b9e2	0	0	POST /stat HTTP/1.1	504	/userList	Mozilla/5.0 (iPhone; U; CPU like Mac OS X)AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93Safari/419.3	1508648390195
     *      1003	222.68.207.11	d1f517cb-36f0-4fff-9a07-f711bb1f94b7	10709	0	GET /index HTTP/1.1	404	/tologin	Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML,like Gecko) Chrome/0.2.149.27 Safari/525.13	1508648391201
     *      1000	218.26.219.186	null	20202	0	GET /update/pass HTTP/1.0	500	/top	Mozilla/4.0 (compatible; MSIE 8.0; Windows NT6.0)	1508648392203
     *      1002	221.8.9.6 80	975580c2-1d0c-47e9-9694-ac29580a52b7	30303	1	GET /update/pass HTTP/1.0	500	/index	Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML,like Gecko) Chrome/0.2.149.27 Safari/525.13	1508648393204
     *      1000	211.167.248.22	c3363299-8517-4802-a14e-bb2862b7b786	0	1	GET /tologin HTTP/1.0	504	/getDataById	Mozilla/4.0 (compatible; MSIE 6.0; Windows NT5.1)	1508648394205
     *      1000	121.11.87.171	null	10709	0	POST /check/init HTTP/1.1	200	/index	Mozilla/5.0 (iPhone; U; CPU like Mac OS X)AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93Safari/419.3	1508648395207
     *      1000	220.194.55.244	null	0	1	GET /user/add HTTP/1.1	200	/check/detail	Mozilla/5.0 (iPhone; U; CPU like Mac OS X)AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93Safari/419.3	1508648396208
     *      1000	59.76.81.3 808	f314ff84-b7ec-4963-8dd3-72f6d5487012	10608	1	POST /stat HTTP/1.1	408	/user/add	Mozilla/5.0 (Windows; U; Windows NT 5.1)Gecko/20070803 Firefox/1.5.0.12	1508648397208
     *
     */
    static class  AccessLogger implements Runnable{

        public void run() {
            Random r  =  new Random();
            Logger accessLogger = Logger.getLogger("access"); // 日志
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                accessLogger.info(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s"
                        , InitData.appid.get(r.nextInt(4))
                        , InitData.ip.get(r.nextInt(20))
                        , mid.get(r.nextInt(20))
                        , InitData.userid.get(r.nextInt(12)), r.nextInt(2)
                        , InitData.request.get(r.nextInt(15))
                        , InitData.status.get(r.nextInt(5))
                        , InitData.http_referer.get(r.nextInt(15))
                        , InitData.user_agent.get(r.nextInt(10)), new Date().getTime()));
            }
        }
    }

    /**
     *
     * 模拟用户操作日志：
     *
     *      head：appid	ip	mid	seid	userid	param	time
     *      1003	125.39.129.67	3de4c273-cc9d-408e-b46a-9946324b08ea	6c5c5d85-25eb-487c-90e6-65c3f42d4c93	10207	{"ugctype":"fav","userId":"40604","item":"13"}	1508648390203
     *      1002	211.167.248.22	null	99bd5f66-7cea-4058-a151-acea30aedd94	20201	{"ugctype":"fav","userId":"10207","item":"12"}	1508648391207
     *      1001	121.9.221.188	26042c2c-3029-4cf8-a149-80b082743c3b	867ed1f4-1cab-431b-b612-7cb5f50df68f	10709	{"ugctype":"recharge","userId":"40604","rmb":"100","number":"20"}	1508648392209
     *      1002	218.75.75.133	71764d58-06d5-4058-8d2f-82c7491494c4	37217cf6-9a56-4aea-8a0e-8365f27a6bc5	20101	{"ugctype":"consumer","userId":"10022","coin":"50","number":"2"}	1508648393212
     *      1003	61.172.249.96	null	42e7e541-df3e-482b-a555-d3bb146d6b79	20101	{"ugctype":"consumer","userId":"20202","coin":"20","number":"3"}	1508648394214
     *      1001	125.39.129.67	6f2e64ae-30b8-4c12-b3db-b8460eed356e	c4bc6712-837f-4118-beef-53a3443f3aeb	30303	{"ugctype":"fav","userId":"40604","item":"13"}	1508648395216
     *      1000	121.9.221.188	d1f517cb-36f0-4fff-9a07-f711bb1f94b7	68194197-47ff-412c-a142-f64aa4a9a7ab	10201	{"ugctype":"recharge","userId":"10022","rmb":"100","number":"20"}	1508648396217
     *      1002	59.76.81.3 808	3de4c273-cc9d-408e-b46a-9946324b08ea	c2183643-8dae-4850-a6f3-86ce08b1050a	10608	{"ugctype":"fav","userId":"10207","item":"12"}	1508648397218

     *      detail：appid	ip	mid	seid	userid	param	result	time
     *      1002	218.75.75.133	4caf0848-9269-4c40-b2ec-9d13df8167dd	6c5c5d85-25eb-487c-90e6-65c3f42d4c93	10022	{"ugctype":"recharge","userId":"20202","rmb":"50","number":"10"}	0	1508648390205
     *      1001	221.204.246.11	71764d58-06d5-4058-8d2f-82c7491494c4	99bd5f66-7cea-4058-a151-acea30aedd94	30303	{"ugctype":"consumer","userId":"20202","coin":"20","number":"3"}	0	1508648391208
     *      1003	218.26.219.186	7ecc6be3-040b-47d6-afdc-adc81215de4d	867ed1f4-1cab-431b-b612-7cb5f50df68f	20201	{"ugctype":"recharge","userId":"40604","rmb":"100","number":"20"}	1	1508648392210
     *      1001	218.75.100.114	4bf4b553-7873-4ba0-a49a-7f837b378886	37217cf6-9a56-4aea-8a0e-8365f27a6bc5	40604	{"ugctype":"fav","userId":"10709","item":"11"}	0	1508648393212
     *      1002	211.155.234.99	a313d273-37ca-4342-9b22-798e0377bcbf	42e7e541-df3e-482b-a555-d3bb146d6b79	10207	{"ugctype":"consumer","userId":"40604","coin":"10","number":"2"}	1	1508648394215
     *      1000	218.26.219.186	4bf4b553-7873-4ba0-a49a-7f837b378886	c4bc6712-837f-4118-beef-53a3443f3aeb	40604	{"ugctype":"consumer","userId":"10022","coin":"50","number":"2"}	0	1508648395217
     *      1001	121.9.221.188	28d1311f-7498-476c-a3e7-d10d921b8cca	68194197-47ff-412c-a142-f64aa4a9a7ab	30303	{"ugctype":"consumer","userId":"20202","coin":"20","number":"3"}	1	1508648396217
     *      1003	218.75.75.133	eb420378-9e47-41d7-a0cd-27b3cc9b5a5b	c2183643-8dae-4850-a6f3-86ce08b1050a	40604	{"ugctype":"fav","userId":"10207","item":"12"}	0	1508648397218
     *
     */
   static class UgcLogger implements  Runnable{

        public void run() {
            Random r = new Random();
            String seid;
            Logger headLogger = Logger.getLogger("ugchead");
            Logger tailLogger = Logger.getLogger("ugctail");

            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seid = UUID.randomUUID().toString();

                //appid	ip	mid	seid	userid	param	time
                headLogger.info(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s"
                        , InitData.appid.get(r.nextInt(4))
                        , InitData.ip.get(r.nextInt(20))
                        , mid.get(r.nextInt(20))
                        , seid
                        , InitData.userid.get(r.nextInt(10))
                        , InitData.param.get(r.nextInt(10)), new Date().getTime()));


                //appid	ip	mid	seid	userid	param	result	time
                tailLogger.info(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s"
                        , InitData.appid.get(r.nextInt(4))
                        , InitData.ip.get(r.nextInt(20))
                        , mid.get(r.nextInt(20))
                        , seid
                        , InitData.userid.get(r.nextInt(10))
                        , InitData.param.get(r.nextInt(10))
                        , r.nextInt(2)
                        , new Date().getTime()));
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new AccessLogger()).start();
        new Thread(new UgcLogger()).start();

    }

}
