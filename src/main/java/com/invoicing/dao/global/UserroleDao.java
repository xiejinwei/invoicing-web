package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.entity.global.Userrole;

@Repository("userroleDao")
public class UserroleDao extends BaseDao<Userrole> {

	@Autowired
	private HibernateTemplate hibernateTemplate;

}
