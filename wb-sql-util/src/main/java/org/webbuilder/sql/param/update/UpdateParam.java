package org.webbuilder.sql.param.update;

import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.parser.ExecuteConditionParser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class UpdateParam extends SqlRenderConfig {

    private Map<String, Object> data = new LinkedHashMap<>();


    public UpdateParam() {

    }

    public UpdateParam(SqlRenderConfig sqlRenderConfig) {
        super(sqlRenderConfig);
    }

    public UpdateParam set(String setField, Object value) {
        set(new SetField(setField, value));
        return this;
    }

    public UpdateParam set(SetField setField, SetField... setFields) {
        data.put(setField.getField(), setField.getValue());
        for (SetField field : setFields) {
            data.put(field.getField(), field.getValue());
        }
        include(setField, setFields);
        return this;
    }

    public UpdateParam set(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public UpdateParam where(String key, Object value) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put(key, value);
        where(hashMap);
        return this;
    }

    public UpdateParam where(String conditionJson) {
        this.getConditions().addAll(ExecuteConditionParser.parseByJson(conditionJson));
        return this;
    }

    public UpdateParam where(Map<String, Object> conditionMap) {
        Set<ExecuteCondition> conditions = ExecuteConditionParser.parseByMap(conditionMap);
        this.getConditions().addAll(conditions);
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public UpdateParam skipTrigger() {
        this.addProperty("skipTrigger", true);
        return this;
    }
}
