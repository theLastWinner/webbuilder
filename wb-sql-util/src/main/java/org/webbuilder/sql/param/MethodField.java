package org.webbuilder.sql.param;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class MethodField extends IncludeField {
    public MethodField() {
    }

    public MethodField(String method, String field) {
        setMethod(method).setField(field);
    }

    public MethodField count(String field) {
        setMethod("count").setField(field);
        return this;
    }

    public MethodField sum(String field) {
        setMethod("sum").setField(field);
        return this;
    }

    public MethodField avg(String field) {
        setMethod("avg").setField(field);
        return this;
    }

}
