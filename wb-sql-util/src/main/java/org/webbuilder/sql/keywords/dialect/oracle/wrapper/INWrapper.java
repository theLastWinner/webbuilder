package org.webbuilder.sql.keywords.dialect.oracle.wrapper;

import org.webbuilder.sql.param.ExecuteCondition;

import java.util.*;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class INWrapper extends EQWrapper {

    public INWrapper(boolean not) {
        super(not);
    }

    @Override
    public ExecuteCondition.QueryType getType() {
        return not ? ExecuteCondition.QueryType.NOTIN : ExecuteCondition.QueryType.IN;
    }

    @Override
    public String template(ExecuteCondition condition) {
        StringBuilder builder = new StringBuilder(condition.getFullField());
        builder.append(not ? " not in" : " in");
        if (condition.isSql()) {
            builder.append("(").append(String.valueOf(condition.getValue())).append(")");
            return builder.toString();
        }
        builder.append("(");
        //根据value 进行拼接
        boolean isFirst = true;
        Map<String, Object> param = this.value(condition);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (!isFirst)
                builder.append(",");
            builder.append(String.format("#{%s}", entry.getKey()));
            isFirst = false;
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public Map<String, Object> value(ExecuteCondition condition) {
        Map<String, Object> param = new LinkedHashMap<>();
        Object val = condition.getValue();
        Object[] in;
        if (val instanceof Collection) {
            in = ((Collection) val).toArray();
        } else if (val.getClass().isArray()) {
            in = (Object[]) val;
        } else if (val instanceof String) {
            in = String.valueOf(val).split(",");
        } else {
            in = new Object[0];
        }
        int index = 0;
        for (Object o : in) {
            param.put(String.format("%s_index%d", getFiledName(condition), index++), o);
        }
        return param;
    }

}
