package org.webbuilder.utils.db.exec.helper;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.db.def.DataFormater;
import org.webbuilder.utils.db.def.TableMetaData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-08 0008.
 */
public abstract class ResultHelper<T> {

    private Map<String, AttrHelper> attrHelpers = new HashMap<String, AttrHelper>();
    private Map<Class<?>, DataFormater> dataformaters = new HashMap<Class<?>, DataFormater>();

    private TableMetaData metaData;

    public TableMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(TableMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * 获取构建对象类型
     */

    public Class<T> getType() {
        return (Class<T>) ClassUtil.getGenericType(this.getClass());
    }

    /**
     * 创建对象实例
     */
    public abstract T newInstance();

    /**
     * 填充对象属性值
     *
     * @param name     属性名称
     * @param value    值
     * @param instance 对象实例
     */
    public abstract void initAttr(String name, Object value, T instance);

    public String toString() {
        return "ObjectCreator[" + getType() + "]";
    }

    public Map<String, AttrHelper> getAttrHelpers() {
        return attrHelpers;
    }

    public AttrHelper getAttrHelper(String attrName) {
        return attrHelpers.get(attrName);
    }

    /**
     * 结果格式化
     *
     * @param val 要格式化的值
     * @return 格式化的值
     */
    public Object formatData(Object val) {
        if (val == null)
            return val;
        DataFormater formater = getDataformater(val.getClass());
        if (formater != null) {
            return formater.format(val);
        }
        return val;
    }

    public DataFormater getDataformater(Class<?> type) {
        return dataformaters.get(type);
    }

    public void addDataformater(DataFormater<?> formater) {
        dataformaters.put(formater.getType(), formater);
    }

    public void onCreated(T instance) {

    }
}
