package org.webbuilder.sql.render.template;

import org.webbuilder.sql.TableMetaData;

import java.util.HashMap;

/**
 * 渲染参数
 * Created by 浩 on 2015-11-06 0006.
 */
public class SqlRenderParam extends HashMap<String, Object> {

    private TableMetaData tableMetaData;

    private SqlTemplate.TYPE type;


    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public SqlTemplate.TYPE getType() {
        return type;
    }

    public void setType(SqlTemplate.TYPE type) {
        this.type = type;
    }
}
