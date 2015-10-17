package org.webbuilder.web.core.cache;

import org.webbuilder.utils.base.MD5;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * Created by 浩 on 2015-08-24 0024.
 */
public class CommonKeyGenerator implements KeyGenerator {

    private static String KEY_ENCODE_TYPE = null;

    private String keyEncodeType = null;

    @Override
    public Object generate(Object o, Method method, Object... objects) {
        StringBuilder builder = new StringBuilder(o.getClass().getName());
        builder.append(".").append(method.getName());
        for (Object object : objects) {
            builder.append(",").append(object != null ? object.hashCode() : "null");
        }
        return encode(builder.toString());
    }

    private static String encode(String key) {
        switch (KEY_ENCODE_TYPE.toUpperCase()) {
            case "MD5":
                return MD5.encode(key);
            case "HASH":
                return String.valueOf(key.hashCode());
            default:
                return key;
        }

    }

    public String getKeyEncodeType() {
        return keyEncodeType;
    }

    public void setKeyEncodeType(String keyEncodeType) {
        this.keyEncodeType = keyEncodeType;
        KEY_ENCODE_TYPE=keyEncodeType;
    }
}
