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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.invoicing.annotation.RightCode;
import com.invoicing.entity.global.Role;
import com.invoicing.entity.global.Smodel;
import com.invoicing.entity.global.User;
import com.invoicing.entity.global.Userrole;
import com.invoicing.service.global.ModelService;
import com.invoicing.service.global.ModelrightService;
import com.invoicing.service.global.ModelroleService;
import com.invoicing.service.global.RoleService;
import com.invoicing.service.global.UserService;
import com.invoicing.service.global.UserroleService;
import com.invoicing.utils.HTMLUtil;
import com.invoicing.utils.MD5Util;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserController.class);

	private static final String REDIRECT_USER = "redirect:/user/list";

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserroleService userroleService;
	@Autowired
	private ModelroleService modelroleService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private ModelrightService modelrightService;

	// 用户登录
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request,
			@RequestParam("username") String username,
			@RequestParam("userpass") String userpass, Model model) {
		logger.info("user login,username:" + username);
		if (username == null || "".equals(username.trim())) {
			model.addAttribute("nameerror", "用户名不能为空");
			return "home";
		}
		if (userpass == null || "".equals(userpass.trim())) {
			model.addAttribute("passerror", "密码不能为空");
			return "home";
		}
		User user = userService.findByName(username);
		if (user == null) {
			model.addAttribute("nameerror", "当前用户不存在");
			return "home";
		}
		String pass = MD5Util.MD5(userpass);
		if (!user.getUserpass().equals(pass)) {
			model.addAttribute("passerror", "用户密码输入错误");
			return "home";
		}
		if (user.getIsdelete() != 0) {
			model.addAttribute("errmsg", "当前用户已删除，如需再次使用，请联系系统管理员");
			return "home";
		}
		// 用户验证完成,将用户信息保存到session
		request.getSession().setAttribute("suser", user);
		userService.addRolesAndModels(request, user);
		if (user.getUsername().equals("admin")) {
			return "redirect:/user/list";
		} else {
			@SuppressWarnings("unchecked")
			List<Smodel> models = ((List<Smodel>) request.getSession()
					.getAttribute("smodels"));
			String path = "";
			for (Smodel m : models) {
				if (m.getController() != null) {
					path = m.getUrl();
					break;
				}
			}
			return "redirect:" + path;
		}
	}

	// 安全退出
	@RequestMapping("/logout")
	@RightCode(RightCode.GUEST)
	public String logout(HttpServletRequest request) {
		// 删除用户
		request.getSession().removeAttribute("suser");
		// 删除角色
		request.getSession().removeAttribute("sroles");
		// 删除菜单
		request.getSession().removeAttribute("smodels");
		// 删除角色菜单
		request.getSession().removeAttribute("smrs");
		return "redirect:/";
	}

	/**
	 * 用户管理
	 * 
	 * @param request
	 * @param model
	 * @param page
	 * @param pageSize
	 * @param username
	 * @return
	 */
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/list")
	public String list(
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(value = "delete", defaultValue = "0") int delete,
			String username) {

		logger.info("进入用户管理页");

		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
		List<User> users = userService.pageList("from User u", p,
				"order by u.username", "and u.username like :username",
				(username == null ? username : "%" + username + "%"),
				" and u.username != :admin", "admin",
				" and u.isdelete=:delete", delete);
		String url = StringUtil.getRequestGetUrl(request, "username", username);
		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		model.addAttribute("users", users);
		model.addAttribute("username", username);
		model.addAttribute("delete", delete);
		return "sys/user/list";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {

		logger.info("进入添加用户页");

		return "sys/user/add";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView add(HttpServletRequest request, User user,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		logger.info("添加用户");
		ModelAndView mv = new ModelAndView();
		// 对数据进行验证
		if (user.getUsername() == null || "".equals(user.getUsername().trim())) {
			mv.addObject("errmsg", "用户名不能为空");
			mv.setViewName("sys/user/add");
			return mv;
		}
		if (user.getUserpass() == null || "".equals(user.getUserpass().trim())) {
			mv.addObject("errmsg", "初始密码不能为空");
			mv.setViewName("sys/user/add");
			return mv;
		}
		String unam = user.getUsername().toLowerCase();
		if (unam.equals("admin")) {
			mv.addObject("errmsg", "用户名：admin是超级管理员，不能添加为普通用户");
			mv.setViewName("sys/user/add");
			return mv;
		}
		User u = userService.findByName(user.getUsername());
		if (u != null) {
			mv.addObject("errmsg", "当前用户已存在，请重新输入一个用户名");
			mv.setViewName("sys/user/add");
			return mv;
		}
		userService.addUser(request, user, file);
		mv.setViewName("redirect:/user/list");
		return mv;
	}

	@RightCode(RightCode.LOGIN)
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String update(Model model, String id) {
		logger.info("进入用户修改");
		User user = userService.findById(id, User.class);
		if (user == null)
			throw new RuntimeException("当前用户不存在");
		model.addAttribute("user", user);
		return "sys/user/update";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(HttpServletRequest request, User user, Model model,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		logger.info("用户修改");
		if (user.getUserpass() == null || "".equals(user.getUserpass().trim())) {
			model.addAttribute("errmsg", "初始密码不能为空");
			return "sys/user/update";
		}
		userService.updateUser(request, user, file);
		return REDIRECT_USER;
	}

	@RightCode(RightCode.LOGIN)
	@RequestMapping(value = "/userupdate", method = RequestMethod.GET)
	public String userupdate(Model model, String id) {
		logger.info("进入用户修改");
		User user = userService.findById(id,User.class);
		if (user == null)
			throw new RuntimeException("当前用户不存在");
		model.addAttribute("user", user);
		return "sys/user/userupdate";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "/userupdate", method = RequestMethod.POST)
	public String userupdate(HttpServletRequest request, User user,
			Model model,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		logger.info("用户修改");
		if (user.getUserpass() == null || "".equals(user.getUserpass().trim())) {
			model.addAttribute("errmsg", "初始密码不能为空");
			return "sys/user/userupdate";
		}
		userService.updateUser(request, user, file);

		@SuppressWarnings("unchecked")
		List<Smodel> models = ((List<Smodel>) request.getSession()
				.getAttribute("smodels"));
		String path = "";
		for (Smodel m : models) {
			if (m.getController() != null) {
				path = m.getUrl();
				break;
			}
		}
		return "redirect:" + path;
	}

	@RightCode(RightCode.DELETE)
	@RequestMapping("/delete")
	public @ResponseBody String delete(String id) {
		return userService.deleteUser(id);
	}

	// 查出登录用户所拥有的角色集合，并与需要配置的用户角色进行比较，如果比需要
	@RequestMapping(value = "/getuserrole", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> getUserroles(
			HttpServletRequest request, String uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) request.getSession().getAttribute("suser");
		List<Role> roles = new ArrayList<Role>();
		List<Role> uroles = new ArrayList<Role>();
		if (user == null)
			return null;
		if (user.getUsername().equals("admin")) {
			// 超级管理员
			roles = roleService.list("from Role r");
		} else {
			// 普通用户
			List<Userrole> loginurs = userroleService
					.findByUserId(user.getId());
			if (loginurs != null) {
				for (Userrole ur : loginurs) {
					Role r = roleService.findById(ur.getRid(), Role.class);
					roles.add(r);
				}
			}

			// 拥有同样角色，或所需分配用户角色比登录用户角色多时，提示无权分配
			if ((roles.size() == uroles.size() && roles.containsAll(uroles))
					|| !roles.containsAll(uroles)) {
				map.put("has", false);
				return map;
			}
		}
		// 查出配置用户已有的角色
		List<Userrole> userroles = userroleService.findByUserId(uid);
		if (userroles != null) {
			for (Userrole ur : userroles) {
				Role role = roleService.findById(ur.getRid(), Role.class);
				if (role == null) {
					userroleService.delete(ur);
				} else {
					if (!uroles.contains(role)) {
						uroles.add(role);
					}
				}
			}
		}
		for (Role role : roles) {
			if (uroles.contains(role))
				role.setHas(true);
		}
		map.put("roles", roles);
		map.put("has", true);
		return map;
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping("/adduserrole")
	public @ResponseBody String addUserrole(String uid,
			@RequestParam("rids") String[] rids) {
		if (uid == null)
			return "必须要有一个需要分配的用户";
		return userService.addUserrole(uid, rids);
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping("/restart")
	public String restartUser(String id){
		User user = userService.findById(id,User.class);
		user.setIsdelete(0);
		return "redirect:list";
	}
	
}
