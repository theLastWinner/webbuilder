package org.webbuilder.utils.db.def;


import org.webbuilder.utils.base.ClassUtil;

/**
 * Created by 浩 on 2015-07-09 0009.
 */
public abstract class DataFormater<T> {
    public <T extends DataFormater> Class<T> getType() {
        return (Class<T>) ClassUtil.getGenericType(this.getClass());
    }

    public abstract Object format(T value);
}
