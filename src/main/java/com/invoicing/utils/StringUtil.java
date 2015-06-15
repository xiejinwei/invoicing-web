package com.invoicing.utils;

import java.text.NumberFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

public class StringUtil {

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 检查字符是不是为空（包括空字符）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null || "".equals(str.trim()) || str.length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 将金额增加逗号分隔符
	 * 
	 * @param d
	 * @return
	 */
	public static String amountAddcomma(double d) {
		NumberFormat nf = NumberFormat.getInstance();
		return nf.format(d);
	}

	/**
	 * 取得带参服务器调用地址 传入参数为name,value键值对的多组或0组数据
	 * 
	 * @Example : getRequestGetUrl(request, "username",username) 或
	 *          getRequestGetUrl(request,
	 *          "username",username,"userpass",userpass);
	 * @param req
	 * @param obj
	 * @return
	 */
	public static String getRequestGetUrl(HttpServletRequest req, Object... obj) {
		if (obj == null || obj.length == 0)
			return "";
		if (obj.length % 2 != 0)
			throw new RuntimeException("传入参数长度不正确");
		// String url = "http://" + req.getServerName() // 服务器地址
		// + ":" + req.getServerPort() // 端口号
		// + req.getContextPath() // 项目名称
		// + req.getServletPath();
		String url = req.getServletPath();
		// 每次执行取两个数字，所以
		for (int i = 0; i < obj.length; i += 2) {
			if (obj[i + 1] != null) {
				if (url.indexOf("?") == -1) {
					url += "?";
				}
				if (i != obj.length - 2) {
					url += obj[i] + "=" + obj[i + 1] + "&";
				} else {
					url += obj[i] + "=" + obj[i + 1];
				}
			}
		}
		return url;
	}
}
