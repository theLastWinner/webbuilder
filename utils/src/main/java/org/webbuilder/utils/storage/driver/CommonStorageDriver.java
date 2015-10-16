package org.webbuilder.utils.storage.driver;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.StorageParser;
import org.webbuilder.utils.storage.driver.event.DriverListener;
import org.webbuilder.utils.storage.instance.parser.JsonStorageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认存储器驱动抽象类，实现部分驱动方法，拓展此类即可实现存储器驱动
 * Created by 浩 on 2015-08-10 0010.
 */
public abstract class CommonStorageDriver implements StorageDriver {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 存储器缓存
     */
    protected final Map<String, Storage> cache = new ConcurrentHashMap<>();

    /**
     * 用于spring注入
     */
    protected transient List<Storage> storageReg = null;

    /**
     * 监听器集合
     */
    private final List<DriverListener> listeners = new ArrayList<>();

    /**
     * 默认的对象解析器类型
     */
    private Class<? extends StorageParser> defaultParserClass = null;

    /**
     * 驱动名称
     */
    protected String name;

    /**
     * 注册一个存储器
     *
     * @param storage 存储器实例
     * @param <K>     键类型
     * @param <V>     值类型
     * @return 注册后的存储器实例
     * @throws Exception 异常信息
     */
    @Override
    public <K, V> Storage<K, V> registerStorage(Storage<K, V> storage) throws Exception {
        return cache.put(storage.getName(), storage);
    }

    /**
     * 添加一个监听器
     *
     * @param listener 监听器实例
     * @throws Exception 添加异常
     */
    @Override
    public void addListener(DriverListener listener) throws Exception {
        listeners.add(listener);
    }

    /**
     * 移除一个监听器
     *
     * @param listener 监听器实例
     * @throws Exception 移除异常
     */
    @Override
    public void removeListener(DriverListener listener) throws Exception {
        listeners.remove(listener);
    }

    /**
     * 根据存储器名称获取存储器实例，当未获取到时，触发{@link DriverListener#storageNotFound(String)}事件进行获取
     *
     * @param name 存储器名称
     * @param <K>  存储器 key类型
     * @param <V>  存储器value类型
     * @return 存储器实例
     * @throws Exception 异常信息
     */
    @Override
    public <K, V> Storage<K, V> getStorage(String name) throws Exception {
        Storage<K, V> storage = cache.get(name);
        if (storage == null) {
            for (DriverListener listener : listeners) {
                storage = listener.storageNotFound(name);
                if (storage != null)
                    break;
            }
            if (storage != null)
                registerStorage(storage);
        }
        return storage;
    }

    /**
     * 根据class获取存储器，当未获取到时，触发{@link DriverListener#storageNotFound(Class)}事件进行获取
     *
     * @param type 存储器名称
     * @param <K>  存储器 key类型
     * @param <V>  存储器value类型
     * @return 存储器实例
     * @throws Exception 异常信息
     */
    @Override
    public <K, V> Storage<K, V> getStorage(Class<V> type) throws Exception {
        Storage<K, V> storage = cache.get(getNameByType(type));
        if (storage == null) {
            for (DriverListener listener : listeners) {
                storage = listener.storageNotFound(type);
                if (storage != null)
                    break;
            }
            if (storage != null)
                registerStorage(storage);
        }
        return storage;
    }

    /**
     * 根据class和name获取存储器，当未获取到时，触发{@link DriverListener#storageNotFound(String,Class)}事件进行获取
     *
     * @param type 存储器名称
     * @param <K>  存储器 key类型
     * @param <V>  存储器value类型
     * @return 存储器实例
     * @throws Exception 异常信息
     */
    @Override
    public <K, V> Storage<K, V> getStorage(String name, Class<V> type) throws Exception {
        Storage<K, V> storage = cache.get(name);
        if (storage == null) {
            for (DriverListener listener : listeners) {
                storage = listener.storageNotFound(name, type);
                if (storage != null)
                    break;
            }
            if (storage != null)
                registerStorage(storage);
        }
        return storage;
    }

    @Override
    public void reload() throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("reload storage...");
        for (Map.Entry<String, Storage> entry : cache.entrySet()) {
            entry.getValue().init();
        }
        if (logger.isDebugEnabled())
            logger.debug("reload storage success");
    }


    public String getNameByType(Class<?> type) {
        return type.getName();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends StorageParser> getDefaultParserClass() {
        if (defaultParserClass == null)
            defaultParserClass = JsonStorageParser.class;
        return defaultParserClass;
    }

    public void setDefaultParserClass(Class<? extends StorageParser> defaultParserClass) {
        this.defaultParserClass = defaultParserClass;
    }

    public void setStorageReg(List<Storage> storageReg) {
        for (Storage storage : storageReg) {
            try {
                registerStorage(storage);
            } catch (Exception e) {
                logger.error("注册存储器失败:" + storage, e);
            }
        }
        this.storageReg = null;
    }

    public StorageParser getParserInstance() {
        try {
            return getDefaultParserClass().newInstance();
        } catch (Exception e) {
            logger.error("实例化parser失败,将使用默认parser", e);
            return new JsonStorageParser();
        }
    }
}
