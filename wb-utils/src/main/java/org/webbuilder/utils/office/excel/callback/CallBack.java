package org.webbuilder.utils.office.excel.callback;

import org.webbuilder.utils.office.excel.annotation.Excel;

import java.lang.reflect.Field;

/**
 * EXCEL读写回调接口
 * 
 * @author ZhouHao
 * 
 */
public interface CallBack {
	/**
	 * 读取前回调
	 * 
	 * @param index
	 *            读取的索引
	 * @return
	 */
	public Object before(int index, int max);

	/**
	 * 异常时回调
	 * 
	 * @param header
	 *            表头
	 * @param e
	 *            异常实体
	 * @return
	 */
	public Object exception(String header, Throwable e);

	/**
	 * 读取成功时回调
	 * 
	 * @param header
	 *            表头
	 * @param val
	 *            读取的值
	 * @return
	 */
	public Object success(String header, Object val);

	/**
	 * 值为null
	 * 
	 * @param header
	 *            表头
	 * @param type
	 *            类型
	 * @return 返回值
	 */
	public Object valueIsNull(String header, Class<?> type);

	/**
	 * 类型为自定义类型
	 * 
	 * @param header
	 *            当写出表头回调时，传入值为  fieldName.fieldName...,如 goodsType.name
	 * @param val
	 *            当写出表头回调时， 值为字段试题，类型为{@link Field}<br>
	 *            当写出数据单元格回调时，值为对象属性的值
	 * @param excel
	 * @return 返回值
	 */
	public Object typeIsCustom(String header, Object val, Excel excel);
	
}
