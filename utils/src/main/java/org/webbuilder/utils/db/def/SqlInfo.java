package org.webbuilder.utils.db.def;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public class SqlInfo {
    private String sql;

    private Object[] params;

    private String paramString;

    //关联sql，key为关联类型，value管理的sql语句
    private Map<String, SqlInfo> bindSql = new LinkedHashMap<String, SqlInfo>();

    public Map<String, SqlInfo> getBindSql() {
        return bindSql;
    }

    public SqlInfo(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }

    public SqlInfo(String sql) {
        this.sql = sql;
    }


    //仅对查询sql有效
    public Object[] initForBind(Object object) {
        return getParams();
    }

    public String getSql() {
        return sql;
    }

    public Object[] getParams() {
        if (params == null)
            return new Object[0];
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String paramsString() {
        if (getParams() == null)
            return "";
        if (paramString == null) {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (Object param : getParams()) {
                if (i++ != 0)
                    builder.append(",");
                builder.append(String.valueOf(param));
                builder.append("(");
                builder.append(param == null ? "null" : param.getClass().getSimpleName());
                builder.append(")");
            }
            paramString = builder.toString();
        }
        return paramString;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getSql());
        builder.append("\n");
        builder.append(paramsString());
        for (SqlInfo sqlInfo : bindSql.values()) {
            builder.append("\t").append(sqlInfo);
        }
        return builder.toString();
    }

    public void setParamString(String paramString) {
        this.paramString = paramString;
    }

    @Override
    public int hashCode() {
        StringBuilder builder = new StringBuilder(getSql());
        for (Object param : getParams()) {
            builder.append(param);
        }
        return builder.toString().hashCode();
    }
}
