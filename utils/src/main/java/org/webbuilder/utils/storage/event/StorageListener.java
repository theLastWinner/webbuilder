package org.webbuilder.utils.storage.event;


import org.webbuilder.utils.storage.Storage;

import java.io.Serializable;

/**
 * 存储事件
 *
 * @param <K> 键
 * @param <V> 值
 */
public class StorageListener<K, V> implements Serializable {

    /**
     * 当存储器初始化时被调用
     *
     * @param storage 存储器实例
     */
    public void onLoad(Storage<K, V> storage) {
    }

    /**
     * 当在存储器中查找某个值时调用，如果返回了非null的理想值，则会被重新缓存
     *
     * @param key 被查找的key
     * @return 理想值
     */
    public V onNotFoundVal(K key) {
        return null;
    }
}
