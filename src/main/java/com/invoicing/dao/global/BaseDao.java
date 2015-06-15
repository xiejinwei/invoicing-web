package com.invoicing.dao.global;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.utils.Page;
import com.invoicing.utils.SQLUtil;

@Repository("baseDao")
public class BaseDao<T> {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	
	public int getTotalCount(String hql) {
		return hibernateTemplate.find(hql).size();
	}

	
	public void save(T t) {
		hibernateTemplate.save(t);
	}

	
	public void update(T t) {
		hibernateTemplate.update(t);
	}

	
	public void saveOrUpdate(T t) {
		hibernateTemplate.saveOrUpdate(t);
	}

	
	public void delete(T t) {
		hibernateTemplate.delete(t);
	}

	
	@SuppressWarnings("unchecked")
	public List<T> list(String hql) {
		return  (List<T>) hibernateTemplate.find(hql);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T findById(String id, Class clazz) {
		if (id == null || "".equals(id.trim()))
			return null;
		if (clazz == null)
			throw new RuntimeException("查询主体不能为空");
		String hql = "from " + clazz.getSimpleName() + " t where t.id=? ";
		List<T> list = (List<T>) hibernateTemplate.find(hql, id);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	
	public T lockByid(String id, Class clazz) {
		if (id == null || "".equals(id.trim()))
			return null;
		if (clazz == null)
			throw new RuntimeException("查询主体不能为空");
		String hql = "from " + clazz.getSimpleName()
				+ " t where t.id=? for update";
		List<T> list = (List<T>) hibernateTemplate.find(hql, id);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	
	@SuppressWarnings("unchecked")
	public List<T> listByParam(String hql, String order, Object... object) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		hql = SQLUtil.packageParams(hql, order, object);
		Query query = session.createQuery(hql);
		query = SQLUtil.setParams(query, object);
		return query.list();
	}

	
	@SuppressWarnings("unchecked")
	public T findSingleEntityByParam(String hql, String name) {
		List<T> list = (List<T>) hibernateTemplate.find(hql, name);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	
	@SuppressWarnings("unchecked")
	public List<T> findListById(String hql, String id) {
		return (List<T>) hibernateTemplate.find(hql, id);
	}

	@SuppressWarnings("unchecked")
	
	public List<T> findSQL(String sql, String order, Object... obj) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		sql = SQLUtil.packageParams(sql, order, obj);
		Query query = session.createSQLQuery(sql);
		query = SQLUtil.setParams(query, obj);
		return query.list();
	}

	
	@SuppressWarnings("unchecked")
	public List<T> pageSQLList(String sql, Page p, String order, Object... obj) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		sql = SQLUtil.packageParams(sql, order, obj);
		Query query = session.createSQLQuery(sql);
		query = SQLUtil.setParams(query, obj);
		p.setTotalCount(query.list().size());
		List<T> list = query
				.setFirstResult((p.getPage() - 1) * p.getPageSize())
				.setMaxResults(p.getPageSize()).list();
		return list;
	}

	
	@SuppressWarnings("unchecked")
	public List<T> pageList(String hql, Page p, String order, Object... objects) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		hql = SQLUtil.packageParams(hql, order, objects);
		Query query = session.createQuery(hql);
		query = SQLUtil.setParams(query, objects);
		p.setTotalCount(query.list().size());
		List<T> list = query
				.setFirstResult((p.getPage() - 1) * p.getPageSize())
				.setMaxResults(p.getPageSize()).list();
		return list;
	}

	
	@SuppressWarnings("unchecked")
	public List<T> findListByParams(String hql, Object... obj) {
		return (List<T>) hibernateTemplate.find(hql, obj);
	}

	
	@SuppressWarnings("unchecked")
	public T findEntityByParams(String hql, Object... obj) {
		List<T> list = (List<T>) hibernateTemplate.find(hql, obj);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	
	@SuppressWarnings("unchecked")
	public List<String> seachAutocomplete(String hql, String order,
			Object... objs) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		hql = SQLUtil.packageParams(hql, order, objs);
		Query query = session.createQuery(hql);
		query = SQLUtil.setParams(query, objs);
		return query.list();
	}

	
	public int getCount(String hql, Object... objects) {
		return hibernateTemplate.find(hql,objects).size();
	}

	
	public void saveAll(List<T> t) {
		for (T a : t) {
			hibernateTemplate.save(a);
		}
	}
	
	
	public int updateByParam (String hql, Object... objects){
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		Query query = session.createQuery(hql);
		if (objects != null && objects.length > 0) {
            for (int i = 0; i < objects.length; i++) {
                query.setParameter(i, objects[i]);
            }
        }
		return query.executeUpdate();
	}
	
	
	public void deleteByParam(String hql, Object... objects) {
		Session session = hibernateTemplate.getSessionFactory()
				.getCurrentSession();
		Query query = session.createQuery(hql);
		if (objects != null && objects.length > 0) {
            for (int i = 0; i < objects.length; i++) {
                query.setParameter(i, objects[i]);
            }
        }
		query.executeUpdate();
	}
	
}
