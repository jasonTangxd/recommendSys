## remd-web 模拟用户请求数据部分

### 数据的来源
1. 用户有某个需求，比如打发时间浏览一下新闻，想买个东西逛一逛淘宝，想团购打开美团看看附近的商家等等；
2. 用户的这些行为或者操作，就会在后端业务系统中产生浏览、查看、购买、订单等记录数据。

### 数据的对接
1. 推荐系统立项后就可以和业务系统进行对接，业务系统现有用户行为数据是否能满足推荐系统的需求；
2. 双方进行相关约束（相关的字段，在业务中代表的意思等），埋点
3. 推荐系统怎么获取数据？业务系统传输数据过来，还是数据流到kafka，推荐系统部署agent?

### 数据的采集
1. 本推荐系统是业务系统log4j产生数据，flume-ng 采集数据
2. 我们重点就关注推荐系统，前面的业务系统和用户参数的数据就通过该项目模拟生成。


### 运行该项目
1. mvn打包
```sbtshell
cd $Project/remd-web/ 
mvn clean package
```
2. 上传到xiaoxiaomo08,xiaoxiaomo09阶段，运行
``` sbtshell
java -cp remd-web-1.0-SNAPSHOT-jar-with-dependencies.jar com.xxo.web.SimulationRequest
```





# 博客地址
[小小默：http://blog.xiaoxiaomo.com](http://blog.xiaoxiaomo.com)
