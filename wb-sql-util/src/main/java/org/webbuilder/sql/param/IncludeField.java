package org.webbuilder.sql.param;

import org.webbuilder.utils.base.StringUtil;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-11-07 0007.
 */
public class IncludeField implements Serializable {

    private String mainTable;

    private String field;

    private String targetTable;

    private String as;

    private String method;

    private boolean anotherTable;

    private boolean skipCheck;

    public boolean isAnotherTable() {
        return anotherTable;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public boolean contains(String str) {
        return getField().contains(str);
    }

    public String[] split(String patten) {
        return getField().split(patten);
    }


    public IncludeField() {
    }

    public IncludeField(String field) {
        this(field, null, null);
    }

    public IncludeField(String field, String as) {
        this(field, as, null);
    }

    public IncludeField(String field, String as, String method) {
        this.as = as;
        this.method = method;
        setField(field);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        if (field.contains(".")) {
            anotherTable = true;
            String[] info = field.split("[.]");
            targetTable = info[0];
            field = info[1];
        }
        this.field = field;
    }

    public String getAs() {
        if (as == null)
            return field;
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (method != null) builder.append(getMethod()).append(" ");
        if (isAnotherTable()) {
            builder.append(getTargetTable()).append(".");
        }
        builder.append(field);
        if (as != null) builder.append(" as ").append(as);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public String getFullField() {
        if (isAnotherTable()) {
            return StringUtil.concat(targetTable, ".", field);
        } else {
            return StringUtil.concat(mainTable, ".", field);
        }
    }

    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    public boolean isSkipCheck() {
        return skipCheck;
    }

    public void setSkipCheck(boolean skipCheck) {
        this.skipCheck = skipCheck;
    }
}
