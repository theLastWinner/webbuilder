package org.webbuilder.utils.storage.instance;


import org.webbuilder.utils.storage.event.KeyFilter;
import org.webbuilder.utils.storage.event.StorageListener;
import org.webbuilder.utils.storage.event.Finder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存存储器，基于ConcurrentHashMap
 * Created by 浩 on 2015-6-13.
 */
public class LocalCacheStorage<K, V> extends CacheStorage<K, V> {

    public LocalCacheStorage(String name) {
        super(name);
    }

    /**
     * 存储内容
     */
    private Map<K, V> STORAGE = new ConcurrentHashMap<>(64);

    /**
     * 获取所有存储内容
     *
     * @return 所有存储内容
     */
    @Override
    public List<V> get() {
        return new ArrayList<>(STORAGE.values());
    }

    @Override
    public boolean containsKey(K key) {
        return STORAGE.containsKey(key);
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<>(STORAGE.keySet());
    }

    @Override
    public Set<K> keySet(KeyFilter<K> filter) {
        Set<K> newSet = new LinkedHashSet<>();
        for (K k : keySet()) {
            if (filter.isOver()) break;
            if (filter.each(k)) {
                newSet.add(k);
            }
        }
        return newSet;
    }

    @Override
    public List<V> find(Finder<K, V> finder) {
        List<V> vals = new LinkedList<>();
        int count = 0;
        for (Map.Entry<K, V> kvEntry : STORAGE.entrySet()) {
            if (finder.isOver())
                break;
            if (finder.each(++count,kvEntry.getKey(), kvEntry.getValue())) {
                vals.add(kvEntry.getValue());
            }
        }
        return vals;
    }

    @Override
    public boolean remove(K key) {
        STORAGE.remove(key);
        return true;
    }

    @Override
    public int clear() {
        int size = size();
        STORAGE.clear();
        return size;
    }

    @Override
    public int size() {
        return STORAGE.size();
    }

    @Override
    public V get(K key) {
        V val = STORAGE.get(key);
        if (val == null) {
            if (logger.isInfoEnabled())
                logger.info(getName() + " key:" + key + " not found! do event!");
            for (StorageListener<K, V> e : getListeners()) {
                val = e.onNotFoundVal(key);
                if (val != null)//遍历所有Event，当某个Event返回结果不为null时退出。
                    break;
            }
            if (val != null)
                put(key, val);//回掉后的值放入存储器
            else
                return null; //未获取到任何值
        }
        return val;
    }

    @Override
    public boolean put(K key, V val) {
        if (key == null || val == null)
            return false;
        STORAGE.put(key, val);
        return true;
    }

}
