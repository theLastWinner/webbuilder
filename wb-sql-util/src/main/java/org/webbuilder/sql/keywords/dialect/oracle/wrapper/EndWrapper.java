package org.webbuilder.sql.keywords.dialect.oracle.wrapper;

import org.webbuilder.sql.param.ExecuteCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class EndWrapper extends EQWrapper {
    public EndWrapper(boolean not) {
        super(not);
        if (not) type = ExecuteCondition.QueryType.NOTEND;
        else type = ExecuteCondition.QueryType.END;
    }

    @Override
    public ExecuteCondition.QueryType getType() {
        return type;
    }

    @Override
    public String template(ExecuteCondition condition) {
        StringBuilder builder = new StringBuilder(condition.getFullField());
        builder.append(not ? " not like " : " like ");
        if (condition.isSql()) {
            builder.append(String.format("'%s'||%s", "%", String.valueOf(condition.getValue())));
            return builder.toString();
        }
        builder.append(String.format("'%s'||#{%s$END}", "%", getFiledName(condition)));
        return builder.toString();
    }

    @Override
    public Map<String, Object> value(ExecuteCondition condition) {
        Map<String, Object> param = new HashMap<>();
        Object val = condition.getValue();
        param.put(getFiledName(condition), val);
        return param;
    }


}
