package org.webbuilder.sql;

import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.trigger.Trigger;
import org.webbuilder.sql.trigger.TriggerResult;
import org.webbuilder.utils.base.StringUtil;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class TableMetaData implements Serializable {

    private String name;

    private String alias;

    private String comment;

    private String location;

    private Map<String, FieldMetaData> fieldMetaDatas = new LinkedHashMap<>();

    private Map<String, Correlation> correlations = new LinkedHashMap<>();

    private Map<String, Trigger> triggerBase = new ConcurrentHashMap<>();

    public TableMetaData on(Trigger trigger) {
        triggerBase.put(trigger.getName(), trigger);
        return this;
    }

    public boolean triggerSupport(String name) {
        return triggerBase.containsKey(name);
    }

    public TriggerResult on(String name) throws TriggerException {
        return this.on(name, new HashMap<String, Object>());
    }

    public TriggerResult on(String name, Map<String, Object> root) throws TriggerException {
        Trigger trigger = triggerBase.get(name);
        if (trigger == null) {
            throw new TriggerException(String.format("trigger %s not found!", name));
        }
        return trigger.execute(root);
    }

    private DataBaseMetaData dataBaseMetaData;

    public TableMetaData getCorrelationTable(String name) {
        return dataBaseMetaData.getTableMetaData(name);
    }

    public FieldMetaData addField(FieldMetaData fieldMetaData) {
        fieldMetaDatas.put(fieldMetaData.getName(), fieldMetaData);
        return fieldMetaData;
    }

    public DataBaseMetaData getDataBaseMetaData() {
        return dataBaseMetaData;
    }

    public SqlTemplate getTemplate(SqlTemplate.TYPE type) {
        return getDataBaseMetaData().getTemplate(type, this);
    }

    public void setDataBaseMetaData(DataBaseMetaData dataBaseMetaData) {
        this.dataBaseMetaData = dataBaseMetaData;
    }

    public void addCorrelation(Correlation correlation) {
        correlations.put(correlation.getTargetTable(), correlation);
    }

    public boolean hasCorrelation(String table) {
        return correlations.containsKey(table);
    }

    public Correlation getCorrelation(String table) {
        return correlations.get(table);
    }

    @Override
    public String toString() {
        return new StringBuilder(getDataBaseMetaData().getName()).append(".").append(getName()).toString();
    }

    public boolean hasField(String field) {
        boolean has = fieldMetaDatas.containsKey(field);
        if (has) return true;
        if (field.contains(".")) {
            String[] info = field.split("[.]");
            String t_name = info[0];
            //本表
            if (this.getName().equals(t_name)) return this.hasField(info[1]);
            //外表
            if (!this.hasCorrelation(info[0])) return false;
            TableMetaData target = dataBaseMetaData.getTableMetaData(info[0]);
            if (target == null) return false;
            return target.hasField(info[1]);
        }
        return false;
    }

    public FieldMetaData getField(String name) {
        if (!hasField(name)) return null;
        if (name.contains(".")) {
            String[] tmp = name.split("[.]");
            return dataBaseMetaData.getTable(tmp[0]).getField(tmp[1]);
        } else {
            return fieldMetaDatas.get(name);
        }

    }

    public Set<FieldMetaData> getFields() {
        return new LinkedHashSet<>(fieldMetaDatas.values());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        if (alias == null)
            alias = getName();
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static class Correlation implements Serializable {
        private String targetTable;

        private boolean one2one = true;

        private MOD mod = MOD.LEFT_JOIN;

        private Set<ExecuteCondition> condition = new LinkedHashSet<>();

        public Correlation addCondition(ExecuteCondition condition, ExecuteCondition... conditions) {
            this.condition.add(condition);
            this.condition.addAll(Arrays.asList(conditions));
            return this;
        }

        public boolean isOne2one() {
            return one2one;
        }

        public void setOne2one(boolean one2one) {
            this.one2one = one2one;
        }

        public String getTargetTable() {
            return targetTable;
        }

        public void setTargetTable(String targetTable) {
            this.targetTable = targetTable;
        }

        public Set<ExecuteCondition> getCondition() {
            return condition;
        }

        public void setCondition(Set<ExecuteCondition> condition) {
            this.condition = condition;
        }

        public MOD getMod() {
            return mod;
        }

        public void setMod(MOD mod) {
            this.mod = mod;
        }

        public enum MOD {
            LEFT_JOIN, RIGHT_JOIN, INNER_JOIN;

            public String toSql() {
                return this.toString().replace("_", " ").toLowerCase();
            }
        }
    }
}
