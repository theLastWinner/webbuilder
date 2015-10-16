package org.webbuilder.web.po.logger;

import org.webbuilder.web.core.FastJsonHttpMessageConverter;
import org.webbuilder.web.core.bean.GenericPo;

/**
 * Created by 浩 on 2015-09-11 0011.
 */
public class LogInfo extends GenericPo<String> {

    private String ip;

    private String uri;

    private String url;

    private String method;

    private String response;

    private String user_id;

    private long request_time;

    private long response_time;

    private long use_time = -1;

    private String referer;

    private String user_agent;

    private String code;

    private String headers;

    private String status;

    private String class_name;

    private String desc;

    private String params;

    private String exception;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getRequest_time() {
        return request_time;
    }

    public void setRequest_time(long request_time) {
        this.request_time = request_time;
    }

    public long getResponse_time() {
        return response_time;
    }

    public void setResponse_time(long response_time) {
        this.response_time = response_time;
    }

    public long getUse_time() {
        if (use_time == -1)
            use_time = getResponse_time() - getRequest_time();
        return use_time;
    }

    public void setUse_time(long use_time) {
        this.use_time = use_time;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return FastJsonHttpMessageConverter.toJson(this);
    }
}
