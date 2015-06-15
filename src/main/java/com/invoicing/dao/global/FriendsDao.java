package com.invoicing.dao.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.invoicing.entity.global.Friends;

@Repository("friendsDao")
public class FriendsDao extends BaseDao<Friends> {

	@Autowired
	private HibernateTemplate hibernateTemplate;

}
