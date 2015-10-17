package org.webbuilder.utils.office.excel.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WriteExcelConfig {
	private String[] filter;

	private List<Merge> merges = new LinkedList<Merge>();

	private Map<String, ChangeValue> changeValueMap = new HashMap<String, ChangeValue>();

	// 合并相同列
	private List<String> mergeColumns = new LinkedList<String>();

	public List<String> getMergeColumns() {
		return mergeColumns;
	}

	public void setMergeColumns(List<String> mergeColumns) {
		this.mergeColumns = mergeColumns;
	}

	public List<Merge> getMerges() {
		return merges;
	}

	public void setMerges(List<Merge> merges) {
		this.merges = merges;
	}

	/**
	 * 添加单元格合并
	 * 
	 * @param rowFrom
	 * @param colFrom
	 * @param rowTo
	 * @param colTo
	 */
	public void addMerge(int rowFrom, int colFrom, int rowTo, int colTo) {
		addMerge(new Merge(rowFrom, colFrom, rowTo, colTo));
	}

	public void addMerge(Merge merge) {
		if (!merges.contains(merge))
			merges.add(merge);
	}

	public void changeValue(ChangeValue changeValue) {
		changeValueMap.put(changeValue.getField(), changeValue);
	}

	public void changeValue(String field, Object before, Object after) {
		ChangeValue changeValue = changeValueMap.get(field);
		if (changeValue == null) {
			changeValue(new ChangeValue(field));
			changeValue(field, before, after);
		} else {
			changeValue.changeValue(before, after);
		}
	}

	public ChangeValue getChangeValue(String field) {
		return changeValueMap.get(field);
	}

	public Map<String, ChangeValue> getChangeValueMap() {
		return changeValueMap;
	}

	public void setChangeValueMap(Map<String, ChangeValue> changeValueMap) {
		this.changeValueMap = changeValueMap;
	}

	public String[] getFilter() {
		return filter;
	}

	public void setFilter(String... filter) {
		this.filter = filter;
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

	public class Merge {
		private int rowFrom;

		private int colFrom;

		private int rowTo;

		private int colTo;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Merge) {
				Merge m = (Merge) obj;
				return m.getColFrom() == this.getColFrom() && m.getColTo() == this.getColTo() && m.getRowFrom() == this.getRowFrom() && m.getRowTo() == this.getRowTo();
			}
			return super.equals(obj);
		}

		public Merge(int rowFrom, int colFrom, int rowTo, int colTo) {
			this.rowFrom = rowFrom;
			this.colFrom = colFrom;
			this.rowTo = rowTo;
			this.colTo = colTo;
		}

		public int getRowFrom() {
			return rowFrom;
		}

		public void setRowFrom(int rowFrom) {
			this.rowFrom = rowFrom;
		}

		public int getColFrom() {
			return colFrom;
		}

		public void setColFrom(int colFrom) {
			this.colFrom = colFrom;
		}

		public int getRowTo() {
			return rowTo;
		}

		public void setRowTo(int rowTo) {
			this.rowTo = rowTo;
		}

		public int getColTo() {
			return colTo;
		}

		public void setColTo(int colTo) {
			this.colTo = colTo;
		}

	}
}
