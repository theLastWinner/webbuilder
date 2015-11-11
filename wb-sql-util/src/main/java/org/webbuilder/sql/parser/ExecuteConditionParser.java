package org.webbuilder.sql.parser;

import com.alibaba.fastjson.JSON;
import org.apache.tools.ant.taskdefs.Exec;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.render.template.SqlTemplate;

import java.util.*;

/**
 * Created by 浩 on 2015-11-11 0011.
 */
public class ExecuteConditionParser {

    public static Set<ExecuteCondition> parseByJson(String json) {
        return parseByMap(JSON.parseObject(json));
    }


    public static Set<ExecuteCondition> parseByMap(Map<String, Object> condition) {
        Set<ExecuteCondition> conditions = new LinkedHashSet<>();
        if (condition == null || condition.size() == 0) return conditions;

        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            String key = entry.getKey();
            ExecuteCondition condition1 = new ExecuteCondition();
            // 类似 name$LIKE
            if (key.contains("$")) {
                String[] real = key.split("[$]");
                key = real[0];
                try {
                    ExecuteCondition.QueryType queryType = ExecuteCondition.QueryType.valueOf(real[1].toUpperCase());
                    condition1.setQueryType(queryType);
                } catch (Exception e) {
                    continue;
                }
            }
            if (key.contains(".")) {
                String[] field = key.split("[.]");
                condition1.setTable(field[0]);
                condition1.setField(field[1]);
            } else {
                condition1.setField(key);
            }
            Object val = entry.getValue();
            //关联查询
            if (val instanceof Map) {
                Map<String, Object> map = (Map) val;
                val = map.get("value");
                String type = String.valueOf(map.get("type"));
                if ("null".equals(type))
                    type = "and";
                if (!Arrays.asList("and", "or").contains(type.toLowerCase())) continue;
                condition1.setAppendType(type);
                condition1.setValue(val);
                Object nest = map.get("nest");
                if (nest instanceof Map) {
                    Set<ExecuteCondition> nests = parseByMap((Map) nest);
                    condition1.setNest(nests);
                }
            } else {
                condition1.setValue(val);
            }
            conditions.add(condition1);
        }
        return conditions;
    }

    public static void main(String[] args) {
        System.out.println(parseByJson("{\"name$LIKE\":{\"value\":\"张三\",\"nest\":{\"age$GT\":{\"type\":\"or\",\"value\":10}} }}"));
    }
}
