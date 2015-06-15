package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;

import com.invoicing.entity.global.Role;

@Service("roleDao")
public class RoleDao extends BaseDao<Role> {

	@Autowired
	private HibernateTemplate hibernateTemplate;
}
