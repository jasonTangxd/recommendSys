package com.xiaoxiaomo.hadoop.utils;

import com.xiaoxiaomo.hadoop.entity.UserAgentEntity;
import org.apache.commons.lang.StringUtils;

public class UserAgentUtils {

    /**
     * 用途：根据客户端 User Agent Strings 判断其浏览器、操作平台
     * if 判断的先后次序：
     * 根据设备的用户使用量降序排列，这样对于大多数用户来说可以少判断几次即可拿到结果：
     * 	>>操作系统:Windows > 苹果 > 安卓 > Linux > ...
     * 	>>Browser:Chrome > FF > IE > ...
     * @param userAgent
     * @return
     */
    public static UserAgentEntity getUserAgent(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return null;
        }

        if (userAgent.contains("Windows")) {//主流应用靠前
            /**
             * ******************
             * 台式机 Windows 系列
             * ******************
             * Windows NT 6.2	-	Windows 8
             * Windows NT 6.1	-	Windows 7
             * Windows NT 6.0	-	Windows Vista
             * Windows NT 5.2	-	Windows Server 2003; Windows XP x64 Edition
             * Windows NT 5.1	-	Windows XP
             * Windows NT 5.01	-	Windows 2000, Service Pack 1 (SP1)
             * Windows NT 5.0	-	Windows 2000
             * Windows NT 4.0	-	Microsoft Windows NT 4.0
             * Windows 98; Win 9x 4.90	-	Windows Millennium Edition (Windows Me)
             * Windows 98	-	Windows 98
             * Windows 95	-	Windows 95
             * Windows CE	-	Windows CE
             * 判断依据:http://msdn.microsoft.com/en-us/library/ms537503(v=vs.85).aspx
             */
            if (userAgent.contains("Windows NT 6.2")) {//Windows 8
                return judgeBrowser(userAgent, "Windows", "8" , null);//判断浏览器
            } else if (userAgent.contains("Windows NT 6.1")) {//Windows 7
                return judgeBrowser(userAgent, "Windows", "7" , null);
            } else if (userAgent.contains("Windows NT 6.0")) {//Windows Vista
                return judgeBrowser(userAgent, "Windows", "Vista" , null);
            } else if (userAgent.contains("Windows NT 5.2")) {//Windows XP x64 Edition
                return judgeBrowser(userAgent, "Windows", "XP" , "x64 Edition");
            } else if (userAgent.contains("Windows NT 5.1")) {//Windows XP
                return judgeBrowser(userAgent, "Windows", "XP" , null);
            } else if (userAgent.contains("Windows NT 5.01")) {//Windows 2000, Service Pack 1 (SP1)
                return judgeBrowser(userAgent, "Windows", "2000" , "SP1");
            } else if (userAgent.contains("Windows NT 5.0")) {//Windows 2000
                return judgeBrowser(userAgent, "Windows", "2000" , null);
            } else if (userAgent.contains("Windows NT 4.0")) {//Microsoft Windows NT 4.0
                return judgeBrowser(userAgent, "Windows", "NT 4.0" , null);
            } else if (userAgent.contains("Windows 98; Win 9x 4.90")) {//Windows Millennium Edition (Windows Me)
                return judgeBrowser(userAgent, "Windows", "ME" , null);
            } else if (userAgent.contains("Windows 98")) {//Windows 98
                return judgeBrowser(userAgent, "Windows", "98" , null);
            } else if (userAgent.contains("Windows 95")) {//Windows 95
                return judgeBrowser(userAgent, "Windows", "95" , null);
            } else if (userAgent.contains("Windows CE")) {//Windows CE
                return judgeBrowser(userAgent, "Windows", "CE" , null);
            }
        } else if (userAgent.contains("Mac OS X")) {
            /**
             * ********
             * 苹果系列
             * ********
             * iPod	-		Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8G4 Safari/6533.18.5
             * iPad	-		Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10
             * iPad2	-		Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3
             * iPhone 4	-	Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7
             * iPhone 5	-	Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3
             * 判断依据:http://www.useragentstring.com/pages/Safari/
             * 参考:http://stackoverflow.com/questions/7825873/what-is-the-ios-5-0-user-agent-string
             * 参考:http://stackoverflow.com/questions/3105555/what-is-the-iphone-4-user-agent
             */
            if (userAgent.contains("iPod")) {
                return judgeBrowser(userAgent, "iPod", null , null);//判断浏览器
            }
        }
        return null;
    }

    /**
     * 用途：根据客户端 User Agent Strings 判断其浏览器
     * if 判断的先后次序：
     * 根据浏览器的用户使用量降序排列，这样对于大多数用户来说可以少判断几次即可拿到结果：
     * 	>>Browser:Chrome > FF > IE > ...
     * @param userAgent:user agent
     * @param platformType:平台
     * @param platformSeries:系列
     * @param platformVersion:版本
     * @return
     */
    private static UserAgentEntity judgeBrowser(String userAgent, String platformType, String platformSeries, String platformVersion) {
        if (userAgent.contains("Chrome")) {
            /**
             * ***********
             * Chrome 系列
             * ***********
             * Chrome 24.0.1295.0	-	Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.15 (KHTML, like Gecko) Chrome/24.0.1295.0 Safari/537.15
             * Chrome 24.0.1292.0	-	Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.14 (KHTML, like Gecko) Chrome/24.0.1292.0 Safari/537.14
             * Chrome 24.0.1290.1	-	Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.13 (KHTML, like Gecko) Chrome/24.0.1290.1 Safari/537.13
             * 判断依据:http://www.useragentstring.com/pages/Chrome/
             */
            String temp = userAgent.substring(userAgent.indexOf("Chrome/") + 7);//拿到User Agent String "Chrome/" 之后的字符串,结果形如"24.0.1295.0 Safari/537.15"或"24.0.1295.0"
            String chromeVersion = null;
            if (temp.indexOf(" ") < 0) {//temp形如"24.0.1295.0"
                chromeVersion = temp;
            } else {//temp形如"24.0.1295.0 Safari/537.15"
                chromeVersion = temp.substring(0, temp.indexOf(" "));
            }
            return new UserAgentEntity("Chrome", chromeVersion, platformType, platformSeries, platformVersion);
        } else if (userAgent.contains("Firefox")) {
            /**
             * *******
             * FF 系列
             * *******
             * Firefox 16.0.1	-	Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1
             * Firefox 15.0a2	-	Mozilla/5.0 (Windows NT 6.1; rv:15.0) Gecko/20120716 Firefox/15.0a2
             * Firefox 15.0.2	-	Mozilla/5.0 (Windows NT 6.2; WOW64; rv:15.0) Gecko/20120910144328 Firefox/15.0.2
             * 判断依据:http://www.useragentstring.com/pages/Firefox/
             */
            String temp = userAgent.substring(userAgent.indexOf("Firefox/") + 8);//拿到User Agent String "Firefox/" 之后的字符串,结果形如"16.0.1 Gecko/20121011"或"16.0.1"
            String ffVersion = null;
            if (temp.indexOf(" ") < 0) {//temp形如"16.0.1"
                ffVersion = temp;
            } else {//temp形如"16.0.1 Gecko/20121011"
                ffVersion = temp.substring(0, temp.indexOf(" "));
            }
            return new UserAgentEntity("Firefox", ffVersion, platformType, platformSeries, platformVersion);
        } else if (userAgent.contains("MSIE")) {
            /**
             * *******
             * IE 系列
             * *******
             * MSIE 10.0	-	Internet Explorer 10
             * MSIE 9.0	-	Internet Explorer 9
             * MSIE 8.0	-	Internet Explorer 8 or IE8 Compatibility View/Browser Mode
             * MSIE 7.0	-	Windows Internet Explorer 7 or IE7 Compatibility View/Browser Mode
             * MSIE 6.0	-	Microsoft Internet Explorer 6
             * 判断依据:http://msdn.microsoft.com/en-us/library/ms537503(v=vs.85).aspx
             */
            if (userAgent.contains("MSIE 10.0")) {//Internet Explorer 10
                return new UserAgentEntity("Internet Explorer", "10", platformType, platformSeries, platformVersion);
            } else if (userAgent.contains("MSIE 9.0")) {//Internet Explorer 9
                return new UserAgentEntity("Internet Explorer", "9", platformType, platformSeries, platformVersion);
            } else if (userAgent.contains("MSIE 8.0")) {//Internet Explorer 8
                return new UserAgentEntity("Internet Explorer", "8", platformType, platformSeries, platformVersion);
            } else if (userAgent.contains("MSIE 7.0")) {//Internet Explorer 7
                return new UserAgentEntity("Internet Explorer", "7", platformType, platformSeries, platformVersion);
            } else if (userAgent.contains("MSIE 6.0")) {//Internet Explorer 6
                return new UserAgentEntity("Internet Explorer", "6", platformType, platformSeries, platformVersion);
            }
        } else {//暂时支持以上三个主流.其它浏览器,待续...
            return new UserAgentEntity(null, null, platformType, platformSeries, platformVersion);
        }
        return null;
    }
}