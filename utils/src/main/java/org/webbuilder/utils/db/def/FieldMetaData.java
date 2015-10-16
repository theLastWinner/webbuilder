package org.webbuilder.utils.db.def;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.valid.Validator;
import org.webbuilder.utils.db.exception.DataValidException;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public class FieldMetaData<T> implements Serializable {
    //字段名称
    private String name;
    //注释
    private String remark;
    //java类型
    private Class<T> javaType;
    //长度
    private int length = 256;
    //数据类型
    private String dataType;
    //是否为主键
    private boolean primaryKey;
    //是否不为空
    private boolean notNull;
    //默认值
    private Object defaultValue;
    //别名
    private String alias;
    //是否索引
    private boolean index;
    //是否可被修改
    private boolean canUpdate = true;

    private String vtype = null;

    private Map<String, Object> attrs = new LinkedHashMap<String, Object>();

    public Object attr(String name) {
        return attrs.get(name);
    }

    public Object attr(String name, Object val) {
        return attrs.put(name, val);
    }

    //校验器集合，用于在生成insert，update时前进行校验
    private List<Validator> validators = new LinkedList<Validator>();

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNotNull() {
        if (isPrimaryKey())
            return true;
        return notNull;
    }
    public FieldMetaData(){}

    public List<Validator> getValidators() {
        return validators;
    }

    public FieldMetaData addValidator(Validator validator) {
        if (validator != null)
            validators.add(validator);
        return this;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public FieldMetaData setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getAlias() {
        if (StringUtil.isNullOrEmpty(alias))
            alias = getRemark();
        return alias;
    }

    public FieldMetaData setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    /**
     * 构造器
     *
     * @param name     字段名称
     * @param javaType 对应Java类型
     */
    public FieldMetaData(String name, Class<T> javaType) {
        this(name, javaType, null, null, 256, false, false, null, null);
    }

    public FieldMetaData(String name, Class<T> javaType, int length) {
        this(name, javaType, null, null, length, false, false, null, null);
    }

    public FieldMetaData(String name, Class<T> javaType, String dataType) {
        this(name, javaType, dataType, null, 256, false, false, null, null);
    }

    public FieldMetaData(String name, Class<T> javaType, String dataType, String remark) {
        this(name, javaType, dataType, null, 256, false, false, remark, null);
    }

    public FieldMetaData(String name, Class<T> javaType, String dataType, String remark, boolean primaryKey) {
        this(name, javaType, dataType, null, 256, primaryKey, false, remark, null);
    }

    public FieldMetaData(String name, Class<T> javaType, String dataType, boolean primaryKey) {
        this(name, javaType, dataType, null, 256, primaryKey, false, null, null);
    }

    public FieldMetaData(String name, Class<T> javaType, String dataType, String remark, boolean primaryKey, boolean notNull) {
        this(name, javaType, dataType, null, 256, primaryKey, notNull, remark, null);
    }

    public FieldMetaData(String name, Class<T> javaType, String dataType, Object defaultValue, int length, boolean primaryKey, boolean notNull, String remark, String alias) {
        this.name = name;
        this.javaType = javaType;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.length = length;
        this.primaryKey = primaryKey;
        this.notNull = notNull;
        this.remark = remark;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public Class<T> getJavaType() {
        return javaType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public FieldMetaData setName(String name) {
        this.name = name;
        return this;
    }

    public void setJavaType(Class<T> javaType) {
        this.javaType = javaType;
    }

    public String getRemark() {
        if (StringUtil.isNullOrEmpty(remark))
            alias = getName();
        return remark;
    }

    public FieldMetaData setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getDataType() {
        return dataType;
    }

    public FieldMetaData setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public boolean equalsFull(FieldMetaData metaData) {
        return metaData.getDataType().equals(this.getDataType())
                && metaData.isPrimaryKey() == this.isPrimaryKey() && metaData.isNotNull() == this.isNotNull();
    }

    @Override
    public String toString() {
        return new StringBuilder(getName()).append("(").append(getJavaType()).append(")").toString();
    }

    @Override
    public FieldMetaData clone() {
        FieldMetaData the_new = new FieldMetaData(this.name, this.javaType, this.dataType, this.defaultValue, this.length, this.primaryKey, this.notNull, this.remark, this.alias);
        the_new.getValidators().addAll(this.getValidators());
        the_new.setCanUpdate(this.isCanUpdate());
        return the_new;
    }

    public void valid(Object data) throws DataValidException {
        for (Validator validator : getValidators()) {
            validator.valid(this.getName(), data);
        }
    }

    public boolean isIndex() {
        return index;
    }

    public FieldMetaData setIndex(boolean index) {
        this.index = index;
        return this;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public String getVtype() {
        return vtype;
    }

    public void setVtype(String vtype) {
        this.vtype = vtype;
    }
}
