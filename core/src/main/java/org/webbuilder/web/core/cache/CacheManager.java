package org.webbuilder.web.core.cache;

import org.webbuilder.utils.storage.driver.StorageDriver;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.LinkedList;

/**
 * spring 缓存管理器
 * Created by 浩 on 2015-08-24 0024.
 */
public class CacheManager extends AbstractCacheManager {

    private Collection<? extends Cache> caches;

    public CacheManager() {
    }

    private StorageDriver driver;

    /**
     * 根据名称获取缓存，缓存不存在时，自动创建一个缓存实例{@link SpringCacheImpl}
     *
     * @param name 缓存名称
     * @return 缓存实例
     */
    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null) {
            SpringCacheImpl tmp = new SpringCacheImpl();
            tmp.setDriver(driver);
            tmp.setName(name);
            addCache(cache = tmp);
        }
        return cache;
    }

    public void setCaches(Collection<? extends Cache> caches) {
        this.caches = caches;
    }

    protected Collection<? extends Cache> loadCaches() {
        if (caches == null)
            caches = new LinkedList<>();
        return this.caches;
    }

    public StorageDriver getDriver() {
        return driver;
    }

    public void setDriver(StorageDriver driver) {
        this.driver = driver;
    }
}
