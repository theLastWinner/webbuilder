package org.webbuilder.utils.office.excel.io;


import org.webbuilder.utils.office.excel.callback.CallBack;

public class ExcelIOConfig {
	private int[] sheets = { 0 };

	/**
	 * 导出属性过滤，为空时，全部注解导出
	 */
	private String[] writeFilter = {};

	/**
	 * 行回调，主要在导入时使用
	 */
	private CallBack rowCallBack;

	/**
	 * 单元格回调
	 */
	private CallBack cellCallBack;

	/**
	 * 日期格式，已弃用
	 */
	@Deprecated
	private String dateTimeFormater;

	public String[] getWriteFilter() {
		if(writeFilter==null)
			writeFilter = new String[]{};
		return writeFilter;
	}

	public void setWriteFilter(String[] writeFilter) {
		this.writeFilter = writeFilter;
	}

	public ExcelIOConfig() {
	}

	public ExcelIOConfig(CallBack rowCallBack, CallBack cellCallBack) {
		this.rowCallBack = rowCallBack;
		this.cellCallBack = cellCallBack;
	}

	public int[] getSheets() {
		return sheets;
	}

	public void setSheets(int[] sheets) {
		this.sheets = sheets;
	}

	public CallBack getRowCallBack() {
		return rowCallBack;
	}

	public void setRowCallBack(CallBack rowCallBack) {
		this.rowCallBack = rowCallBack;
	}

	public CallBack getCellCallBack() {
		return cellCallBack;
	}

	public void setCellCallBack(CallBack cellCallBack) {
		this.cellCallBack = cellCallBack;
	}

	@Deprecated
	public String getDateTimeFormater() {
		return dateTimeFormater;
	}

	@Deprecated
	public void setDateTimeFormater(String dateTimeFormater) {
		this.dateTimeFormater = dateTimeFormater;
	}

	
}
