package org.webbuilder.utils.base;

import java.util.*;

/**
 * 基于回掉的迭代器工具
 * Created by 浩 on 2015-07-05 0005.
 */
public class Loop {

    /**
     * 指定源集合进行迭代，返回迭代后的结果。迭代后的结果默认实例与源集合实例相同
     * <br/>若无法进行实例化，将返回通用的实例如下:
     * List --> ArrayList
     * Set  --> HashSet
     *
     * @param source   源集合
     * @param each     迭代实例
     * @param <TARGET> 返回结果实例
     * @param <T>      集合泛型
     * @return 迭代后的结果
     */
    public static <TARGET, T> TARGET doEach(Collection<T> source, Each<T> each) {
        Collection<T> target = getInstance(source);
        doEach(source, target, each);
        return (TARGET) target;
    }

    /**
     * 指定源集合和目标集合进行迭代，将源集合的迭代结果填充入目标集合
     *
     * @param source 源集合
     * @param target 目标集合
     * @param each   迭代器实例
     * @param <T>    集合泛型
     */
    public static <T> void doEach(Collection<T> source, Collection<T> target, Each<T> each) {
        if (source == null || source.size() == 0 || each == null) return;
        int index = 0;
        for (T el : source) {
            if (each.isExited()) break;
            if (each.exec(el, index++)) target.add(el);
        }
        each.exit();
    }

    /**
     * 根据源集合获取目标集合实例
     * <br/>若无法进行实例化，将返回通用的实例如下:
     * List --> ArrayList
     * Set  --> HashSet
     *
     * @return 目标集合
     */
    private static <T> Collection<T> getInstance(Collection<T> source) {
        Collection<T> cl;
        try {
            cl = source.getClass().newInstance();
            return cl;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (source instanceof List) {
            return new ArrayList<T>();
        } else if (source instanceof Set) {
            return new HashSet<T>();
        }
        return new ArrayList<T>();
    }


    /**
     * 迭代器抽象类
     *
     * @param <T> 迭代泛型
     */
    public abstract static class Each<T> {
        //是否已经结束迭代
        private boolean exited = false;

        /**
         * 对每一个元素调用此方法，如果此方法返回true，则将此对象填充入目标集合
         *
         * @param el    当前被迭代的元素
         * @param index 当前被迭代元素所在索引
         * @return 是否将此对象填充如目标集合
         */
        public abstract boolean exec(T el, int index);

        /**
         * 退出迭代
         */
        public void exit() {
            exited = true;
        }

        /**
         * 是否已经退出迭代
         *
         * @return 是否已经退出迭代
         */
        public boolean isExited() {
            return exited;
        }
    }

}
