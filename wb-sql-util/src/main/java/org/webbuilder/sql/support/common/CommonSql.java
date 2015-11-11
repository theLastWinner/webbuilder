package org.webbuilder.sql.support.common;

import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;

import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-07 0007.
 */
public class CommonSql implements SQL {
    private String sql;

    private Map<String, Object> params;

    private List<BindSQL> bindSQLs;

    private TableMetaData tableMetaData;

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public Map<String, Object> getParams() {
        return this.params;
    }

    @Override
    public List<BindSQL> getBinds() {
        return this.bindSQLs;
    }

    @Override
    public int size() {
        int size = 1;
        if (getBinds() != null) {
            for (BindSQL bindSQL : getBinds()) {
                size += bindSQL.getSql().size();
            }
        }
        return size;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<BindSQL> getBindSQLs() {
        return bindSQLs;
    }

    public void setBindSQLs(List<BindSQL> bindSQLs) {
        this.bindSQLs = bindSQLs;
    }
}
