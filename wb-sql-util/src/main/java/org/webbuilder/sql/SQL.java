package org.webbuilder.sql;

import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface SQL {

    TableMetaData getTableMetaData();

    /**
     * 获取sql语句模板
     *
     * @return sql语句模板
     */
    String getSql();

    /**
     * 获取预编译参数
     *
     * @return
     */
    Map<String, Object> getParams();

    /**
     * 获取关联查询的sql
     *
     * @return
     */
    List<BindSQL> getBinds();

    int size();
}
