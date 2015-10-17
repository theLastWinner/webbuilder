package org.webbuilder.utils.base.file;

/**
 * 文件读取回掉
 * 
 * @author zhouhao
 * 
 */
public interface ReadCallBack {
	/**
	 * 读取一行 回掉此方法
	 * 
	 * @param line
	 *            行内容
	 */
	public void readLine(String line);

	/**
	 * 读取错误是 回掉此方法
	 * 
	 * @param e
	 *            异常
	 */
	public void readError(Throwable e);
	
	public void readOver();
	
}
