package org.webbuilder.utils.office.excel.io;

import org.webbuilder.utils.office.excel.callback.ObjcetCreater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel读取配置，配置自定义对象创建和未绑定字段读取回调
 * 
 * @author ZhouHao
 * 
 */
public class ReadExcelConfig<T> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, ChangeValue> changeValueMap = new HashMap<String, ChangeValue>();

	/**
	 * 对象创建回调集合
	 */
	private List<ObjcetCreater<?>> creaters;

	public ReadExcelConfig(ObjcetCreater<?>... creaters) {
		this.creaters = new ArrayList<ObjcetCreater<?>>();
		for (ObjcetCreater<?> creater : creaters) {
			this.creaters.add(creater);
		}
	}

	/**
	 * 当excel中的表头未找到对象属性匹配时，回调此方法
	 * 
	 * @param header
	 *            字段名称
	 * @param val
	 *            字段值
	 * @param nowObj
	 *            当前行对象
	 */
	public void headerNotFound(String header, Object val, T nowObj) {
		logger.error("excel header(" + header + ") not found in objcet(" + nowObj.getClass() + "),value is " + val);
	}

	public final ObjcetCreater<?>[] getCreaters() {
		return creaters.toArray(new ObjcetCreater<?>[] {});
	}

	public final void setCreaters(List<ObjcetCreater<?>> creaters) {
		this.creaters = creaters;
	}

	public final void addCreaters(ObjcetCreater<?>... creaters) {
		for (ObjcetCreater<?> creater : creaters) {
			this.creaters.add(creater);
		}
	}

	public final void changeValue(ChangeValue changeValue) {
		changeValueMap.put(changeValue.getField(), changeValue);
	}

	public final void changeValue(String field, Object before, Object after) {
		ChangeValue changeValue = changeValueMap.get(field);
		if (changeValue == null) {
			changeValue(new ChangeValue(field));
			changeValue(field, before, after);
		} else {
			changeValue.changeValue(before, after);
		}
	}

	public final ChangeValue getChangeValue(String field) {
		return changeValueMap.get(field);
	}

	public final Map<String, ChangeValue> getChangeValueMap() {
		return changeValueMap;
	}

	public final void setChangeValueMap(Map<String, ChangeValue> changeValueMap) {
		this.changeValueMap = changeValueMap;
	}

	public class ChangeValue {
		private String field;

		private Map<Object, Object> map = new HashMap<Object, Object>();

		public void changeValue(Object key, Object value) {
			map.put(key, value);
		}

		public ChangeValue(String field) {
			this.field = field;
		}

		public String getField() {
			return field;
		}

		public Object getValue(Object key) {
			return map.get(key);
		}

		public void setField(String field) {
			this.field = field;
		}

		public Map<Object, Object> getMap() {
			return map;
		}

		public void setMap(Map<Object, Object> map) {
			this.map = map;
		}

	}
}
