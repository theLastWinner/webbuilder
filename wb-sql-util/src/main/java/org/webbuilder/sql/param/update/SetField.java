package org.webbuilder.sql.param.update;

import org.webbuilder.sql.param.IncludeField;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class SetField extends IncludeField {
    private Object value;

    public SetField(String field, Object value) {
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
