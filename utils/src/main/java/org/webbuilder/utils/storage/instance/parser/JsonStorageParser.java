package org.webbuilder.utils.storage.instance.parser;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.storage.StorageParser;

/**
 * 使用json方式进行对象序列化和反序列化
 * Created by 浩 on 2015-08-17 0017.
 */
public class JsonStorageParser extends StorageParser {
    @Override
    public byte[] serialize(String key, Object obj) {
        if (obj instanceof String)
            return ((String) obj).getBytes();
        return JSON.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(String key, byte[] data) {
        if (this.getType() == String.class)
            return new String(data);
        return JSON.parseObject(data, getType());
    }
}
