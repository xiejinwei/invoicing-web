package com.invoicing.controller.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.invoicing.annotation.RightCode;
import com.invoicing.entity.global.Modelright;
import com.invoicing.entity.global.Modelrole;
import com.invoicing.entity.global.Role;
import com.invoicing.entity.global.Smodel;
import com.invoicing.entity.global.User;
import com.invoicing.service.global.ModelService;
import com.invoicing.service.global.ModelrightService;
import com.invoicing.service.global.ModelroleService;
import com.invoicing.service.global.RoleService;
import com.invoicing.service.global.UserService;
import com.invoicing.service.global.UserroleService;
import com.invoicing.utils.HTMLUtil;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

@Controller
@RequestMapping("/role")
public class RoleController {

	private static final Logger logger = LoggerFactory
			.getLogger(RoleController.class);

	@Autowired
	private RoleService roleService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private UserService userService;
	@Autowired
	private ModelroleService modelroleService;
	@Autowired
	private ModelrightService modelrightService;
	@Autowired
	private UserroleService userroleService;

	@RequestMapping("/list")
	@RightCode(RightCode.BLOWSE)
	public String list(
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			String name) {

		logger.info("进入角色管理");

		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
		List<Role> roles = roleService.pageList("from Role r ",p,"order by r.name", "and r.name like :name",
				(name == null ? name : "%" + name + "%"));
		model.addAttribute("roles", roles);
		model.addAttribute("name", name);
		String url = StringUtil.getRequestGetUrl(request, "name", name);
		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		return "sys/role/list";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String add(Model model, String id) {

		logger.info("进入添加角色");

		List<Role> roles = roleService.list("from Role r");

		model.addAttribute("roles", roles);

		return "sys/role/add";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String add(HttpServletRequest request,Model model, Role role) {
		logger.info("添加角色");
		if (role.getName() == null || "".equals(role.getName().trim())) {
			model.addAttribute("errmsg", "角色名称不能为空");
			return "sys/role/add";
		}
		roleService.save(role);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "redirect:list";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "update", method = RequestMethod.GET)
	public String update(Model model, String id) {
		Role role = roleService.findById(id, Role.class);
		model.addAttribute("role", role);
		List<Role> roles = roleService.list("from Role r");
		model.addAttribute("roles", roles);
		return "sys/role/update";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(HttpServletRequest request,Model model, Role role) {
		Role r = roleService.findById(role.getId(), Role.class);
		if (role.getName() == null || "".equals(role.getName().trim())) {
			model.addAttribute("errmsg", "角色名称不能为空");
			return "sys/role/update";
		}
		if (r.getName().equals(role.getName())
				&& r.getParentid().equals(role.getParentid())) {
			model.addAttribute("errmsg", "输入名称与当前角色名称一样");
			return "sys/role/update";
		}
		r.setName(role.getName());
		r.setParentid(role.getParentid());
		roleService.update(r);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "redirect:list";
	}

	@RightCode(RightCode.DELETE)
	@RequestMapping("/delete")
	public @ResponseBody
	String delete(HttpServletRequest request,String id) {
		roleService.deleteRole(id);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "success";
	}

	@RequestMapping("/getrolemodel")
	public @ResponseBody
	Map<String, Object> getRolemodel(HttpServletRequest request, String rid) {
		if (rid == null)
			return null;
		Role role = roleService.findById(rid, Role.class);
		if (role == null)
			return null;
		Map<String, Object> map = new HashMap<String, Object>();
		// 登录用户的菜单
		@SuppressWarnings("unchecked")
		List<Smodel> logmodels = (List<Smodel>) request.getSession().getAttribute("smodels");
		if(logmodels!=null){
			for(Smodel m : logmodels){
				m.setHas(false);
			}
		}
		// 取出角色已一菜单
		List<Smodel> rolemodels = new ArrayList<Smodel>();
		List<Modelrole> rolemodelroles = modelroleService.getListByRid(role
				.getId());
		if (rolemodelroles != null) {
			for (Modelrole umr : rolemodelroles) {
				List<Smodel> usermodel = modelService
						.findListByParams("from Smodel m where (select count(*) from Modelright mr where mr.mid = m.id) >0 and m.id=?",
								umr.getMid());
				if (usermodel == null || usermodel.size() == 0) {
					modelroleService.delete(umr);
				} else {
					Smodel m = usermodel.get(0);
					if (!rolemodels.contains(m)) {
						rolemodels.add(m);
					}
				}
			}
		}
		// 检查当前角色权限是否比只自大，只能分配比自己权限小的角色
		User loginuser = (User) request.getSession().getAttribute("suser");
		if(logmodels.size()==rolemodels.size() && logmodels.containsAll(rolemodels) && !loginuser.getUsername().equals("admin"))
			throw new RuntimeException("非超有管理员不能给与自己权限一样的角色分配权限");
		for (Smodel rm : logmodels) {
			if (rolemodels.contains(rm)) {
				rm.setHas(true);
			}
		}
		// 获得进行包含对比及权限对比后的结果
		try {
			List<Smodel> newmodels = this.handleModelIsHas(logmodels,rolemodels,rolemodelroles);
			map.put("has", true);
			map.put("models", newmodels);
			return map;
		} catch (Exception e) {
			map.put("has", false);
			return map;
		}
	}

	/**
	 * 检查并处理菜单及权限码
	 * 
	 * @param loginmodels
	 *            当前登录用户菜单
	 * @param rolemodels
	 *            对应需要配置的角色菜单
	 * @return
	 */
	public List<Smodel> handleModelIsHas(List<Smodel> loginmodels,List<Smodel> rolemodels,List<Modelrole> rolemodelroles) {
		if (rolemodels == null || loginmodels == null)
			return loginmodels;
		for (Smodel lm : loginmodels) {
			//登录用户菜单集合
			List<Modelright> loginmodelrights = modelrightService.findModelrightsByMid(lm.getId());
			if(loginmodelrights!=null){
				for(Modelright mr : loginmodelrights){
					if(rolemodelroles!=null){
						for(Modelrole rmr : rolemodelroles){
							if(mr.getMid().equals(mr.getMid())){
								if(roleService.checkRolemodelRight(rmr, mr.getCode()) && lm.isHas()){
									mr.setUse(true);
								}
							}
						}
					}
				}
			}
			lm.setModelrights(loginmodelrights);
		}
		return loginmodels;
	}

	//保存分配角色权限
	@RequestMapping("/addrolerights")
	public @ResponseBody String addRolerights(HttpServletRequest request,String rid,String rolerights){
		roleService.addRolemodelAndRights(rid,rolerights);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "success";
	}
}
