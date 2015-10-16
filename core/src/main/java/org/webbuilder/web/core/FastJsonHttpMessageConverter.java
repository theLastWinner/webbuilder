package org.webbuilder.web.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.po.form.Form;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 对SpringMvc 响应json支持。
 */
public class FastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    private Charset charset;
    private SerializerFeature[] features;
    private SimplePropertyPreFilter[] filters;
    private static FastJsonHttpMessageConverter instance;

    public FastJsonHttpMessageConverter() {
        super(new MediaType[]{new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8)});
        this.charset = UTF8;
        this.features = new SerializerFeature[0];
        instance = this;
    }

    protected boolean supports(Class<?> clazz) {
        return true;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public SerializerFeature[] getFeatures() {
        return this.features;
    }

    public void setFeatures(SerializerFeature... features) {
        this.features = features;
    }

    public SimplePropertyPreFilter[] getFilters() {
        return filters;
    }

    public void setFilters(SimplePropertyPreFilter[] filters) {
        this.filters = filters;
    }

    public static String toJson(Object obj) {
        return instance.parseToJson(obj);
    }

    public String parseToJson(Object obj) {
        String text;
        if (obj instanceof String) //String直接响应
            text = (String) obj;
        else if (obj instanceof ResponseData) {
            ResponseData data = (ResponseData) obj;
            text = JSON.toJSONString(data.getData(), data.getFilters().toArray(new SerializeFilter[]{}), this.features);
            //指定了Callback跨域调用
            if (!StringUtil.isNullOrEmpty(data.getCallBack())) {
                text = data.getCallBack().concat("(").concat(text).concat(")");
            }
        } else if (obj instanceof ResponseMessage) {
            ResponseMessage data = (ResponseMessage) obj;
            text = JSON.toJSONString(data, this.features);
            //指定了Callback跨域调用
            if (!StringUtil.isNullOrEmpty(data.getCallback())) {
                text = data.getCallback().concat("(").concat(text).concat(")");
            }
        } else {
            text = JSON.toJSONString(obj, this.features);
        }
        return text;
    }

    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = inputMessage.getBody();
        byte[] buf = new byte[1024];
        while (true) {
            int bytes = in.read(buf);
            if (bytes == -1) {
                byte[] bytes1 = baos.toByteArray();
                return JSON.parseObject(bytes1, 0, bytes1.length, this.charset.newDecoder(), clazz, new Feature[0]);
            }
            if (bytes > 0) {
                baos.write(buf, 0, bytes);
            }
        }
    }

    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (obj == null) return;
        OutputStream out = outputMessage.getBody();
        byte[] bytes = parseToJson(obj).getBytes(charset);
        out.write(bytes);
    }

}
