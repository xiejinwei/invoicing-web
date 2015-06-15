package com.invoicing.service.global;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicing.dao.global.ModelrightDao;
import com.invoicing.entity.global.Modelright;

@Service("modelrightService")
public class ModelrightService extends BaseService<Modelright> {

	@Autowired
	private ModelrightDao modelrightDao;

	public List<Modelright> findModelrightsByMid(String mid) {
		String hql = "from Modelright mr where mr.mid=?";
		return modelrightDao.findListById(hql, mid);
	}

	// 对当前用户权限进行匹配
	public boolean isModuleRight(long code, List<Modelright> modelrights) {
		if (modelrights == null || modelrights.size() == 0)
			return false;
		boolean b = false;
		for (int i = 0; i < modelrights.size(); i++) {
			if (modelrights.get(i).getCode() == code) {
				b = true;
				break;
			}
		}
		return b;
	}

}
