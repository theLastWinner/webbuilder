package org.webbuilder.web.core.utils;

import org.webbuilder.web.po.user.User;
import com.sun.xml.internal.ws.client.ResponseContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-25 0025.
 */
public class WebUtil {

    public static Map<String, Object> requestInfo(HttpServletRequest request) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ip", getIpAddr(request));
        map.put("url", request.getRequestURL().toString());
        map.put("method", request.getMethod());
        map.put("User-agent", request.getHeader("User-agent"));
        map.put("referer", request.getHeader("referer"));
        User user = (User) request.getSession().getAttribute("user");
        if (user != null)
            map.put("userId", user.getU_id());
        return map;
    }

    /**
     * 尝试获取当前请求的HttpServletRequest实例
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 尝试获取当前登录的用户（基于ThreadLocal）
     *
     * @return 当前登录的用户
     */
    public static User getLoginUser() {
        return getLoginUser(getHttpServletRequest());
    }

    /**
     * 在HttpSession中获取当前登录的用户
     *
     * @param session HttpSession
     * @return 当前登录的用户
     */
    public static User getLoginUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    /**
     * 在HttpServletRequest中获取当前登录的用户
     *
     * @param request HttpServletRequest
     * @return 当前登录的用户
     */
    public static User getLoginUser(HttpServletRequest request) {
        return getLoginUser(request.getSession());
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, String> param = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] varr = entry.getValue();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < varr.length; i++) {
                String var = varr[i];
                if (i != 0) builder.append(",");
                builder.append(var);
            }
            param.put(key, builder.toString());
        }
        return param;
    }

    public static String getUri(HttpServletRequest request, boolean hasParam) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer loginPath = request.getRequestURL();
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        String userPath = loginPath.substring(basePath.length(), loginPath.length());
        buffer.append(userPath);
        if (hasParam) {
            Map<String, String[]> map = request.getParameterMap();
            int index = 0;
            for (String key : map.keySet()) {
                if (index == 0)
                    buffer.append("?");
                else
                    buffer.append("&");
                index++;
                String[] paravalue = map.get(key);
                buffer.append(key + "=" + paravalue[0]);
            }
        }
        return buffer.toString();
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(" x-forwarded-for ");
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" WL-Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getWebAppPath(HttpServletRequest request) {
        String url = request.getServletContext().getRealPath("") + "/";
        return url;
    }

    public static String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        return basePath;
    }
}
