package com.invoicing.service.global;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicing.annotation.RightCode;
import com.invoicing.dao.global.ModelDao;
import com.invoicing.dao.global.ModelrightDao;
import com.invoicing.dao.global.ModelroleDao;
import com.invoicing.entity.global.Modelright;
import com.invoicing.entity.global.Modelrole;
import com.invoicing.entity.global.Smodel;

@Service("modelService")
public class ModelService extends BaseService<Smodel> {

	@Autowired
	private ModelDao modelDao;
	@Autowired
	private ModelroleDao modelroleDao;
	@Autowired
	private ModelrightDao modelrightDao;
	@Autowired
	private ModelrightService modelrightService;

	@Transactional
	public void deleteModel(String id) {
		Smodel model = modelDao.findById(id, Smodel.class);
		// 找出菜单角色关系信息及所有子孙菜单
		List<Smodel> allmodels = getChildmodels(model);
		if (allmodels != null) {
			for (Smodel m : allmodels) {
				List<Modelrole> modelroles = modelroleDao.findListById(
						"from Modelrole mr where mr.mid=?", m.getId());
				if (modelroles != null) {
					for (Modelrole mr : modelroles) {
						modelroleDao.delete(mr);
					}
				}
				modelDao.delete(m);
			}
		}
		modelDao.delete(model);
	}

	public List<Smodel> getChildmodels(Smodel m) {
		List<Smodel> childs = modelDao.findListById("from Smodel m where m.parentid=?",
				m.getId());
		if (childs != null) {
			for (Smodel cm : childs) {
				childs.addAll(getChildmodels(cm));
			}
		}
		return childs;
	}

	@Transactional
	public void addModel(Smodel m) {
		m.setCreatetime(new Date());
		if (m.getController() != null && "".equals(m.getController()))
			m.setController(null);
		else
			m.setController(m.getController().trim());
		if (m.getParentid() == null || "".equals(m.getParentid().trim()))
			m.setParentid(null);
		else
			m.setParentid(m.getParentid().trim());
		modelDao.save(m);
	}

	@Transactional
	public void addRight(String mid, String[] rights) {
		Smodel m = modelDao.findById(mid, Smodel.class);
		if (m == null)
			throw new RuntimeException("没有找到当前菜单信息，请刷新后重试");
		if (m.getController() == null || "".equals(m.getController().trim()))
			throw new RuntimeException("当前菜单不能添加权限信息");
		// 分配权限之前先删除之前的权限
		List<Modelright> modelrights = modelrightDao.findListById(
				"from Modelright mr where mr.mid=?", m.getId());
		if (modelrights != null) {
			for (Modelright mr : modelrights) {
				modelrightDao.delete(mr);
			}
		}
		for (String r : rights) {
			String[] rd = r.split("_");
			Modelright mr = new Modelright();
			mr.setMid(m.getId());
			mr.setCode(Long.parseLong(rd[0]));
			mr.setName(rd[1]);
			modelrightDao.save(mr);
		}
	}

	@Transactional
	public void updateModel(Smodel m) {
		Smodel model = modelDao.findById(m.getId(), Smodel.class);
		if (model == null)
			throw new RuntimeException("没有找到菜单信息");
		if (m.getController() != null && "".equals(m.getController()))
			model.setController(null);
		else
			model.setController(m.getController().trim());
		if (m.getParentid() != null && "".equals(m.getParentid().trim()))
			model.setParentid(null);
		else
			model.setParentid(m.getParentid().trim());
		if (m.getUrl() == null || "".equals(m.getUrl().trim()))
			model.setUrl(null);
		else
			model.setUrl(m.getUrl().trim());
		model.setIshide(m.getIshide());
		model.setName(m.getName());
		model.setSort(m.getSort());
		modelDao.update(model);
	}

	// 取得权限码集合
	public List<Map<String, Object>> getRightsMap(List<Modelright> modelrights) {
		List<Map<String, Object>> mrs = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 11; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			switch (i) {
			case 0:
				map.put("val", RightCode.BLOWSE);
				map.put("name", "查看");
				if (modelrightService.isModuleRight(RightCode.BLOWSE,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 1:
				map.put("val", RightCode.NEW);
				map.put("name", "新增");
				if (modelrightService.isModuleRight(RightCode.NEW, modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 3:
				map.put("val", RightCode.UPDATE);
				map.put("name", "修改");
				if (modelrightService.isModuleRight(RightCode.UPDATE,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 4:
				map.put("val", RightCode.DELETE);
				map.put("name", "删除");
				if (modelrightService.isModuleRight(RightCode.DELETE,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 5:
				map.put("val", RightCode.AUDIT);
				map.put("name", "审核");
				if (modelrightService.isModuleRight(RightCode.AUDIT,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 6:
				map.put("val", RightCode.UNAUDIT);
				map.put("name", "取消审核");
				if (modelrightService.isModuleRight(RightCode.UNAUDIT,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 7:
				map.put("val", RightCode.CONFIRM);
				map.put("name", "确认");
				if (modelrightService.isModuleRight(RightCode.CONFIRM,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 8:
				map.put("val", RightCode.UNCONFIRM);
				map.put("name", "取消确认");
				if (modelrightService.isModuleRight(RightCode.UNCONFIRM,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 9:
				map.put("val", RightCode.CLOSE);
				map.put("name", "关闭");
				if (modelrightService.isModuleRight(RightCode.CLOSE,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			case 10:
				map.put("val", RightCode.UNCLOSE);
				map.put("name", "打开");
				if (modelrightService.isModuleRight(RightCode.UNCLOSE,
						modelrights)) {
					map.put("isuse", true);
				} else {
					map.put("isuse", false);
				}
				mrs.add(map);
				break;
			default:
				break;
			}
		}
		return mrs;
	}

	// //检查当前菜单所拥有的权限并拼接成HTML代码返回
	// @SuppressWarnings("unchecked")
	// public List<Modelright> checkModelrightsAndReturn(HttpServletRequest
	// request,String controller){
	// List<Modelrole> modelroles = (List<Modelrole>)
	// request.getSession().getAttribute("smrs");
	// List<Smodel> models = (List<Smodel>)
	// request.getSession().getAttribute("smodels");
	// Smodel model = new Smodel();
	// for(Smodel m : models){
	// if(m.getController()!=null && m.getController().equals(controller)){
	// model=m;
	// break;
	// }
	// }
	// List<Modelright> modelrights = model.getModelrights();
	// List<Map<String, Object>> checkedrights = getRightsMap(modelrights);
	// return null;
	// }

}
