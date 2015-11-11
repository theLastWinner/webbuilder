package org.webbuilder.sql.keywords.dialect.oracle.wrapper;

import org.webbuilder.sql.param.ExecuteCondition;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class ThanWrapper extends EQWrapper {
    public ThanWrapper(boolean not) {
        super(not);
        if (not) type = ExecuteCondition.QueryType.GT;
        else type = ExecuteCondition.QueryType.LT;
    }

    @Override
    public ExecuteCondition.QueryType getType() {
        return type;
    }

    @Override
    public String template(ExecuteCondition condition) {
        StringBuilder builder = new StringBuilder(condition.getFullField());
        builder.append(not ? ">=" : "<=");
        if (condition.isSql()) {
            builder.append(String.format("%s", String.valueOf(condition.getValue())));
            return builder.toString();
        }

        if (condition.getFieldMetaData().getJavaType() == Date.class) {
            builder.append(String.format("to_date(#{%s},'YYYY-MM-DD HH24:MI:SS')", getFiledName(condition)));
        } else {
            builder.append(String.format("#{%s}", getFiledName(condition)));
        }
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
