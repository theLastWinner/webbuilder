package org.webbuilder.sql.keywords.dialect.oracle.wrapper;

import org.webbuilder.sql.param.ExecuteCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class NullWrapper extends EQWrapper {
    public NullWrapper(boolean not) {
        super(not);
        if (not) {
            type = ExecuteCondition.QueryType.NOTNULL;
        } else {
            type = ExecuteCondition.QueryType.ISNULL;
        }
    }

    @Override
    public String template(ExecuteCondition condition) {

        return new StringBuilder(condition.getFullField()).append(not ? " is not null" : " is null").toString();
    }

    @Override
    public Map<String, Object> value(ExecuteCondition condition) {
        return new HashMap<>();
    }
}
