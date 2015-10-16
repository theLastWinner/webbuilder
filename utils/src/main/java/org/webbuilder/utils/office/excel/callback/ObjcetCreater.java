package org.webbuilder.utils.office.excel.callback;

import org.webbuilder.utils.base.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjcetCreater<T> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Class<T> type;

	public ObjcetCreater(Class<T> type) {
		this.type = type;
	}

	/**
	 * excel表格的值转为对象属性 当对象属性为自定义类型时，ExcelIO 会调用此方法来获取该自定义属性的值，有n个表头指向自定义对象，就会被调用n次 <br>
	 * 默认通过反射填充
	 * 
	 * @param attrName
	 *            传入该自定义对象的属性名(通过注解获取)
	 * @param val
	 *            传入该属性的值
	 * @param nowObj
	 *            此属性自定义类型的实体(如果为null，会被反射创建)
	 * @return 赋值填充后的对象（可能几个表头都会指向同一个自定义类型属性，返回的为依次填充后的对象）
	 */
	public T excelValue2Object(String attrName, Object val, T nowObj) {
		if (nowObj == null || attrName == null)
			return null;
		Field attribute;
		try {
			if (nowObj == null)
				nowObj = type.newInstance();
			attribute = nowObj.getClass().getDeclaredField(attrName);
			Method method = nowObj.getClass().getMethod(ClassUtil.getGetOrSetMethodByAttribute("set", attrName), attribute.getType());
			method.invoke(nowObj, ClassUtil.String2Obj(String.valueOf(val), attribute.getType()));
		} catch (Exception e) {
			logger.error("create object(" + type.getClass() + ") error attrName=" + attrName + ",value=" + val, e);
		}
		return nowObj;
	};

	/**
	 * 对象创建泛型转换
	 * 
	 * @param attrName
	 * @param val
	 * @param nowObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T execute(String attrName, Object val, Object nowObj) {
		return excelValue2Object(attrName, val, (T) nowObj);
	}

	/**
	 * 当对象创建并填充完成时调用此方法进行其他的处理,如填充其他数据，进行数据库操作等
	 * 
	 * @param val
	 */
	public void objectCreateSuccess(T val) {
	};

	public Class<T> getType() {
		return type;
	}

}
