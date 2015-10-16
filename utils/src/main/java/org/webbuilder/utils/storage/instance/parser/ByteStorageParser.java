package org.webbuilder.utils.storage.instance.parser;

import org.webbuilder.utils.storage.StorageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 使用2进制方式进行对象序列化和反序列化，对象必须实现{@link java.io.Serializable 接口}
 * Created by 浩 on 2015-08-17 0017.
 */
public class ByteStorageParser extends StorageParser {
    @Override
    public byte[] serialize(String key, Object obj) {
        return ByteStorageParser.object2Byte(obj);
    }

    @Override
    public Object deserialize(String key, byte[] data) {
        return ByteStorageParser.byte2Object(data);
    }

    private transient static Logger logger = LoggerFactory.getLogger(ByteStorageParser.class);

    public static <T> T byte2Object(byte[] bytes) {
        Object obj = null;
        try {
            if (bytes == null) return null;
            InputStream bi =new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            logger.error("byte2Object err", e);
        }
        return (T) obj;
    }

    public static byte[] object2Byte(Object obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (Exception e) {
            logger.error("object2Byte err", e);
        }
        return bytes;
    }

}
