package org.webbuilder.utils.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.webbuilder.utils.base.ListUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-26 0026.
 */
public class HttpUtils {
    public static String doPost(String url) throws Exception {
        return doPost(url, new HashMap<String, String>(), new HashMap<String, String>());
    }

    public static String doPost(String url, Map<String, String> param) throws Exception {
        return doPost(url, param, new HashMap<String, String>());
    }

    public static String doPost(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        PostMethod post = new PostMethod(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addRequestHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : param.entrySet()) {
            post.setParameter(entry.getKey(), entry.getValue());
        }
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        httpClient.executeMethod(post);
        return post.getResponseBodyAsString();
    }

    public static PostMethod doPostv2(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        PostMethod post = new PostMethod(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addRequestHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : param.entrySet()) {
            post.setParameter(entry.getKey(), entry.getValue());
        }
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        httpClient.executeMethod(post);
        return post;
    }

    public static String doGet(String url) throws Exception {
        return doGet(url, new HashMap<String, String>(), new HashMap<String, String>());
    }

    public static String doGet(String url, Map<String, String> param) throws Exception {
        return doGet(url, param, new HashMap<String, String>());
    }

    public static String doGet(String url, Map<String, String> param, Map<String, String> headers) throws Exception {
        GetMethod get = new GetMethod(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            get.addRequestHeader(entry.getKey(), entry.getValue());
        }
        HttpMethodParams p = new HttpMethodParams();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            p.setParameter(entry.getKey(), entry.getValue());
        }
        get.setParams(p);
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        httpClient.executeMethod(get);
        return get.getResponseBodyAsString();
    }

}
