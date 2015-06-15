package com.invoicing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限码接口，用于增加权限时传递权限码
 * 
 * @Excepor
 * 
 * @author xjw
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RightCode {

	public long value() default 0;// 默认为登录

	/**
	 * 无限制
	 */
	public static final long GUEST = -1;
	/**
	 * 登录
	 */
	public static final long LOGIN = 0;
	/**
	 * 查看
	 */
	public static final long BLOWSE = 2;
	/**
	 * 新增
	 */
	public static final long NEW = 4;
	/**
	 * 修改
	 */
	public static final long UPDATE = 8;
	/**
	 * 审核
	 */
	public static final long AUDIT = 16;
	/**
	 * 取消审核
	 */
	public static final long UNAUDIT = 32;
	/**
	 * 确认
	 */
	public static final long CONFIRM = 64;
	/**
	 * 取消确认
	 */
	public static final long UNCONFIRM = 128;
	/**
	 * 删除
	 */
	public static final long DELETE = 256;
	/**
	 * 关闭
	 */
	public static final long CLOSE = 512;
	/**
	 * 取消关闭
	 */
	public static final long UNCLOSE = 1024;
}
