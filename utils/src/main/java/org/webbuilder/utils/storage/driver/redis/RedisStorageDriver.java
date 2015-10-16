package org.webbuilder.utils.storage.driver.redis;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.StorageParser;
import org.webbuilder.utils.storage.driver.CommonStorageDriver;
import org.webbuilder.utils.storage.driver.StorageDriverManager;
import org.webbuilder.utils.storage.driver.event.DriverListener;
import org.webbuilder.utils.storage.exception.StorageException;
import org.webbuilder.utils.storage.instance.redis.RedisHashStorage;
import org.webbuilder.utils.storage.instance.redis.RedisStorage;
import redis.clients.jedis.ShardedJedisPool;


/**
 * Redis存储驱动器，用于获取redis存储实现
 * Created by 浩 on 2015-08-10 0010.
 */
public class RedisStorageDriver extends CommonStorageDriver {

    /**
     * 默认名称，如果未设置驱动名称，将默认使用此名称
     */
    private static final String DEFAULT_NAME = "redis";

    /**
     * redis 连接池
     */
    private ShardedJedisPool pool = null;

    /**
     * 驱动监听器
     */
    private DriverListener default_listener = null;

    /**
     * 默认存储实现类
     */
    private Class<? extends RedisStorage> defaultStorageClass = null;

    /**
     * 默认构造方法，使用默认名称，不使用链接池进行初始化
     */
    public RedisStorageDriver() {
        this(DEFAULT_NAME, null);
    }

    /**
     * 指定驱动名称进行初始化，不使用链接池
     *
     * @param name 动名称
     */
    public RedisStorageDriver(String name) {
        this(name, null);
    }

    /**
     * 指定连接池，使用默认名称进行初始化
     *
     * @param pool 连接池实例
     */
    public RedisStorageDriver(ShardedJedisPool pool) {
        this(DEFAULT_NAME, pool);
    }

    /**
     * 指定名称和连接池进行初始化
     *
     * @param name 驱动名称
     * @param pool 连接池实例
     */
    public RedisStorageDriver(String name, ShardedJedisPool pool) {
        this.name = name;
        this.pool = pool;
    }

    /**
     * 初始化，创建一个默认的DriverListener监听器，该监听器在企图向驱动获取一个存储器，但是驱动未找到存储器时进行存储器的自动创建
     * <br/>如果链接池为null，将抛出{@link StorageException} "redis pool is null!" 异常
     *
     * @throws Exception 初始化异常信息
     */
    @Override
    public void init() throws Exception {
        if (pool == null)
            throw new StorageException("redis pool is null!");
        //初始化默认的监听器，当存储器未找到时，进行自动创建
        default_listener = new DriverListener() {
            @Override
            public Storage storageNotFound(String name) {
                RedisStorage redisMapStorage = newStorageInstance();
                redisMapStorage.setName(name);
                redisMapStorage.setType(Object.class);
                redisMapStorage.setPool(getPool());
                StorageParser storageParser = getParserInstance();
                storageParser.setType(Object.class);
                redisMapStorage.setParser(storageParser);
                try {
                    redisMapStorage.init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return redisMapStorage;
            }

            @Override
            public Storage storageNotFound(Class type) {
                RedisStorage redisMapStorage = newStorageInstance();
                redisMapStorage.setType(type);
                redisMapStorage.setPool(getPool());
                StorageParser storageParser = getParserInstance();
                storageParser.setType(type);
                redisMapStorage.setParser(storageParser);
                try {
                    redisMapStorage.init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return redisMapStorage;
            }

            @Override
            public Storage storageNotFound(String name, Class type) {
                RedisStorage redisMapStorage = newStorageInstance();
                redisMapStorage.setName(name);
                redisMapStorage.setType(type);
                redisMapStorage.setPool(getPool());
                StorageParser storageParser = getParserInstance();
                storageParser.setType(type);
                redisMapStorage.setParser(storageParser);
                try {
                    redisMapStorage.init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return redisMapStorage;
            }
        };
        addListener(default_listener);
        //自动注册驱动
        StorageDriverManager.registerDriver(this);
    }

    /**
     * 注册一个存储器，存储器必须继承{@link RedisStorage}
     *
     * @param storage 存储器实例
     * @param <K>     键类型
     * @param <V>     值类型
     * @return 注册后的存储器实例
     * @throws Exception 异常信息
     */
    @Override
    public <K, V> Storage<K, V> registerStorage(Storage<K, V> storage) throws Exception {
        if (!(storage instanceof RedisStorage)) {
            throw new StorageException("storage not instanceof RedisStorage!");
        }
        RedisStorage redisStorage = (RedisStorage) storage;
        redisStorage.setPool(pool);
        return super.registerStorage(redisStorage);
    }

    /**
     * 获取驱动名
     *
     * @return 驱动名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 设置驱动名称
     *
     * @param name 驱动名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取此驱动的连接池
     *
     * @return 连接池
     */
    protected ShardedJedisPool getPool() {
        return pool;
    }

    /**
     * 设置此驱动的连接池
     *
     * @return 连接池
     */
    public void setPool(ShardedJedisPool pool) {
        this.pool = pool;
    }

    /**
     * 创建存储器实例
     *
     * @return 存储器实例
     */
    protected RedisStorage newStorageInstance() {
        try {
            return getDefaultStorageClass().newInstance();
        } catch (Exception e) {
            return new RedisHashStorage();
        }
    }

    /**
     * 获取默认的存储器实现类
     *
     * @return 储器实现类
     */
    protected Class<? extends RedisStorage> getDefaultStorageClass() {
        return defaultStorageClass;
    }

    /**
     * 设置默认的存储器实现类，实现类为{@link RedisStorage}的子类
     *
     * @param defaultStorageClass 存储器实现类
     */
    public void setDefaultStorageClass(Class<? extends RedisStorage> defaultStorageClass) {
        this.defaultStorageClass = defaultStorageClass;
    }
}
