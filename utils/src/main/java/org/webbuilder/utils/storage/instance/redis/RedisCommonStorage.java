package org.webbuilder.utils.storage.instance.redis;

import org.webbuilder.utils.storage.event.Finder;
import org.webbuilder.utils.storage.event.KeyFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;

import java.util.*;

/**
 * Created by 浩 on 2015-08-24 0024.
 */
public class RedisCommonStorage extends RedisStorage {

    /**
     * 根据key获取存储内容
     *
     * @param key 键
     * @return 存储内容
     */
    @Override
    public Object get(Object key) {
        byte[] n_key = String.valueOf(key).getBytes();
        try (ShardedJedis jedis = getResource()) {
            return getParser().deserialize(String.valueOf(key), jedis.get(n_key));
        }
    }

    @Override
    public boolean put(Object key, Object val, long timeout) {
        byte[] n_key = String.valueOf(key).getBytes();
        try (ShardedJedis jedis = getResource()) {
            jedis.set(n_key, getParser().serialize(String.valueOf(key), val));
            jedis.expire(n_key, (int) timeout);
            return true;
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
    public boolean put(Object key, Object val) {
        byte[] n_key = String.valueOf(key).getBytes();
        try (ShardedJedis jedis = getResource()) {
            jedis.set(n_key, getParser().serialize(String.valueOf(key), val));
            return true;
        }
    }

    /**
     * 根据key删除个数据
     *
     * @param key 预计删除的key
     * @return 是否删除成功
     */
    @Override
    public boolean remove(Object key) {
        byte[] n_key = String.valueOf(key).getBytes();
        try (ShardedJedis jedis = getResource()) {
            jedis.del(n_key);
            return true;
        }
    }

    /**
     * 清空存储器
     *
     * @return 共清除多少条记录
     */
    @Override
    public int clear() {
        try (ShardedJedis jedis = getResource()) {
            for (Jedis jedis1 : jedis.getAllShards()) {
                jedis1.flushDB();
            }
            // jedis.d
        }
        return 0;
    }

    @Override
    public boolean containsKey(Object key) {
        try (ShardedJedis jedis = getResource()) {
            return jedis.exists(String.valueOf(key));
        }
    }

    @Override
    public Set<String> keySet(final KeyFilter filter) {
        final Set<String> keys = new LinkedHashSet<>();
        try (ShardedJedis jedis = getResource()) {
            for (Jedis jedis1 : jedis.getAllShards()) {
                if (filter.isOver()) break;
                scanKey("0", jedis1, new ScanCallBack() {
                    @Override
                    public String pattern() {
                        return filter.pattern();
                    }

                    @Override
                    void next(byte[] key) {
                        if (filter.isOver()) over();
                        String skey = new String(key);
                        if (filter.each(skey)) {
                            keys.add(skey);
                        }
                    }
                });
            }
        }
        return keys;
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        try (ShardedJedis jedis = getResource()) {
            for (Jedis jedis1 : jedis.getAllShards()) {
                keys.addAll(jedis1.keys("*"));
            }
        }
        return keys;
    }

    /**
     * 存储器记录总数
     *
     * @return 共清除多少条记录
     */
    @Override
    public int size() {
        int size = 0;
        try (ShardedJedis jedis = getResource()) {
            for (Jedis jedis1 : jedis.getAllShards()) {
                size += jedis1.dbSize();
            }
        }
        return size;
    }

    /**
     * 查找结果，通过扫描key来获取结果
     *
     * @param finder 查找器 使用方法见{@link Finder}
     * @return 查找结果
     */
    @Override
    public List find(final Finder finder) {
        final List list = new LinkedList();

        try (final ShardedJedis jedis = getResource()) {
            //扫描key
            keySet(new KeyFilter<String>() {
                int count = 0;
                @Override
                public boolean each(String key) {
                    if (finder.isOver()) over();//退出扫描
                    byte[] value = jedis.get(key.getBytes());
                    Object val = null;
                    try {
                        val = getParser().deserialize(new String(key), value);
                    } catch (Exception e) {
                    }
                    if (finder.each(++count, key, val)) {
                        list.add(val);
                    }
                    return false;
                }
            });
        }
        return list;
    }

    /**
     * 扫描key，通过redis游标来进行key遍历并回掉。当回掉中over()==true时停止扫描
     *
     * @param cursor   游标位置
     * @param jedis    jedis实例
     * @param callBack 扫描回掉实例
     */
    protected void scanKey(String cursor, Jedis jedis, ScanCallBack callBack) {
        ScanParams params = new ScanParams();
        if (callBack.pattern() != null) {
            params.match(callBack.pattern());
        }
        //扫描结果
        ScanResult<byte[]> res = jedis.scan(cursor.getBytes(), params);
        for (byte[] bytes : res.getResult()) {
            if (callBack.isOver()) return;
            callBack.next(bytes);
        }
        if (!"0".equals(res.getStringCursor()) && !callBack.isOver()) {
            scanKey(res.getStringCursor(), jedis, callBack);
        }
    }

    private abstract class ScanCallBack {
        private boolean over = false;

        abstract void next(byte[] key);

        public boolean isOver() {
            return over;
        }

        public String pattern() {
            return null;
        }

        public void over() {
            over = true;
        }

    }
}
