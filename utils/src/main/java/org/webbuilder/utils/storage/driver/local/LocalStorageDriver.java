package org.webbuilder.utils.storage.driver.local;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.CommonStorageDriver;
import org.webbuilder.utils.storage.driver.StorageDriver;
import org.webbuilder.utils.storage.driver.StorageDriverManager;
import org.webbuilder.utils.storage.driver.event.DriverListener;
import org.webbuilder.utils.storage.exception.StorageException;
import org.webbuilder.utils.storage.instance.LocalCacheStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-08-07 0007.
 */
public class LocalStorageDriver extends CommonStorageDriver {
    private final Map<String, LocalCacheStorage> cache = new ConcurrentHashMap<>();

    private final List<DriverListener> listeners = new LinkedList<>();

    public static final String NAME = "local";

    private static LocalStorageDriver defaultDriver = null;

    //单例，本地缓存一律使用单例模式
    private LocalStorageDriver() {
    }

    static {
        if (defaultDriver == null) {
            defaultDriver = new LocalStorageDriver();
        }
        //注册驱动
        StorageDriverManager.registerDriver(defaultDriver);
        try {
            defaultDriver.addListener(new DriverListener() {
                @Override
                public Storage storageNotFound(String name) {
                    return new LocalCacheStorage(name);
                }

                @Override
                public Storage storageNotFound(Class type) {
                    return new LocalCacheStorage(type.getName());
                }

                @Override
                public Storage storageNotFound(String name, Class type) {
                    return new LocalCacheStorage(name);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <K, V> Storage<K, V> registerStorage(Storage<K, V> storage) throws Exception {
        if (!(storage instanceof LocalCacheStorage)) {
            throw new StorageException("storage not instanceof LocalCacheStorage!");
        }
        return super.registerStorage(storage);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init() throws Exception {

    }
}
