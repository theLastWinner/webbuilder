package org.webbuilder.sql.param.query;

import org.webbuilder.sql.param.IncludeField;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class OrderBy extends IncludeField {
    public OrderBy() {
    }

    public OrderBy(String field) {
        super(field);
    }

    public OrderBy(String field, String as) {
        super(field, as);
    }

    public OrderBy(String field, String as, String method) {
        super(field, as, method);
    }
}
