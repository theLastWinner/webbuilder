package org.webbuilder.utils.storage.instance.redis;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.StorageParser;
import org.webbuilder.utils.storage.instance.parser.JsonStorageParser;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-08-10 0010.
 */
public abstract class RedisStorage<K, V> extends Storage<K, V> {
    /**
     * redis连接池
     */
    protected ShardedJedisPool pool = null;

    /**
     * 存储的类型
     */
    private Class<V> type = null;

    protected StorageParser parser = null;

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        if (name == null)
            name = getType().getName();
        return super.getName();
    }

    @Override
    public void init() throws Exception {
    }

    public ShardedJedisPool getPool() {
        return pool;
    }

    public void setPool(ShardedJedisPool pool) {
        this.pool = pool;
    }

    public ShardedJedis getResource() {
        return pool.getResource();
    }

    public void resetResource(ShardedJedis jedis) {
    }

    public Class<V> getType() {
        return type;
    }

    public void setType(Class<V> type) {
        this.type = type;
    }

    public StorageParser getParser() {
        return parser;
    }

    public void setParser(StorageParser parser) {
        this.parser = parser;
    }

}
