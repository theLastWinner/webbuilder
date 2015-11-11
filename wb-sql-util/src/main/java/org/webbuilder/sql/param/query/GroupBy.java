package org.webbuilder.sql.param.query;

import org.webbuilder.sql.param.IncludeField;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class GroupBy extends IncludeField {
    public GroupBy() {
    }

    public GroupBy(String field) {
        super(field);
    }

    public GroupBy(String field, String as) {
        super(field, as);
    }

    public GroupBy(String field, String as, String method) {
        super(field, as, method);
    }
}
