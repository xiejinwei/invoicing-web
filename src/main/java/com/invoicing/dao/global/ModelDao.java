package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.entity.global.Smodel;

@Repository("modelDao")
public class ModelDao extends BaseDao<Smodel> {
	@Autowired
	private HibernateTemplate hibernateTemplate;
}
