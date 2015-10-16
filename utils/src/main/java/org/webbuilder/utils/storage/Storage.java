package org.webbuilder.utils.storage;

import org.webbuilder.utils.storage.event.KeyFilter;
import org.webbuilder.utils.storage.event.StorageListener;
import org.webbuilder.utils.storage.event.Finder;
import org.webbuilder.utils.storage.exception.StorageException;
import org.webbuilder.utils.storage.instance.parser.ByteStorageParser;
import org.webbuilder.utils.storage.instance.redis.RedisHashStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 存储器抽象类，基于键值对
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public abstract class Storage<K, V> implements Serializable {

    private transient static final ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();

    /**
     * 日志
     */
    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 事件最大数量
     */
    public transient static final int event_max_num = 100;

    /**
     * 事件容器
     */
    private List<StorageListener<K, V>> listeners = new LinkedList();

    /**
     * 注册一个事件
     *
     * @param e 事件
     * @return 返回当前Storage实体
     * @throws Exception 注册异常
     */
    public Storage addListener(StorageListener<K, V> e) throws Exception {
        if (e == null)
            throw new StorageException(getName() + " listener is null!");
        if (listeners.size() > event_max_num)
            throw new StorageException(getName() + " listener is too many!");
        synchronized (listeners) {
            if (logger.isDebugEnabled())
                logger.debug(getName() + " add listener:" + e);
            listeners.add(e);
        }
        return this;
    }

    /**
     * 获取所有事件
     *
     * @return 事件集合
     */
    protected List<StorageListener<K, V>> getListeners() {
        return listeners;
    }

    //存储器名称 唯一
    protected String name;

    /**
     * 默认构造方法
     */
    public Storage() {
    }

    /**
     * 初始化存储器名称的构造方法
     *
     * @param name 存储器名称
     */
    public Storage(final String name) {
        this.name = name;
    }

    /**
     * 根据key获取存储内容
     *
     * @param key 键
     * @return 存储内容
     */
    public abstract V get(final K key);

    /**
     * 根据key获取存储内容
     *
     * @param key 键
     * @return 存储内容
     */
    public V get(final K key, StorageListener<K, V> listener) {
        V value = get(key);
        if (value != null) {
            value = listener.onNotFoundVal(key);
            if (value != null) {
                this.put(key, value);
            }
        }
        return value;
    }

    /**
     * 获取所有存储数据默认方法（通过finder获取）
     *
     * @return 所有缓存结果
     */
    public List<V> get() {
        return find(new Finder<K, V>() {
            @Override
            public boolean each(int index,K key, V val) {
                return true;
            }
        });
    }

    /**
     * 根据key搜索存储内容
     *
     * @param condition 搜索条件
     * @return 搜索结果, 不推荐返回null
     */
    public List<V> search(Map<String, Object> condition) {
        return new ArrayList<V>();
    }

    /**
     * 存储数据，如果key已存在则覆盖
     *
     * @param key 键
     * @param val 值
     * @return 是否存储成功
     */
    public abstract boolean put(final K key, final V val);

    /**
     * 存储寿命有限的数据，如果key已存在则覆盖,默认使用Timer进行寿命处理
     *
     * @param key     键
     * @param val     值
     * @param timeout 数据寿命(分钟)
     * @return 是否存储成功
     */
    public boolean put(final K key, final V val, final long timeout) {
        if (put(key, val)) {
            schedule.schedule(new Thread() {
                @Override
                public void run() {
                    if (logger.isDebugEnabled())
                        logger.debug(getName() + " key:" + key + " is timeout(" + timeout + "s),execute remove!");
                    remove(key);
                }
            }, timeout, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    /**
     * 根据key删除个数据
     *
     * @param key 预计删除的key
     * @return 是否删除成功
     */
    public abstract boolean remove(final K key);

    /**
     * 清空存储器
     *
     * @return 共清除多少条记录
     */
    public abstract int clear();

    /**
     * 存储器记录总数
     *
     * @return 共清除多少条记录
     */
    public abstract int size();

    /**
     * 查找结果（循环整个存储结果集进行回掉查找）
     *
     * @param finder 查找器 使用方法见{@link Finder}
     * @return 查找结果
     */
    public abstract List<V> find(Finder<K, V> finder);

    /**
     * 初始化，初始化时，将调用所有{@link StorageListener#onLoad(Storage)}方法
     *
     * @throws Exception 初始化异常
     */
    public void init() throws Exception {
        for (StorageListener<K, V> e : getListeners()) {
            e.onLoad(this);
        }
        if (logger.isDebugEnabled())
            logger.debug("init " + getName() + " success!");
    }

    public Set<K> keySet() {
        return keySet(new KeyFilter<K>() {
            @Override
            public boolean each(K key) {
                return true;
            }
        });
    }

    public Set<K> keySet(KeyFilter<K> filter) {
        return null;
    }

    public boolean containsKey(K key) {
        return keySet().contains(key);
    }

    /**
     * 获取存储器名称
     *
     * @return 存储器名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置存储器名称
     *
     * @param name 存储器名称
     */
    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

}
