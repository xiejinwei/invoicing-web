package com.invoicing.controller.business;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import com.invoicing.entity.business.Instore;
import com.invoicing.entity.business.Supplier;
import com.invoicing.service.business.InstoreService;
import com.invoicing.service.business.SupplierService;
import com.invoicing.utils.DateUtil;
import com.invoicing.utils.HTMLUtil;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

/**
 * 入库管理
 * 
 * @author xjw
 *
 */
@Controller
@RequestMapping("/instore")
public class InstoreController {

	private static final Logger logger = LoggerFactory
			.getLogger(InstoreController.class);

	@Autowired
	private InstoreService instoreService;
	@Autowired
	private SupplierService supplierService;

	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/list")
	public String list(
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			String brand,String spec,String color,String starttime,String endtime,
			String supplierid,String remarks) throws ParseException {

		logger.info("进入入库");

		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
		Date startDate = DateUtil.stringToDate("yyyy-MM-dd", starttime);
		Date endDate = DateUtil.stringToDate("yyyy-MM-dd", endtime);
		List<Instore> instores = instoreService.pageList("from Instore m ", p,
				"order by m.createtime desc ", 
				" and m.brand like :brand",(brand == null ? brand : "%" + brand + "%"),
				" and m.spec like :spec",(spec == null ? spec : "%" + spec + "%"),
				" and m.color like :color",(color == null ? color : "%" + color + "%"),
				" and m.createtime >= :startDate",startDate,
				" and m.createtime < :endDate",endDate,
				"and m.remarks like :remarks",(remarks==null?remarks:"%"+remarks+"%")
				);
		model.addAttribute("instores", instores);
		model.addAttribute("brand", brand);
		model.addAttribute("spec", spec);
		model.addAttribute("color", color);
		model.addAttribute("supplierid", supplierid);
		model.addAttribute("starttime", startDate);
		model.addAttribute("endtime", endDate);
		model.addAttribute("remarks", remarks);
		String url = StringUtil.getRequestGetUrl(request, "brand", brand,
				"spec",spec,"color",color,"supplierid",supplierid,"starttime",
				starttime,"endtime",endtime,"remarks",remarks);
		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		return "core/instore/list";
	}
	
	@RightCode(RightCode.NEW)
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(Model model){
		List<Supplier> suppliers = supplierService.list("from Supplier s");
		model.addAttribute("suppliers", suppliers);
		return "core/instore/add";
	}
	
	@RightCode(RightCode.NEW)
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public String add(Instore instore,String time) throws ParseException{
		Date createtime = DateUtil.stringToDate("yyyy-MM-dd", time);
		instore.setCreatetime(createtime);
		instoreService.addInstore(instore);
		return "redirect:list";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping(value="/update",method=RequestMethod.GET)
	public String update(Model model,String id){
		List<Supplier> suppliers = supplierService.list("from Supplier s");
		model.addAttribute("suppliers", suppliers);
		Instore instore = instoreService.findById(id, Instore.class);
		model.addAttribute("instore", instore);
		return "core/instore/update";
	}
	
	@RightCode(RightCode.UPDATE)
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public String update(Instore inst,String time) throws ParseException{
		inst.setCreatetime(DateUtil.stringToDate("yyyy-MM-dd", time));
		instoreService.updateInstore(inst);
		return "redirect:list";
	}
	
	@RightCode(RightCode.AUDIT)
	@RequestMapping("/audit")
	public String audit(String id){
		instoreService.auditInstore(id);
		return "redirect:list";
	}
	
	@RightCode(RightCode.UNAUDIT)
	@RequestMapping("/unaudit")
	public String unaudit(String id){
		instoreService.unauditInstore(id);
		return "redirect:list";
	}
	
	@RightCode(RightCode.DELETE)
	@RequestMapping("/delete")
	public @ResponseBody String delete(String id){
		Instore instore = instoreService.findById(id, Instore.class);
		if(instore==null)
			return "没有找到要删除的信息";
		instoreService.delete(instore);
		return "success";
	}
}
