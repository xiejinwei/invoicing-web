package com.invoicing.controller.business;

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

import com.invoicing.annotation.RightCode;
import com.invoicing.entity.business.Supplier;
import com.invoicing.entity.global.User;
import com.invoicing.service.business.SupplierService;
import com.invoicing.utils.HTMLUtil;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

/**
 * 供货商管理
 * 
 * @author xjw
 * 
 */

@Controller
@RequestMapping("/supplier")
public class SupplierController {

	private static final Logger logger = LoggerFactory
			.getLogger(SupplierController.class);

	@Autowired
	private SupplierService supplierService;

	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/list")
	public String list(
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			String name) {

		logger.info("进入菜单管理");

		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
		List<Supplier> suppliers = supplierService.pageList("from Supplier s ",
				p, " order by s.name", "and s.name like :name",
				(name == null ? name : "%" + name + "%"));
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("name", name);
		String url = StringUtil.getRequestGetUrl(request, "name", name);
		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		return "core/supplier/list";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "core/supplier/add";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request, Model model, Supplier supplier) {
		if (StringUtil.isNullOrEmpty(supplier.getName())) {
			model.addAttribute("errmsg", "供应商名字不能为空");
			return "core/supplier/add";
		}
		supplier.setCreatetime(new Date());
		supplier.setUser((User) request.getSession().getAttribute("suser"));
		supplierService.save(supplier);
		return "redirect:list";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String update(Model model, String sid) {
		Supplier supplier = supplierService.findById(sid, Supplier.class);
		model.addAttribute("supplier", supplier);
		return "core/supplier/update";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Model model, Supplier supplier) {
		Supplier su = supplierService
				.findById(supplier.getId(), Supplier.class);
		if (su == null)
			throw new RuntimeException("没有找到要修改的供应商，请刷新再试");
		if (StringUtil.isNullOrEmpty(supplier.getName())) {
			model.addAttribute("errmsg", "供应商名字不能为空");
			return "core/supplier/update";
		}
		su.setName(supplier.getName());
		su.setPerson(supplier.getPerson());
		su.setPhone(supplier.getPhone());
		supplierService.update(su);
		return "redirect:list";
	}

	@RightCode(RightCode.DELETE)
	@RequestMapping("/delete")
	public String delete(String id) {
		Supplier su = supplierService.findById(id, Supplier.class);
		if (su == null)
			throw new RuntimeException("没有找到要修改的供应商，请刷新再试");
		supplierService.delete(su);
		return "redirect:list";
	}
}
