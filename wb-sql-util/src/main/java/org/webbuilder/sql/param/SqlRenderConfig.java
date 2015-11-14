package org.webbuilder.sql.param;

import java.util.*;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class SqlRenderConfig {
    private Set<ExecuteCondition> conditions = new LinkedHashSet<>();

    private Map<String, Object> properties = new LinkedHashMap<>();

    private Set<IncludeField> includes = new LinkedHashSet<>();

    private Set<String> excludes = new LinkedHashSet<>();

    public void copy(SqlRenderConfig config) {
        config.setExcludes(this.getExcludes());
        config.setIncludes(this.getIncludes());
        config.setProperties(this.getProperties());
        config.setConditions(this.getConditions());
    }

    public SqlRenderConfig addCondition(ExecuteCondition condition, ExecuteCondition... conditions) {
        this.conditions.add(condition);
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public SqlRenderConfig removeProperty(String key) {
        properties.remove(key);
        return this;
    }

    public SqlRenderConfig addProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public SqlRenderConfig addProperty(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public SqlRenderConfig include(String field, String... fields) {
        includes.add(new IncludeField(field));
        for (int i = 0; i < fields.length; i++) {
            includes.add(new IncludeField(fields[i]));
        }
        return this;
    }

    public SqlRenderConfig include(IncludeField field, IncludeField... fields) {
        includes.add(field);
        includes.addAll(Arrays.asList(fields));
        return this;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Object get(String key) {
        return this.getProperty(key);
    }

    public SqlRenderConfig exclude(String field, String... fields) {
        excludes.add(field);
        excludes.addAll(Arrays.asList(fields));
        return this;
    }

    public Set<ExecuteCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<ExecuteCondition> conditions) {
        this.conditions = conditions;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public Set<IncludeField> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<IncludeField> includes) {
        this.includes = includes;
    }
}
