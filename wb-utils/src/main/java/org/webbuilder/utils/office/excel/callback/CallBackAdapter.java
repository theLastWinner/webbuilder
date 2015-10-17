package org.webbuilder.utils.office.excel.callback;


import org.webbuilder.utils.office.excel.annotation.Excel;

import java.io.Serializable;


public class CallBackAdapter implements CallBack ,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6993014529514960180L;

	@Override
	public Object before(int index,int max) {
		return null;
	}

	@Override
	public Object exception(String header, Throwable e) {
		return null;
	}

	@Override
	public Object success(String header, Object val) {
		return val;
	}


	@Override
	public Object valueIsNull(String header, Class<?> type) {
		return null;
	}

	@Override
	public Object typeIsCustom(String header, Object val, Excel excel) {
		// TODO Auto-generated method stub
		return null;
	}

}
