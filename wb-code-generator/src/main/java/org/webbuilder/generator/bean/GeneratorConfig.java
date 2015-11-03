package org.webbuilder.generator.bean;

import org.webbuilder.utils.db.def.valid.ValidatorFactory;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 浩 on 2015-07-27 0027.
 */
public class GeneratorConfig {
    private File input;

    private File output;

    private String packageName;
    private String databaseType = "orcl";

    private String tableName;

    private String className;

    private String remark;

    private boolean autoCreate;

    private String module;

    Set<Field> fields = new LinkedHashSet<>();

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    private Map<String, String> dbConfig = null;

    public String getTableName() {
        if (tableName == null)
            tableName = getClassName().toUpperCase();
        return tableName;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public void valid() throws Exception {
        ValidatorFactory.NOT_NULL.valid("输出目录不能为空", getOutput());
        ValidatorFactory.NOT_EMPTY.valid("类名不能为空", getClassName());
        ValidatorFactory.NOT_EMPTY.valid("包名不能为空", getPackageName());
    }

    public Map<String, String> getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(Map<String, String> dbConfig) {
        this.dbConfig = dbConfig;
    }
}
