package com.invoicing.utils;

import java.util.ArrayList;
import java.util.List;

public class Page {

	private int totalCount = 0;// 总记录数
	private int pageCount;// 总页数
	private int pageSize = 10;// 每页显示记录数
	private int page = 1;// 当前页
	private int num = 5;// 当前页之前和之后显示的页数个数 如：假设当前页是 6 共有11页 那么 显示分页条会显示 1 2 3 4
	// 5 [6] 7 8 9 10 11
	@SuppressWarnings("rawtypes")
	private List items = new ArrayList();// 当前页记录内容集合
	@SuppressWarnings("unused")
	private int prev;// 前一页
	@SuppressWarnings("unused")
	private int next;// 后一页
	@SuppressWarnings("unused")
	private int last;// 最后一页
	@SuppressWarnings("unused")
	private List<Integer> prevPages;// 得到前num页的数据集合
	@SuppressWarnings("unused")
	private List<Integer> nextPages;// 得到后num页的数据集合

	/**
	 * 计算总页数
	 * 
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount) {
		if (totalCount > 0) {
			this.totalCount = totalCount;
			this.pageCount = (totalCount + pageSize - 1) / pageSize;
		}
	}

	/**
	 * 判断是否有前一页
	 * 
	 * @return boolean
	 */
	public boolean getIsPrev() {
		if (page > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 获取前一页
	 * 
	 * @return int
	 */
	public int getPrev() {
		if (getIsPrev()) {
			return page - 1;
		} else {
			return page;
		}
	}

	/**
	 * 判断是否有后一页
	 * 
	 * @return boolean
	 */
	public boolean getIsNext() {
		if (page < pageCount) {
			return true;
		}
		return false;
	}

	/**
	 * 获取后一页
	 * 
	 * @return int
	 */
	public int getNext() {
		if (getIsNext()) {
			return page + 1;
		}
		return getPageCount();
	}

	/**
	 * 获取最后一页
	 * 
	 * @return int
	 */
	public int getLast() {
		return pageCount;
	}

	/**
	 * 当前页的前num条页 假设当前页是 6 共有11页 如：1 2 3 4 5
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> getPrevPages() {
		List<Integer> list = new ArrayList<Integer>();
		int _frontStart = 1;
		if (page > num) {
			_frontStart = page - num;
		} else if (page <= num) {
			_frontStart = 1;
		}
		for (int i = _frontStart; i < page; i++) {
			list.add(i);
		}
		return list;
	}

	/**
	 * 当前页的后num条页 假设当前页是 6 共有11页 如：7 8 9 10 11
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> getNextPages() {
		List<Integer> list = new ArrayList<Integer>();
		int _endCount = num;
		if (num < pageCount && (page + num) < pageCount) {
			_endCount = page + _endCount;
		} else if ((page + num) >= pageCount) {
			_endCount = pageCount;
		}
		for (int i = page + 1; i <= _endCount; i++) {
			list.add(i);
		}
		return list;
	}

	/**
	 * 获取每页显示记录数
	 * 
	 * @return int
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页显示记录数
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 得到当前页数
	 * 
	 * @return int
	 */
	public int getPage() {
		return page;
	}

	/**
	 * 设置当前页数
	 * 
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * 获取当前页之前或之后显示的页数个数
	 * 
	 * @return int
	 */
	public int getNum() {
		return num;
	}

	/**
	 * 设置当前页之前或之后显示的页数个数
	 * 
	 * @param num
	 */
	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * 获取当前页记录内容集合
	 * 
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public List getItems() {
		return items;
	}

	/**
	 * 设置当前页记录内容集合
	 * 
	 * @param items
	 */
	@SuppressWarnings("rawtypes")
	public void setItems(List items) {
		this.items = items;
	}

	/**
	 * 获取总记录数
	 * 
	 * @return int
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * 得到总页数
	 * 
	 * @return int
	 */
	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setPrev(int prev) {
		this.prev = prev;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public void setPrevPages(List<Integer> prevPages) {
		this.prevPages = prevPages;
	}

	public void setNextPages(List<Integer> nextPages) {
		this.nextPages = nextPages;
	}

}
