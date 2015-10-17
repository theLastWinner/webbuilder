package org.webbuilder.utils.storage.instance;


import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


/**
 * 缓存存储器
 * Created by 浩 on 2015-6-13.
 */
public abstract class CacheStorage<K, V> extends Storage<K, V> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public CacheStorage(String name) {
        this.setName(name);
    }

    public Class<V> getValueType(){
        return (Class<V>) ClassUtil.getGenericType(this.getClass(),1);
    }
}
