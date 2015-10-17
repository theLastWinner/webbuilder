package org.webbuilder.utils.db.def;


import org.webbuilder.utils.base.ClassUtil;

/**
 * 属性对象构建器
 * Created by 浩 on 2015-07-08 0008.
 */
public abstract class AttrCreator<T> {
    private String name;

    /**
     * 获取构建对象类型
     */

    public Class<T> getType() {
        return (Class<T>) ClassUtil.getGenericType(this.getClass());
    }

    /**
     * 填充对象属性值
     *
     * @param value    值
     * @param instance 对象实例
     */
    public abstract T value(Object value, Object instance);


    @Override
    public String toString() {
        return "ObjectCreator[" + getType() + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
