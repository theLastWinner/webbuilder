package org.webbuilder.sql;

import org.webbuilder.sql.exception.DeleteException;
import org.webbuilder.sql.exception.InsertException;
import org.webbuilder.sql.exception.QueryException;
import org.webbuilder.sql.exception.UpdateException;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Table {

    /**
     * 获取表元数据
     *
     * @return 元数据
     */
    TableMetaData getMetaData();

    /**
     * 创建查询器
     *
     * @return 查询器
     * @throws QueryException 创建查询器异常
     */
    Query createQuery() throws QueryException;

    Update createUpdate() throws UpdateException;

    Delete createDelete() throws DeleteException;

    Insert createInsert() throws InsertException;
}
