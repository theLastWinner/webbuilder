package org.webbuilder.utils.storage;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-08-17 0017.
 */
public abstract class StorageParser implements Serializable {

    Class type = null;

    public abstract <V> byte[] serialize(String key, V obj);

    public abstract <V> V deserialize(String key, byte[] data);

    public Class getType() {
        if (type == null) type = Object.class;
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}
