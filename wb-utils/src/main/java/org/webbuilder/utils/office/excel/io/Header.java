package org.webbuilder.utils.office.excel.io;


/**
 * 表头对象
 * 
 * @author ZhouHao
 * 
 */
public class Header implements Comparable<Header> {

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 数据类型
	 */
	private Class<?> dataType;

	/**
	 * 属性名
	 */
	private String filed;

	private int index;

	public Header(String title, String filed, Class<?> dataType) {
		this.title = title;
		this.filed = filed;
		this.dataType = dataType;
	}
	public Header(String title, String filed, Class<?> dataType,int index) {
		this.title = title;
		this.filed = filed;
		this.dataType = dataType;
		this.index = index;
	}
	
	public Header(String title, String filed) {
		this.title = title;
		this.filed = filed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Class<?> getDataType() {
		return dataType;
	}

	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	public String getFiled() {
		return filed;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("title=" + title);
		builder.append(",filed=" + filed);
		builder.append(",dataType=" + dataType);
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		Header header = (Header) obj;
		if (header.getTitle().equals(getTitle()) && header.getFiled().equals(getFiled())) {
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(Header o) {
		return this.getIndex() > o.getIndex() ? 1 : 0;
	}


}
