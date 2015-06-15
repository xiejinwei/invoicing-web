package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.entity.global.Modelrole;

@Repository("menuroleDao")
public class ModelroleDao extends BaseDao<Modelrole> {

	@Autowired
	private HibernateTemplate hibernateTemplate;

}
