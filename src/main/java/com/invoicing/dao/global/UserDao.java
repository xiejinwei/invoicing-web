package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.entity.global.User;

@Repository("userDao")
public class UserDao extends BaseDao<User> {

	@Autowired
	private HibernateTemplate hibernateTemplate;

}
