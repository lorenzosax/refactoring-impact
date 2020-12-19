package com.group.pojo.sonar;

public class Paging {

	private int pageIndex;
	private int pageSize;
	private int total;
	
	public Paging(int pageIndex, int pageSize, int total) {
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.total = total;
	}
	
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
