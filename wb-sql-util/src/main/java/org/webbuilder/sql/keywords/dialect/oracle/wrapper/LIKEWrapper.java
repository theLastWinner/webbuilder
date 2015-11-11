package org.webbuilder.sql.keywords.dialect.oracle.wrapper;

import org.webbuilder.sql.param.ExecuteCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class LIKEWrapper extends EQWrapper {
    public LIKEWrapper(boolean not) {
        super(not);
        if (not) type = ExecuteCondition.QueryType.NOTLIKE;
        else type = ExecuteCondition.QueryType.LIKE;
    }

    @Override
    public ExecuteCondition.QueryType getType() {
        return type;
    }

    @Override
    public String template(ExecuteCondition condition) {
        StringBuilder builder = new StringBuilder();
        if (condition.isSql()) {
            builder.append(String.format("INSTR(%s,%s)%s0", getFiledName(condition), String.valueOf(condition.getValue()), not ? "<=" : ">"));
            return builder.toString();
        }
        builder.append(String.format("INSTR(%s,#{%s})%s0", condition.getFullField(), getFiledName(condition), not ? "<=" : ">"));
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
