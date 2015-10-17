package org.webbuilder.utils.storage.driver.event;

import org.webbuilder.utils.storage.Storage;

import java.io.Serializable;

/**
 * 驱动监听器
 * Created by 浩 on 2015-08-07 0007.
 */
public interface DriverListener extends Serializable {
    /**
     * 根据name获取存储器未找到时触发此回掉
     *
     * @param name 存储器名称
     * @return 回掉返回存储器实例
     */
    Storage storageNotFound(String name);

    /**
     * 根据class获取存储器未找到时触发此回掉
     *
     * @param type class
     * @return 回掉返回存储器实例
     */
    Storage storageNotFound(Class type);

    /**
     * 根据name和class获取存储器未找到时触发此回掉
     *
     * @param name 存储器名称
     * @param type class
     * @return 回掉返回存储器实例
     */
    Storage storageNotFound(String name, Class type);
}
