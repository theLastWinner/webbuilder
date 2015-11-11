package org.webbuilder.sql.support.executor;

/**
 * Created by 浩 on 2015-11-10 0010.
 */
public interface DataFormat<T, V> {
    Class<T> support();

    V format(T data);
}
