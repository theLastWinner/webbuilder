package org.webbuilder.sql.param;

import org.webbuilder.utils.base.StringUtil;

import java.io.Serializable;

/**
 * 关联字段，在select，update，insert时，用于指定进行操作的列。
 * Created by 浩 on 2015-11-07 0007.
 */
public class IncludeField implements Serializable {

    /**
     * 主表
     */
    private String mainTable;

    /**
     * 字段
     */
    private String field;

    /**
     * 目标表
     */
    private String targetTable;

    /**
     * 别名：select时有效
     */
    private String as;

    /**
     * 调用函数
     */
    private String method;

    /**
     * 是否为其他的表
     */
    private boolean anotherTable;

    /**
     * 不进行有效性检测
     */
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

    public IncludeField as(String as) {
        this.as = as;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public IncludeField setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (method != null) builder.append(getMethod()).append(" ");
        if (isAnotherTable()) {
            builder.append(getTargetTable()).append(".");
        }
        builder.append(getFullField());
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
            if (mainTable == null)
                return field;
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
