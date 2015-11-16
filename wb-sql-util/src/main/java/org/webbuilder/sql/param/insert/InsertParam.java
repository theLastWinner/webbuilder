package org.webbuilder.sql.param.insert;

import org.webbuilder.sql.param.SqlRenderConfig;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class InsertParam extends SqlRenderConfig {

    private Map<String, Object> data;

    public InsertParam value(String field, Object value) {
        this.include(new InsertField(field, value));
        return this;
    }

    public InsertParam values(Map<String, Object> data) {
        this.data = data;
        this.getIncludes().clear();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            value(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public InsertParam skipTrigger() {
        this.addProperty("skipTrigger", true);
        return this;
    }
}
