package com.xxo.hadoop.mr;

/**
 * Created by xiaoxiaomo on 2015/12/31.
 */
public class UgcLog {

    //appid	ip	mid	seid	userid	param	time
    private Integer appid;
    private String ip;
    private String province;
    private String city;
    
    private String mid;
    private String seid;

    private Integer userid;
    private String param;
    private Long time;
    private String dateTime;
    
    
    public Integer getAppid() {
        return appid;
    }

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getSeid() {
        return seid;
    }

    public void setSeid(String seid) {
        this.seid = seid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

   

    public UgcLog(Integer appid, String ip, String mid, String seid,Integer userid, String param, Long time) {
        this.appid = appid;
        this.ip = ip;
        this.mid = mid;
        this.userid = userid;
        this.param=param;
        this.seid = seid;
        this.time = time;
    }





    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",this.appid,this.ip,this.province,this.city,this.mid,this.userid,this.param,this.seid,this.time,this.dateTime);
    }

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
