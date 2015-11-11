package org.webbuilder.sql.param;

import java.util.Map;
import java.util.Set;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class WrapperCondition {

    private Set<String> tables;

    private String template;

    private Map<String, Object> params;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Set<String> getTables() {
        return tables;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }
}
