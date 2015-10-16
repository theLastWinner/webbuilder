package org.webbuilder.utils.storage.event;

import java.io.Serializable;

/**
 * 存储查找器，用于遍历查找想要的存储内容
 * Created by 浩 on 2015-6-13.
 */
public abstract class Finder<K, V> implements Serializable {
    private boolean isOver = false;

    /**
     * 每遍历一个结果即调用此方法进行判断，返回true则为想要的结果
     *
     * @param key 本次结果的key
     * @param val 本次结果的value
     * @return 是否为想要的结果
     */
    public abstract boolean each(int index,K key, V val);

    /**
     * 如果在遍历中途想提前结束遍历，则调用此方法结束遍历
     */
    public void findOver() {
        isOver = true;
    }

    /**
     * 遍历是否已经结束
     *
     * @return 是否已经结束遍历
     */
    public boolean isOver() {
        return isOver;
    }

}
