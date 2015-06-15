package com.invoicing.service.global;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicing.dao.global.ModelroleDao;
import com.invoicing.entity.global.Modelrole;

@Service("modelroleService")
public class ModelroleService extends BaseService<Modelrole> {

	@Autowired
	private ModelroleDao modelroleDao;
	
	public List<Modelrole> getListByRid(String rid) {
		String hql = "from Modelrole r where r.rid=?";
		return modelroleDao.findListById(hql, rid);
	}

	public List<Modelrole> getListByMid(String mid) {
		String hql = "from Modelrole r where r.mid=?";
		return modelroleDao.findListById(hql, mid);
	}

}
