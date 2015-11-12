package org.webbuilder.sql.param.update;

import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.parser.ExecuteConditionParser;

import java.util.Map;


/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class UpdateParam extends SqlRenderConfig {

    public UpdateParam set(SetField setField, SetField... setFields) {
        include(setField, setFields);
        return this;
    }

    public UpdateParam set(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            SetField field = new SetField(entry.getKey(), entry.getValue());
            set(field);
        }
        return this;
    }


    public UpdateParam where(String conditionJson) {
        this.setConditions(ExecuteConditionParser.parseByJson(conditionJson));
        return this;
    }

    public UpdateParam where(Map<String, Object> conditionMap) {
        this.setConditions(ExecuteConditionParser.parseByMap(conditionMap));
        return this;
    }

}
