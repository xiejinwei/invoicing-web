package com.invoicing.service.global;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicing.dao.global.BaseDao;
import com.invoicing.utils.Page;

@Service("baseService")
public class BaseService<T> {

	@Autowired
	private BaseDao<T> baseDao;

	public void save(T t) {
		baseDao.save(t);
	}

	public void delete(T t) {
		baseDao.delete(t);
	}

	public List<T> list(String hql) {
		return baseDao.list(hql);
	}

	public void update(T t) {
		baseDao.update(t);
	}

	@Transactional
	public List<T> pageList(String hql, Page p, String order, Object... objects) {
		return baseDao.pageList(hql, p, order, objects);
	}

	public T findById(String id, Class<?> clazz) {
		return baseDao.findById(id, clazz);
	}

	public T findByName(String hql, String name) {
		return baseDao.findSingleEntityByParam(hql, name);
	}

	@Transactional
	public List<T> listByParam(String hql, String order, Object... object) {
		return baseDao.listByParam(hql, order, object);
	}

	public void saveOrUpdate(T t) {
		baseDao.saveOrUpdate(t);
	}

	public List<T> findListById(String hql, String id) {
		return baseDao.findListById(hql, id);
	}

	public List<T> findListByParams(String hql, Object... obj) {
		return baseDao.findListByParams(hql, obj);
	}

	@Transactional
	public List<String> seachAutocomplete(String hql, String order,
			Object... objs) {
		return baseDao.seachAutocomplete(hql, order, objs);
	}

	@Transactional
	public int getCount(String hql, Object... objects) {
		return baseDao.getCount(hql, objects);
	}

	public T findEntityByParam(String hql, Object... objects) {
		List<T> list = baseDao.findListByParams(hql, objects);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

}
