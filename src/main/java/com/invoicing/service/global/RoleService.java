package com.invoicing.service.global;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicing.dao.global.ModelDao;
import com.invoicing.dao.global.ModelrightDao;
import com.invoicing.dao.global.ModelroleDao;
import com.invoicing.dao.global.RoleDao;
import com.invoicing.dao.global.UserroleDao;
import com.invoicing.entity.global.Modelrole;
import com.invoicing.entity.global.Role;
import com.invoicing.entity.global.Smodel;
import com.invoicing.entity.global.Userrole;

@Service("roleService")
public class RoleService extends BaseService<Role> {

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private ModelDao modelDao;
	@Autowired
	private UserroleDao userroleDao;
	@Autowired
	private ModelroleDao modelroleDao;
	@Autowired
	private ModelrightDao modelrightDao;

	@Transactional
	public void deleteRole(String id) {
		Role role = roleDao.findById(id, Role.class);
		if(role==null)
			throw new RuntimeException("没有找到角色信息");
		//用户角色表
		List<Userrole> userroles = userroleDao.findListById("from Userrole ur where ur.rid=?", role.getId());
		if(userroles!=null){
			for(Userrole ur : userroles){
				userroleDao.delete(ur);
			}
		}
		//角色菜单表
		List<Modelrole> modelroles = modelroleDao.findListById("from Modelrole mr where mr.rid=?", role.getId());
		if(modelroles!=null){
			for(Modelrole mr : modelroles){
				modelroleDao.delete(mr);
			}
		}
		roleDao.delete(role);
	}

	@Transactional
	public void addRolemodelAndRights(String rid, String rolerights) {
		Role role = roleDao.findById(rid, Role.class);
		if(role==null)
			throw new RuntimeException("分配角色权限失败");
		//先清除之前配置的权限
		List<Modelrole> modelroles = modelroleDao.findListById("from Modelrole mr where mr.rid=?", role.getId());
		if(modelroles!=null){
			for(Modelrole mr : modelroles){
				modelroleDao.delete(mr);
			}
		}
		String[] modelrights = rolerights.split(";");
		for(String mrs : modelrights){
			String[] modelright = mrs.split(":");
			if(modelright.length<=1)
				continue;
			String mid = modelright[0];
			String rights = modelright[1];
			Smodel model= modelDao.findById(mid, Smodel.class);
			if(model==null)
				throw new RuntimeException("没有找到菜单信息");
			String[] right = rights.split("-");
			long rolerightval = 0;
			for(String r : right){
				long rval = Long.parseLong(r);
				rolerightval += rval;
			}
			Modelrole mr = new Modelrole();
			mr.setMid(mid);
			mr.setRid(rid);
			mr.setCode(rolerightval);
			modelroleDao.save(mr);
		}
	}

	// 验证是否已包含权限
	public boolean checkRolemodelRight(Modelrole mr, long rightcode) {
		if ((rightcode & mr.getCode()) == rightcode)
			return true;
		else {
			return false;
		}
	}
}
