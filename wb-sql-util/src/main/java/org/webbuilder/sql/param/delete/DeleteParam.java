package org.webbuilder.sql.param.delete;

import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.parser.ExecuteConditionParser;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class DeleteParam extends SqlRenderConfig {
    public DeleteParam where(String conditionJson) {
        this.setConditions(ExecuteConditionParser.parseByJson(conditionJson));
        return this;
    }

    public DeleteParam where(Map<String, Object> conditionMap) {
        this.setConditions(ExecuteConditionParser.parseByMap(conditionMap));
        return this;
    }

}
