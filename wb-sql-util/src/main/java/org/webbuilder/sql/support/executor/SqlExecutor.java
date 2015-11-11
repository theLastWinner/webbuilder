package org.webbuilder.sql.support.executor;


import org.webbuilder.sql.SQL;

import java.util.List;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public interface SqlExecutor {

    <T> List<T> list(SQL sql, ObjectWrapper<T> wrapper) throws Exception;

    <T> T single(SQL sql, ObjectWrapper<T> wrapper) throws Exception;

    void exec(SQL sql) throws Exception;

    int update(SQL sql) throws Exception;

    int delete(SQL sql) throws Exception;

    int insert(SQL sql) throws Exception;

}
