package org.webbuilder.sql.param.delete;

import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.parser.ExecuteConditionParser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于执行删除的参数
 * Created by 浩 on 2015-11-12 0012.
 */
public class DeleteParam extends SqlRenderConfig {

    public DeleteParam() {
    }

    public DeleteParam(SqlRenderConfig sqlRenderConfig) {
        super(sqlRenderConfig);
    }

    /**
     * 设置删除的条件(json 格式)，如: {"area_id$IN":[1,2,3]}
     *
     * @param conditionJson json格式的删除条件
     * @return 参数对象
     */
    public DeleteParam where(String conditionJson) {
        this.getConditions().addAll(ExecuteConditionParser.parseByJson(conditionJson));
        return this;
    }

    /**
     * 设置删除的条件(map 格式)，如: {area_id$IN=[1,2,3]}
     *
     * @param conditionMap map格式的删除条件
     * @return 参数对象
     */
    public DeleteParam where(Map<String, Object> conditionMap) {
        this.getConditions().addAll(ExecuteConditionParser.parseByMap(conditionMap));
        return this;
    }

    /**
     * 设置删除的单个条件
     *
     * @param key   键
     * @param value 值
     * @return 参数对象
     */
    public DeleteParam where(String key, Object value) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put(key, value);
        where(hashMap);
        return this;
    }

    /**
     * 跳过执行触发器
     *
     * @return 参数对象
     */
    public DeleteParam skipTrigger() {
        this.addProperty("skipTrigger", true);
        return this;
    }
}
