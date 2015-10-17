package org.webbuilder.utils.db.exec;

import java.util.List;

/**
 * Sql执行器接口，用于根据ExecutorConfig配置执行对应的sql语句
 * Created by 浩 on 2015-07-08 0008.
 */
public interface SqlExecutor {
    int exec(ExecutorConfig<?> config) throws Exception;

    <T> List<T> select(ExecutorConfig<T> config) throws Exception;

    <T> T selectOne(ExecutorConfig<T> config) throws Exception;

    int insert(ExecutorConfig<?> config) throws Exception;

    int update(ExecutorConfig<?> config) throws Exception;

    int delete(ExecutorConfig<?> config) throws Exception;

}
