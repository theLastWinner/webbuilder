package org.webbuilder.sql.param;

import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.utils.base.StringUtil;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * sql执行条件
 * Created by 浩 on 2015-11-06 0006.
 */
public class ExecuteCondition {
    //条件类型
    //or || and
    private String appendType = "and";

    //是否为sql语句，而不采用预编译
    private boolean sql = false;

    //表
    private String table;

    //字段
    private String field;
    //查询类型，如GT,LT,LIKE，IN等等
    private QueryType queryType = QueryType.EQ;

    //值
    private Object value;

    //表的元信息
    private TableMetaData tableMetaData;

    private int hashCode = 0;


    /**
     * 组合查询条件
     */
    private Set<ExecuteCondition> nest = new LinkedHashSet<>();

    public ExecuteCondition() {
    }

    public ExecuteCondition(String field) {
        setField(field);
    }

    public ExecuteCondition(String field, Object value) {
        setField(field);
        setValue(value);
    }

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

    public ExecuteCondition setQueryType(QueryType queryType) {
        this.queryType = queryType;
        return this;
    }

    public ExecuteCondition setQueryType(String queryType) {
        try {
            setQueryType(QueryType.valueOf(queryType));
        } catch (Exception e) {
        }
        return this;
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
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        if (hashCode == 0)
            hashCode = toString().hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getAppendType()).append(" ").append(getField()).append(" ").append(queryType);
        builder.append(" ->").append(nest);
        return builder.toString();
    }

    public ExecuteCondition sql(String sql) {
        setSql(true);
        setValue(sql);
        return this;
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


