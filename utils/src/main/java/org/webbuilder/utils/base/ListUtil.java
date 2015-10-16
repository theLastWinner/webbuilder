package org.webbuilder.utils.base;

import java.util.*;

public class ListUtil {

	/**
	 * 在对象集合中找到对象
	 * 
	 * @param ObjectList
	 *            对象集合
	 * @param object
	 *            要找的对象
	 * @return 找到的对象
	 */
	public static <T> T findObjectInList(List<T> ObjectList, T object) {
		for (T t : ObjectList) {
			if (t.equals(object)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * 根据元素对象的属性对list元素进行排序
	 * 
	 * @param <T>
	 * @param list
	 *            需要排序的list
	 * @param by
	 *            根据元素对象的属性 比如 id
	 * @param desc
	 *            是否倒序
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> sort(List<T> list, String by, boolean desc) throws Exception {
		if (list == null)
			return null;
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				T a = list.get(i);
				T b = list.get(j);
				if (compare(StringUtil.isNumber(a) ? StringUtil.toDouble(a) : ClassUtil.getValueByAttribute(by, a), StringUtil.isNumber(a) ? StringUtil.toDouble(a) : ClassUtil.getValueByAttribute(by, b), desc)) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
		return list;
	}
	
	public static boolean compare(Object a, Object b, boolean desc) {
		if (a instanceof Integer && b instanceof Integer) {
			return desc ? (Integer) a < (Integer) b : (Integer) a > (Integer) b;
		} else if (a instanceof Double && b instanceof Double) {
			return desc ? (Double) a < (Double) b : (Double) a > (Double) b;
		} else if (a instanceof String) {
			if (StringUtil.isNumber(a)) {
				return compare(Double.parseDouble(a.toString()), Double.parseDouble(b.toString()), desc);
			}
		} else {
			return desc ? a.hashCode() > b.hashCode() : a.hashCode() < b.hashCode();
		}
		return false;
	}

	public static <T> List<T> sort(List<T> list, String by) throws Exception {
		return sort(list, by, false);
	}

	public static <T> List<T> removeRepeat(List<T> list) throws Exception {
		Map<Integer, T> map = new HashMap<Integer, T>();
		if(list==null)
			return null;
		for (T t : list) {
			if(t==null)
				continue;
			map.put(t.hashCode(), t);
		}
		try {
			list.clear();
			list.addAll(map.values());
		} catch (Exception e) {
		}
		return new ArrayList<T>(map.values());
	}

	public static String toString(Object... obj) {
		return toString(Arrays.asList(obj));
	}

	public static <T> List<T> fromArray(T[] objs) {
		List<T> list = new ArrayList<T>();
		for (T t : objs) {
			list.add(t);
		}
		return list;
	}

	public static String toString(List<Object> objects) {
		StringBuffer buffer = new StringBuffer();
		int index = 0;
		for (Object object : objects) {
			if (index++ != 0)
				buffer.append(",");
			buffer.append(object);
		}
		return buffer.toString() + ",";
	}

	public static Integer[] stringArr2intArr(String[] arr) {
		Integer[] i = new Integer[arr.length];
		int index = 0;
		for (String str : arr) {
			if (StringUtil.isInt(str)) {
				i[index++] = Integer.parseInt(str);
			}
		}
		return i;
	}
}
