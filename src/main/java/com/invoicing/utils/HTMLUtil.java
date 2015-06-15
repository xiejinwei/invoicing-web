package com.invoicing.utils;


public class HTMLUtil {

	/**
	 * 封装分页HTML代码
	 * @param page 
	 * @param url 通过StringUtil.getRequestGetUrl()方式取得的url地址
	 * @return
	 */
	public static String getNumberPageHTML(Page page, String url) {
		if (url.indexOf("?") == -1) {
			url += "?";
		}
		int len = url.substring(url.indexOf("?") + 1, url.length()).length();
		if ( len> 0) {
			url += "&page=";
		} else {
			url += "page=";
		}
		String html = "<div class=‘row‘><div class=‘text-right‘><nav><ul class='pagination'>";
		// 首页
		html += "<li><a href='" + url + "1"+"&pageSize="+page.getPageSize() + "'>&laquo;</a></li>";

		// 总页数大于5
		if (page.getPageCount() > 5) {
			// 总页数大于5
			for (int i = 0; i < 5; i++) {
				switch (i) {
				case 0:
					html += "<li><a href='" + (page.getPage()-2)+"&pageSize="+page.getPageSize() + "'>" + (page.getPage() - 2)
							+ "</a></li>";
					break;
				case 1:
					html += "<li><a href='" + (page.getPage()-1)+"&pageSize="+page.getPageSize() + "'>" + (page.getPage() - 1)
							+ "</a></li>";
					break;
				case 2:
					html += "<li class='active'><a>" + page.getPage() + "</a></li>";
					break;
				case 3:
					html += "<li><a href='" + (page.getPage()+1)+"&pageSize="+page.getPageSize() + "'>" + (page.getPage() + 1)
							+ "</a></li>";
					break;
				default:
					html += "<li><a href='" + (page.getPage()+2)+"&pageSize="+page.getPageSize() + "'>" + (page.getPage() + 2)
							+ "</a></li>";
					break;
				}
			}
		} else {
			// 总页数小于等于5
			for (int i = 1; i <= page.getPageCount(); i++) {
				if (i == page.getPage()) {
					html += "<li class='active'><a>"
							+ i + "</a></li>";
				} else {
					html += "<li><a href='" + url + i +"&pageSize="+page.getPageSize() + "'>" + i + "</a></li>";
				}
			}
		}
		// 尾页
		html += "<li><a href='" + url + page.getPageCount() + "&pageSize="+page.getPageSize()
				+ "'>&raquo;</a></li>";
		html += "</ul></nav></div></div>";
		return html;
	}

	public static String getPreNextPageHTML(Page page, String url) {
		return null;
	}

}
