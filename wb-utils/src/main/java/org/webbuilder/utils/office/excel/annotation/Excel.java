package org.webbuilder.utils.office.excel.annotation;


import java.lang.annotation.*;

/**
 * 导入导出Excel注解，属性通过此注解来与excel表头进行映射
 * 
 * @author ZhouHao
 * 
 */
@Target( { ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Excel {

	/**
	 * 表头名称
	 * 
	 * @return
	 */
	String headerName() default "";

	/**
	 * 字段名称，保留
	 * 
	 * @return 
	 */
	String filedName() default "";

	/**
	 * 自定义类型转换规则,如 姓名=name,年龄=age
	 * 
	 * @return
	 */
	String customRole() default "";

	/**
	 * 导出索引，用于排序
	 * 
	 * @return
	 */
	int index() default Integer.MAX_VALUE;

	/**
	 * 集合转换，表头对应的对象字段
	 * 
	 * @return
	 */
	String KEY_HEADER() default "";

	/**
	 * 集合转换，数据对应的对象字段
	 * 
	 * @return
	 */
	String KEY_DATA() default "";

	/**
	 * 集合转换，数据对应的索引字段
	 * 
	 * @return
	 */
	String KEY_INDEX() default "";

}
