package org.webbuilder.sql.param.insert;

import org.webbuilder.sql.param.SqlRenderConfig;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class InsertParam extends SqlRenderConfig {
    public InsertParam insert(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            this.include(new InsertField(entry.getKey(), entry.getValue()));
        }
        return this;
    }
}
