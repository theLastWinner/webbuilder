package org.webbuilder.utils.storage.driver;


import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.event.DriverListener;

import java.io.Serializable;


/**
 * 存储器驱动接口，用于注册，获取，初始化驱动
 * Created by 浩 on 2015-08-07 0007.
 */
public interface StorageDriver extends Serializable {

    /**
     * 向驱动注册一个存储器
     *
     * @param storage 存储器实例
     * @param <K>     存储器 key
     * @param <V>     存储器 value
     * @return 注册成功的存储器实例
     * @throws Exception 异常信息
     */
    <K, V> Storage<K, V> registerStorage(Storage<K, V> storage) throws Exception;

    /**
     * 根据名称获取一个存储器
     *
     * @param name 存储器名称
     * @param <K>  存储器 key
     * @param <V>  存储器 value
     * @return 存储器实例
     * @throws Exception 异常信息
     */
    <K, V> Storage<K, V> getStorage(String name) throws Exception;

    /**
     * 根据名称，和class获取一个存储器
     *
     * @param name 存储器名称
     * @param type 存储类型
     * @param <K>  存储器 key
     * @param <V>  存储器 value
     * @return 存储器实例
     * @throws Exception 异常信息
     */
    <K, V> Storage<K, V> getStorage(String name, Class<V> type) throws Exception;

    /**
     * 根据class获取一个存储器，存储器名称默认为class.name
     *
     * @param type 存储器类型
     * @param <K>  存储器 key
     * @param <V>  存储器 value
     * @return 存储器实例
     * @throws Exception 异常信息
     */
    <K, V> Storage<K, V> getStorage(Class<V> type) throws Exception;

    /**
     * 初始化驱动
     *
     * @throws Exception 初始化驱动
     */
    void init() throws Exception;

    /**
     * 初始化所有存储器
     *
     * @throws Exception 异常信息
     */
    void reload() throws Exception;

    /**
     * 向驱动注册一个监听器
     *
     * @param listener 监听器实例
     * @throws Exception 异常信息
     */
    void addListener(DriverListener listener) throws Exception;

    /**
     * 移除一个监听器
     *
     * @param listener 监听器实例
     * @throws Exception 异常信息
     */
    void removeListener(DriverListener listener) throws Exception;

    /**
     * 获取驱动名称
     *
     * @return 驱动名称
     */
    String getName();


}
