package com.invoicing.service.global;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.invoicing.dao.global.QfilesDao;
import com.invoicing.dao.global.RoleDao;
import com.invoicing.dao.global.UserDao;
import com.invoicing.dao.global.UserroleDao;
import com.invoicing.entity.global.Modelrole;
import com.invoicing.entity.global.Qfiles;
import com.invoicing.entity.global.Role;
import com.invoicing.entity.global.Smodel;
import com.invoicing.entity.global.User;
import com.invoicing.entity.global.Userrole;
import com.invoicing.utils.FileUtil;
import com.invoicing.utils.MD5Util;
import com.invoicing.utils.SendMail;
import com.invoicing.utils.StringUtil;

@Service("userService")
public class UserService extends BaseService<User> {

	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private QfilesDao qfilesDao;
	@Autowired
	private UserroleDao userroleDao;

	@Autowired
	private RoleService roleService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private UserroleService userroleService;
	@Autowired
	private ModelroleService modelroleService;
	@Autowired
	private ModelrightService modelrightService;
	
	public User getSessionuser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("suser");
	}

	@SuppressWarnings("unchecked")
	public List<Smodel> getUsermodels(HttpServletRequest request, User user) {
		return (List<Smodel>) request.getSession().getAttribute("smodels");
	}

	@SuppressWarnings("unchecked")
	public List<Modelrole> getUserModelRoles(HttpServletRequest request, User user) {
		return (List<Modelrole>) request.getSession().getAttribute("smrs");
	}
	
	@Transactional
	public void addUser(HttpServletRequest request, User user,
			MultipartFile file) {
		User olduser = userDao.findSingleEntityByParam("from User u where u.username=?", user.getUsername());
		if(olduser!=null)
			throw new RuntimeException("当前用户已存在");
		if (user.getId() == null)
			user.setId(StringUtil.uuid());
		user.setCreatetime(new Date());
		if (file != null && !file.isEmpty()) {
			String imgurl = FileUtil.upladFile(request, file);
			Qfiles files = new Qfiles();
			files.setSrcid(user.getId());
			files.setType(Qfiles.USER);
			files.setUrl(imgurl);
			files.setPath(FileUtil.getServerpath(imgurl));
			files.setFilename(FileUtil.getFileName(file));
			files.setCreatetime(new Date());
			qfilesDao.save(files);
			user.setImg(files.getPath());
		}
		String pass = MD5Util.MD5(user.getUserpass());
		user.setUserpass(pass);
		userDao.save(user);
	}

	public User findByName(String username) {
		return userDao.findEntityByParams("from User u where u.username=?",
				username);
	}

	public List<User> likeByName(String username) {
		return userDao.listByParam("from User u where u.isdelete=0 and u.username like ?",
				" order by u.username ", "%" + username + "%");
	}

	public List<User> list() {
		return userDao.list("from User u where u.username !='admin' and u.isdelete=0");
	}

	@Transactional
	public void updateUser(HttpServletRequest request, User user,
			MultipartFile file) {
		User u = userDao.findById(user.getId(), User.class);
		if (u == null)
			throw new RuntimeException("没有找到当前用户信息");
		if (file != null && !file.isEmpty()) {
			
			List<Qfiles> oldfiles = qfilesDao.findListById("from Qfiles q where q.srcid=?", u.getId());
			if(oldfiles!=null){
				for(Qfiles f : oldfiles){
					FileUtil.deleteDirectory(f.getPath());
					qfilesDao.delete(f);
				}
			}
			//保存新头像
			String imgurl = FileUtil.upladFile(request, file);
			Qfiles files = new Qfiles();
			files.setSrcid(user.getId());
			files.setType(Qfiles.USER);
			files.setUrl(imgurl);
			files.setPath(FileUtil.getServerpath(imgurl));
			files.setFilename(FileUtil.getFileName(file));
			files.setCreatetime(new Date());
			qfilesDao.save(files);
			u.setImg(files.getPath());
		}
		String newpass = user.getUserpass();
		String pass = MD5Util.MD5(user.getUserpass());
		boolean passchang = false;
		if (!u.getUserpass().equals(pass)) {
			u.setUserpass(pass);
			passchang = true;
		}
		u.setPhone(user.getPhone());
		u.setEmail(user.getEmail());
		u.setFullname(user.getFullname());
		userDao.update(u);
		// 发送电邮
		if (passchang && user.getEmail() != null) {
			SendMail.sendMessage(null, null, null, user.getEmail(), "用户密码重置",
					"亲，偶们已经帮你重置了登录密码，你的新密码是：'" + newpass
							+ "';有问题可以尽管提，奴家来者不拒哟~~", null);
		}
	}

	@Transactional
	public String deleteUser(String id) {
		User user = userDao.findById(id, User.class);
		if (user == null)
			return "未找到当前用户信息";
		// 找到用户角色关系
		List<Userrole> userroles = userroleDao.findListById(
				"from Userrole ur where ur.uid=?", user.getId());
		if (userroles != null) {
			for (Userrole ur : userroles) {
				userroleDao.delete(ur);
			}
		}
		user.setIsdelete(1);
		userDao.update(user);
		return "success";
	}

	@Transactional
	public String addUserrole(String uid, String[] rids) {
		User user = userDao.findById(uid, User.class);
		if (user == null)
			throw new RuntimeException("没有查到要配置的用户信息，请刷新后再试");
		List<Userrole> urs = userroleDao.findListById(
				"from Userrole ur where ur.uid=?", user.getId());
		if (urs != null) {
			for (Userrole ur : urs) {
				userroleDao.delete(ur);
			}
		}
		for (String rid : rids) {
			Role role = roleDao.findById(rid, Role.class);
			if (role == null)
				throw new RuntimeException("没有查到分配角色信息，请刷新后再式");
			Userrole ur = new Userrole();
			ur.setUid(user.getId());
			ur.setRid(role.getId());
			userroleDao.save(ur);
		}
		return "success";
	}

	public void addRolesAndModels(HttpServletRequest request, User user) {
		// 取得用户的角色信息
		if (user == null)
			user = (User) request.getSession().getAttribute("suser");
		List<Role> roles = new ArrayList<Role>();
		List<Smodel> models = new ArrayList<Smodel>();
		List<Modelrole> modelroles = new ArrayList<Modelrole>();
		if (user.getUsername().equals("admin")) {
			// 超级管理员
			roles = roleService.list("from Role r order by r.name");
			// 只查有权限的from Smodel m where (select count(*) from Modelright mr
			// where mr.mid = m.id) >0
			models = modelService.list("from Smodel m order by m.name");
		} else {
			// 其他用户
			List<Userrole> userroles = userroleService.findByUserId(user
					.getId());
			if (userroles != null) {
				for (Userrole ur : userroles) {
					Role role = roleService.findById(ur.getRid(), Role.class);
					if (role != null && !roles.contains(role))
						roles.add(role);
				}
			}
			// 菜单级菜单角色表
			for (Role r : roles) {
				List<Modelrole> mrs = modelroleService.getListByRid(r.getId());
				if (mrs != null) {
					for (Modelrole mr : mrs) {
						// 将角色菜单表去重后保存
						if (!modelroles.contains(mr)) {
							modelroles.add(mr);
						}
						Smodel m = modelService.findById(mr.getMid(),
								Smodel.class);
						if (m != null && !models.contains(m)) {
							m.setModelrights(modelrightService
									.findModelrightsByMid(m.getId()));
							models.add(m);
							if (m.getParentid() != null
									&& !"".equals(m.getParentid())) {
								List<Smodel> pms = getParentModel(m
										.getParentid());
								for (Smodel pm : pms) {
									if (!models.contains(pm))
										models.add(pm);
								}
							}
						}
					}
				}
			}
		}
		request.getSession().setAttribute("sroles", roles);
		request.getSession().setAttribute("smodels", models);
		request.getSession().setAttribute("smrs", modelroles);
	}

	public List<Smodel> getParentModel(String pid) {
		List<Smodel> pms = new ArrayList<Smodel>();
		Smodel model = modelService.findById(pid, Smodel.class);
		if (model.getParentid() != null && !"".equals(model.getParentid()))
			pms.addAll(getParentModel(model.getParentid()));
		pms.add(model);
		return pms;
	}

}
