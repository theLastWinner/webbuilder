package org.webbuilder.sql.keywords;

import org.webbuilder.sql.param.ExecuteCondition;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public interface FieldTemplateWrapper {
    ExecuteCondition.QueryType getType();

    String template(ExecuteCondition condition);

    Map<String, Object> value(ExecuteCondition condition);
}
