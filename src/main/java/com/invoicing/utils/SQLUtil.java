package com.invoicing.utils;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Query;

public class SQLUtil {

	/**
	 * 封装查询语句条件，并返回一个String HQL
	 * 
	 * @param hql
	 *            请不要在hql句子中写where子句
	 * @param obj
	 *            查询条件，(name,value,name,value...),如果需要用什么关键词接，请写在name 里边
	 * @Example hqlparams("from User u",
	 *          "u.username=:username","xiejinwei","and u.userpass=:userpass"
	 *          ,"xjw1234","or u.email=:email","xjw@126.com")
	 * @Example hqlparams("from User u where 1=1 ",
	 *          "and u.username=:username","xiejinwei"
	 *          ,"or u.email=:email","xjw@126.com")
	 * @return
	 */
	public static String packageParams(String hql, String order, Object... objects) {
		if (objects == null || objects.length == 0){
			if(order==null || "".equals(order.trim()))
				return hql;
			else
				return hql += " "+order;
		}
		if (objects.length % 2 != 0)
			throw new RuntimeException("参数传入有错，请按(name,value...)这样的键值对进行传值");
		if (hql.indexOf("where") == -1 && hql.indexOf("WHERE") == -1) {
			hql += " where 1=1 ";
		}
		for (int i = 0; i < objects.length; i += 2) {
			if (objects[i + 1] != null && !"".equals(objects[i + 1])
					&& !"%%".equals(objects[i + 1])) {
				hql += (" " + objects[i]);
			}
		}
		if(StringUtil.isNullOrEmpty(order)){
			return hql;
		}else{
			return hql += (" " + order);
		}
	}

	// HQL查询参数绑定
	public static Query setParams(Query query, Object... objects) {
		if (objects == null || objects.length == 0)
			return query;
		if (objects.length % 2 != 0)
			throw new RuntimeException("参数传入有错，请按(name,value...)这样的键值对进行传值");
		for (int i = 0; i < objects.length; i += 2) {
			Object obj = objects[i + 1];
			if (obj != null) {
				String[] names = objects[i].toString().split(":");
				String name = names[1].trim();
				if (obj instanceof String && !"".equals(obj)
						&& !"%%".equals(obj)) {
					query.setString(name, obj.toString());
				} else if (obj instanceof Date) {
					query.setDate(name, (Date) obj);
				} else if (obj instanceof Integer) {
					query.setInteger(name, Integer.parseInt(obj.toString()));
				} else if (obj instanceof Double) {
					query.setDouble(name, Double.parseDouble(obj.toString()));
				} else if (obj.getClass().getName().indexOf("com.web.entity")!=-1) {
					query.setEntity(name, obj);
				} else if (obj instanceof Collection<?>){
					query.setParameterList(name, (Collection<?>)obj);
				} else if(obj instanceof Object[]){
					String[] anames = name.split(" ");
					query.setParameterList(anames[0], (Object[])obj);
				} 
			}
		}
		return query;
	}

}
