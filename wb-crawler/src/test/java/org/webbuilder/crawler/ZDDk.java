package org.webbuilder.crawler;

import org.webbuilder.utils.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-10 0010.
 */
public class ZDDk {

    static String username = "zhouhao";

    static String pwd = "123456";

    public static void main(String[] args) throws Exception {
        //上班打卡
      // start(1);

        //下班打卡
        start(2);
    }

    public static void start(int type) throws Exception {
        String cookie = login();
        String api = "http://cqtaihong.com:81/general/attendance/personal/duty/status.php";
        Map<String, String> param = new HashMap<>();
        param.put("ajax_mode", "1");
        param.put("expire", "-1");
        param.put("type", String.valueOf(type));
       // param.put("remark","加班打卡");
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", cookie);
        header.put("Referer", "http://cqtaihong.com:81/general/attendance/personal/duty/index.php");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
        String res = HttpUtils.doPost(api, param, header);
        System.out.println(res);
    }

    public static String login() throws Exception {
        String api = "http://cqtaihong.com:81/";
        Map<String, String> param = new HashMap<>();
        param.put("account", username);
        param.put("password", pwd);
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
        String res = HttpUtils.doPostv2(api, param, header).getResponseHeader("Set-Cookie").getValue();
        return res;
    }
}
