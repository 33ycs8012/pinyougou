package com.pinyougou.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 * @author DJN
 *
 */
public class PageResult implements Serializable{

	//总纪录数
	private long total;
	
	//当前页的数据
	private List rows;

	public PageResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PageResult(long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
	
}
