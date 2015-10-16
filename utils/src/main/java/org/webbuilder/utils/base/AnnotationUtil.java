package org.webbuilder.utils.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解工具
 * 
 * @author ZhouHao
 * 
 */
public class AnnotationUtil {

	/**
	 * 根据字段获取注解
	 * 
	 * @param field
	 *            字段
	 * @return 对应的注解集合
	 */
	public static Annotation[] getAnnotationsByField(Field field) {
		return field.getDeclaredAnnotations();
	}

	public static Map<Field, Annotation[]> getAnnotationsMapByFields(Field[] fields) {
		Map<Field, Annotation[]> map = new HashMap<Field, Annotation[]>();
		for (Field field : fields) {
			Annotation[] annotations = getAnnotationsByField(field);
			if (annotations.length > 0) {
				map.put(field, annotations);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T[]> getAnnotationsMapByFields(Field[] fields, Class<T> annotationType) {
		Map<String, T[]> map = new HashMap<String, T[]>();
		for (Field field : fields) {
			Annotation[] annotations = getAnnotationsByField(field);
			if (annotations.length > 0) {
				List<T> list = new ArrayList<T>();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().getName().equals(annotationType.getName())) {
						list.add((T) annotation);
					}
				}
				if (list.size() > 0) {
					map.put(field.getName(), (T[]) list.toArray());
				}
			}
		}
		return map;
	}
}
