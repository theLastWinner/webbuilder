package org.webbuilder.sql.keywords.dialect.oracle.wrapper;

import org.webbuilder.sql.keywords.FieldTemplateWrapper;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.utils.base.DateTimeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class EQWrapper implements FieldTemplateWrapper {

    protected boolean not;

    protected ExecuteCondition.QueryType type;

    public EQWrapper(boolean not) {
        this.not = not;
        if (not) type = ExecuteCondition.QueryType.NOT;
        else type = ExecuteCondition.QueryType.EQ;
    }

    @Override
    public ExecuteCondition.QueryType getType() {
        return type;
    }

    @Override
    public String template(ExecuteCondition condition) {
        StringBuilder builder = new StringBuilder(condition.getFullField());
        if (not) builder.append("!");
        builder.append("=");
        if(condition.isSql()){
            builder.append(String.valueOf(condition.getValue()));
            return builder.toString();
        }
        if (condition.getFieldMetaData().getJavaType() == Date.class) {
            builder.append(String.format("to_date(#{%s},'YYYY-MM-DD HH24:MI:SS')", getFiledName(condition)));
        } else {
            builder.append(String.format("#{%s}",  getFiledName(condition)));
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> value(ExecuteCondition condition) {
        Map<String, Object> param = new HashMap<>();
        Object val = condition.getValue();
        if (condition.getFieldMetaData().getJavaType() == Date.class) {
            if (val instanceof Date) {
                val = DateTimeUtils.format((Date) val, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
            }
        }
        param.put(getFiledName(condition), val);
        return param;
    }

    protected String getFiledName(ExecuteCondition condition) {
        return String.format("%s$%s$%s", condition.getAppendType(), condition.getFullField(), condition.getQueryType().toString());
    }
}
