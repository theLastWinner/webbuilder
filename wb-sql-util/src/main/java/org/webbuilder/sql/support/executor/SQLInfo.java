package org.webbuilder.sql.support.executor;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class SQLInfo {
    private String sql;

    private Object[] param;

    private String paramString;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParam() {
        return param;
    }

    public void setParam(Object[] param) {
        this.param = param;
    }

    public String paramsString() {
        if (getParam() == null)
            return "";
        if (paramString == null) {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (Object param : getParam()) {
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


}
