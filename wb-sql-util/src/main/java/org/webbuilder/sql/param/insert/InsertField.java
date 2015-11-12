package org.webbuilder.sql.param.insert;

import org.webbuilder.sql.param.IncludeField;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class InsertField extends IncludeField {
    private Object value;

    public InsertField(String field, Object value) {
        super(field);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
