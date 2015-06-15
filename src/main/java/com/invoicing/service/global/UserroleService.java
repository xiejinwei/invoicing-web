package com.invoicing.service.global;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicing.dao.global.UserroleDao;
import com.invoicing.entity.global.Userrole;

@Service("userroleService")
public class UserroleService extends BaseService<Userrole> {
	
	@Autowired
	private UserroleDao userroleDao;

	public List<Userrole> findByUserId(String uid) {
		String hql = "from Userrole r where r.uid=?";
		return userroleDao.findListById(hql, uid);
	}

	public List<Userrole> findByRoleId(String rid) {
		String hql = "from Userrole r where r.rid=?";
		return userroleDao.findListById(hql, rid);
	}

}
