package org.webbuilder.sql.param;

import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.utils.base.StringUtil;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class ExecuteCondition {
    //or || and
    private String appendType = "and";

    private boolean sql = false;

    private String table;
    //字段
    private String field;

    private QueryType queryType = QueryType.EQ;

    //string 或者 ExecuteCondition
    //
    private Object value;

    private TableMetaData tableMetaData;

    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public FieldMetaData getFieldMetaData() {
        return getTableMetaData().getField(getFullField());
    }

    public String getAppendType() {
        return appendType;
    }

    public void setAppendType(String appendType) {
        this.appendType = appendType;
    }

    /**
     * 组合查询
     */
    private Set<ExecuteCondition> nest = new LinkedHashSet<>();

    public Set<ExecuteCondition> getNest() {
        return nest;
    }

    public void setNest(Set<ExecuteCondition> nest) {
        this.nest = nest;
    }

    public ExecuteCondition addNest(ExecuteCondition nest) {
        this.nest.add(nest);
        return this;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public boolean isSql() {
        return sql;
    }

    public void setSql(boolean sql) {
        this.sql = sql;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFullField() {
        if (getTable() == null) return getField();
        return StringUtil.concat(getTable(), ".", getField());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getAppendType()).append(" ").append(getFullField()).append(" ").append(queryType);
        builder.append(" ->").append(nest);
        return builder.toString();
    }

    public enum QueryType {
        /**
         * =
         */
        EQ,
        /**
         * like '%?%'
         */
        LIKE,
        /**
         * not like '%?%'
         */
        NOTLIKE,
        /**
         * like '?%'
         */
        START,
        /**
         * like '%?'
         */
        END,
        /**
         * like '?%'
         */
        NOTSTART,
        /**
         * like '%?'
         */
        NOTEND,
        /**
         * <= ?
         */
        LT,

        /**
         * >= ?
         */
        GT,

        /**
         * in(?,?...)
         */
        IN,
        /**
         * not in(?,?...)
         */
        NOTIN,
        /**
         * !=
         */
        NOT,
        /**
         * not null
         */
        NOTNULL,

        /**
         * is null
         */
        ISNULL


    }
}


