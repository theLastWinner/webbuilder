package org.webbuilder.web.core.cache;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.StorageDriver;
import org.webbuilder.utils.storage.driver.redis.RedisStorageDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * spring cache支持，具体cache策略由StorageDriver实现
 * Created by 浩 on 2015-08-24 0024.
 */
public class SpringCacheImpl implements Cache {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 缓存驱动
     */
    private StorageDriver driver;

    /**
     * 缓存名称
     */
    private String name;

    /**
     * 缓存超时
     */
    private int timeout = -1;

    @Override
    public String getName() {
        return name;
    }

    /**
     * 缓存名称进行前缀追加，避免缓存驱动内部包含其他类型的缓存时，造成冲突
     *
     * @return 追加前缀的缓存名称
     */
    public String getCacheName() {
        return "spring-data-cache.".concat(name);
    }

    @Override
    public Object getNativeCache() {
        try {
            return driver.getStorage(getCacheName());
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 根据key一个缓存，由{@link Storage}实现
     *
     * @param key key
     * @return 值，未获取到时返回null
     */
    @Override
    public ValueWrapper get(Object key) {
        try {
            Storage storage = driver.getStorage(getCacheName());
            Object value = storage.get(String.valueOf(key));
            if (logger.isDebugEnabled()) {
                if (value != null)//命中缓存
                    logger.debug(getCacheName().concat("-->").concat(String.valueOf(key)).concat(" hit!"));
            }
            return (value != null ? new SimpleValueWrapper(value) : null);
        } catch (Exception e) {
            logger.error("get cache error:" + key, e);
        }
        return null;
    }

    /**
     * 获取指定类型的值
     *
     * @param key  key
     * @param type 类型
     * @param <T>  泛型
     * @return key对应的值
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        try {
            Storage<Object, T> storage = driver.getStorage(getCacheName(), type);
            T value = storage.get(String.valueOf(key));
            return value;
        } catch (Exception e) {
            logger.error(String.format("get cache error:%s,class:%s", key, type), e);
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        try {
            if (key == null || value == null) return;
            Storage cache = driver.getStorage(getCacheName(), value.getClass());
            if (timeout > 0)
                cache.put(key, value, timeout);
            else
                cache.put(key, value);
        } catch (Exception e) {
            logger.error(String.format("put cache error:%s,value:%s", key, value), e);
        }
    }

    /**
     * 移除指定key的缓存
     *
     * @param key key
     */
    @Override
    public void evict(Object key) {
        try {
            if (logger.isDebugEnabled())
                logger.debug(String.format("remove cache:%s > %s", getCacheName(), key));
            Storage cache = driver.getStorage(getCacheName());
            cache.remove(key);
        } catch (Exception e) {
            logger.error(String.format("remove cache error:%s", key), e);
        }
    }

    /**
     * 清空所有缓存
     */
    @Override
    public void clear() {
        try {
            driver.getStorage(getCacheName()).clear();
            if (logger.isDebugEnabled())
                logger.debug(String.format("remove all cache:%s", getCacheName()));
        } catch (Exception e) {
            logger.error("clear cache error", e);
        }
    }

    public StorageDriver getDriver() {
        return driver;
    }

    public void setDriver(StorageDriver driver) {
        this.driver = driver;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
