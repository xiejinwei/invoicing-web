package com.invoicing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//防重复提交TOKEN

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

	/**
	 * 是否检查token
	 * 
	 * @return
	 */
	public boolean token() default false;

	/**
	 * 打开或关闭on:打开off:关闭
	 * 
	 * @return
	 */
	public String value() default "off";

}
