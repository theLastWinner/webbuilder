package org.webbuilder.utils.storage.instance.redis;

import org.webbuilder.utils.storage.event.Finder;
import org.webbuilder.utils.storage.event.KeyFilter;
import org.webbuilder.utils.storage.event.StorageListener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.*;

/**
 * redis hash存储的实现
 * Created by 浩 on 2015-08-10 0010.
 */
public class RedisHashStorage<K, V> extends RedisStorage<K, V> {
    /**
     * 根据key获取存储内容
     *
     * @param key 键
     * @return 存储内容
     */
    @Override
    public V get(K key) {
        String hkey = getName();
        String skey = String.valueOf(key);
        try (ShardedJedis jedis = getResource()) {
            byte[] val = jedis.hget(hkey.getBytes(), skey.getBytes());
            V v = null;
            if (val == null) {
                for (StorageListener<K, V> listener : getListeners()) {
                    v = listener.onNotFoundVal(key);
                    if (v != null)
                        break;
                }
                if (v != null)
                    put(key, v);
                return v;
            } else {
                v = getParser().deserialize(skey, val);
            }
            return v;
        }
    }

    /**
     * 存储数据，如果key已存在则覆盖
     *
     * @param key 键
     * @param val 值
     * @return 是否存储成功
     */
    @Override
    public boolean put(K key, V val) {
        try (ShardedJedis jedis = getResource()) {
            String hkey = getName();
            String skey = String.valueOf(key);
            jedis.hset(hkey.getBytes(), skey.getBytes(), getParser().serialize(skey, val));
        }
        return true;
    }

    /**
     * 根据key删除个数据
     *
     * @param key 预计删除的key
     * @return 是否删除成功
     */
    @Override
    public boolean remove(K key) {
        try (ShardedJedis jedis = getResource()) {
            String hkey = getName();
            String skey = String.valueOf(key);
            jedis.hdel(hkey, skey);
        }
        return false;
    }

    /**
     * 清空存储器
     *
     * @return 共清除多少条记录
     */
    @Override
    public int clear() {
        try (ShardedJedis jedis = getResource()) {
            int size = size();
            String hkey = getName();
            jedis.del(hkey);
            return size;
        }
    }

    /**
     * 存储器记录总数
     *
     * @return 共清除多少条记录
     */
    @Override
    public int size() {
        try (ShardedJedis jedis = getResource()) {
            String hkey = getName();
            return jedis.hlen(hkey).intValue();
        }
    }

    @Override
    public boolean containsKey(K key) {
        try (ShardedJedis jedis = getResource()) {
            String hkey = getName();
            jedis.hexists(hkey, String.valueOf(key));
        }
        return super.containsKey(key);
    }

    @Override
    public Set<K> keySet(KeyFilter filter) {
        Set<K> newSet = new LinkedHashSet<>();
        try (ShardedJedis jedis = getResource()) {
            Set<String> keys = jedis.hkeys(getName());
            for (String key : keys) {
                if (filter.isOver()) break;
                if (filter.each(key)) {
                    newSet.add((K)key);
                }
            }
            return newSet;
        }
    }

    @Override
    public Set<K> keySet() {
        try (ShardedJedis jedis = getResource()) {
            return (Set<K>) jedis.hkeys(getName());
        }
    }

    /**
     * 查找结果（循环整个存储结果集进行回掉查找）
     *
     * @param finder 查找器 使用方法见{@link Finder}
     * @return 查找结果
     */
    @Override
    public List<V> find(Finder<K, V> finder) {
        List<V> datas = new ArrayList<>();
        try (ShardedJedis jedis = getResource()) {
            String hkey = getName();
            Map<byte[], byte[]> data = jedis.hgetAll(hkey.getBytes());
            int count=0;
            for (Map.Entry<byte[], byte[]> entry : data.entrySet()) {
                if (finder.isOver())
                    break;
                K key = (K) entry.getKey();
                V v = getParser().deserialize(new String(entry.getKey()), entry.getValue());
                if (finder.each(++count,key, v)) {
                    datas.add(v);
                }
            }
            return datas;
        }
    }

}
