package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.entity.global.Qfiles;

@Repository("qfilesDao")
public class QfilesDao extends BaseDao<Qfiles> {

	@Autowired
	private HibernateTemplate hibernateTimplate;
	
}
