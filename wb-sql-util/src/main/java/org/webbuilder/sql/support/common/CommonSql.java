package org.webbuilder.sql.support.common;

import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;

import java.util.*;

/**
 * Created by 浩 on 2015-11-07 0007.
 */
public class CommonSql implements SQL {
    private String sql;

    private Map<String, Object> params;

    private List<BindSQL> bindSQLs;

    private TableMetaData tableMetaData;

    public CommonSql() {
    }

    public CommonSql(String sql) {
        this.sql = sql;
    }

    public CommonSql(String sql, Map<String, Object> params) {
        this.sql = sql;
        this.params = params;
    }

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
        if (params == null)
            return new HashMap<>();
        return this.params;
    }

    @Override
    public List<BindSQL> getBinds() {
        if (bindSQLs == null)
            bindSQLs = new LinkedList<>();
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
        if (bindSQLs == null)
            bindSQLs = new ArrayList<>();
        return bindSQLs;
    }

    public void setBindSQLs(List<BindSQL> bindSQLs) {
        this.bindSQLs = bindSQLs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getSql()).append(";\n");
        for (BindSQL bindSQL : getBinds()) {
            builder.append(bindSQL.getSql());
        }
        return builder.toString();
    }
}
