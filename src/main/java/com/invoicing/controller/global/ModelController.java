package com.invoicing.controller.global;

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
import com.invoicing.entity.global.Smodel;
import com.invoicing.service.global.ModelService;
import com.invoicing.service.global.ModelrightService;
import com.invoicing.service.global.UserService;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

@Controller
@RequestMapping("/model")
public class ModelController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ModelController.class);

	@Autowired
	private ModelService modelService;
	@Autowired
	private ModelrightService modelrightService;
	@Autowired
	private UserService userService;
	
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/list")
	public String list(
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			String name){
		
		logger.info("进入菜单管理");
		
		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
//		List<Smodel> models = modelService.pageList("from Smodel m ",p,"order by m.sort,m.name"," m.name like :name",(name==null?name:"%"+name+"%"));
		List<Smodel> models = modelService.listByParam("from Smodel m ","order by m.sort,m.name","and m.name like :name",(name==null?name:"%"+name+"%"));
		model.addAttribute("models", models);
		model.addAttribute("name", name);
//		String url = StringUtil.getRequestGetUrl(request, "name",name);
//		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		return "sys/model/list";
	}
	
	@RightCode(RightCode.NEW)
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(Model model,String mid){
		List<Smodel> models = modelService.list("from Smodel m ");
		if(models!=null && mid!=null && !"".equals(mid.trim())){
			for(Smodel m : models){
				if(m.getId().equals(mid)){
					m.setHas(true);
					break;
				}
			}
		}
		model.addAttribute("models", models);
		return "sys/model/add";
	}
	
	@RightCode(RightCode.NEW)
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public String add(HttpServletRequest request,Model model,Smodel m,int hide){
		if(StringUtil.isNullOrEmpty(m.getName())){
			model.addAttribute("errmsg", "菜单名称不能为空");
		}
		if(StringUtil.isNullOrEmpty(m.getController())){
			model.addAttribute("errmsg", "菜单控制器地址不能为空");
		}
		if(StringUtil.isNullOrEmpty(m.getUrl())){
			model.addAttribute("errmsg", "菜单名称不能为空");
		}
		m.setIshide(hide);
		modelService.addModel(m);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "redirect:list";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping(value="/update",method=RequestMethod.GET)
	public String update(Model model,String id,String pid){
		Smodel m = modelService.findById(id, Smodel.class);
		if(m==null)
			throw new RuntimeException("没有找到菜单信息");
		String hql = "from Smodel m";
		List<Smodel> models = modelService.list(hql);
		model.addAttribute("pmodels", models);
 		model.addAttribute("model", m);
		return "sys/model/update";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public String update(HttpServletRequest request,Model model,Smodel m){
		modelService.updateModel(m);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "redirect:list";
	}
	
	@RightCode(RightCode.DELETE)
	@RequestMapping("/delete")
	public @ResponseBody String delete(HttpServletRequest request,String id){
		modelService.deleteModel(id);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "success";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping(value="/addright",method=RequestMethod.GET)
	public String addRight(HttpServletRequest request,Model model,String id){
		Smodel m = modelService.findById(id, Smodel.class);
		if(m==null)
			throw new RuntimeException("没有找到当前菜单信息，请刷新后重试");
		if(StringUtil.isNullOrEmpty(m.getController()))
			throw new RuntimeException("当前菜单不能添加权限信息");
		List<Modelright> modelrights = modelrightService.findModelrightsByMid(m.getId());
		List<Map<String, Object>> mrs = modelService.getRightsMap(modelrights);
		long  maxCode = 65536;
		for(int i=0;i<5;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("val", maxCode);
			map.put("name", maxCode);
			if (modelrightService.isModuleRight(maxCode, modelrights)) {
				map.put("isuse", true);
			} else {
				map.put("isuse", false);
			}
			mrs.add(map);
			maxCode = maxCode*2;
		}
		model.addAttribute("rights", mrs);
		model.addAttribute("modelrights", modelrights);
		model.addAttribute("m", m);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "sys/model/addright";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping(value="/addright",method=RequestMethod.POST)
	public @ResponseBody String addRight(HttpServletRequest request,Model model,String mid,String[] right){
		if(right==null || right.length==0){
			model.addAttribute("errmsg", "至少得选择添加一个权限");
			return "sys/model/addright";
		}
		modelService.addRight(mid,right);
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "success";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping("/getmodelright")
	public @ResponseBody Modelright getModelright(String id){
		return modelrightService.findById(id, Modelright.class);
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping("/updatemodelright")
	public String updateModelright(HttpServletRequest request,String id,String name){
		Modelright modelright = modelrightService.findById(id, Modelright.class);
		if(modelright==null)
			throw new RuntimeException("没有找到当前权限信息");
		if(name==null || "".equals(name.trim())){
			modelright.setName(modelright.getCode()+"");
		}else{
			modelright.setName(name);
		}
		//更新session中的菜单
		userService.addRolesAndModels(request, null);
		return "success";
	}
	
}
