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
import com.invoicing.entity.business.Outstore;
import com.invoicing.entity.business.Stock;
import com.invoicing.service.business.OutstoreService;
import com.invoicing.service.business.StockService;
import com.invoicing.utils.DateUtil;
import com.invoicing.utils.HTMLUtil;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

@Controller
@RequestMapping("/outstore")
public class OutstoreController {
	private static final Logger logger = LoggerFactory
			.getLogger(OutstoreController.class);

	@Autowired
	private OutstoreService outstoreService;
	@Autowired
	private StockService productService;

	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/list")
	public String list(
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			String brand, String spec, String color, String starttime,
			String endtime,String remarks)
			throws ParseException {

		logger.info("进入出库管理");

		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
		Date startDate = DateUtil.stringToDate("yyyy-MM-dd", starttime);
		Date endDate = DateUtil.stringToDate("yyyy-MM-dd", endtime);
		List<Outstore> outstores = outstoreService.pageList("from Outstore m ",
				p, "order by m.createtime desc ", "and m.brand like :brand",
				(brand == null ? brand : "%" + brand + "%"),
				" and m.spec like :spec", (spec == null ? spec : "%" + spec
						+ "%"), " and m.color like :color",
				(color == null ? color : "%" + color + "%"),
				" and m.createtime >= :startDate", startDate,
				" and m.createtime < :endDate", endDate,
				"and m.remarks like :remarks",(remarks==null?remarks:"%"+remarks+"%"));
		model.addAttribute("outstores", outstores);
		model.addAttribute("brand", brand);
		model.addAttribute("spec", spec);
		model.addAttribute("color", color);
		model.addAttribute("starttime", startDate);
		model.addAttribute("endtime", endDate);
		model.addAttribute("remarks", remarks);
		String url = StringUtil.getRequestGetUrl(request, "brand", brand,
				"spec", spec, "color", color, "starttime", starttime,
				"endtime", endtime,"remarks",remarks);
		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		return "core/outstore/list";
	}

	@RightCode(RightCode.NEW)
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model, String pid) {
		Stock product = productService.findById(pid, Stock.class);
		if (product == null)
			throw new RuntimeException("没有找到库存信息");
		if (product.getSupply() <= 0)
			throw new RuntimeException("当前手机库存已完，不能进行出库");
		model.addAttribute("product", product);
		return "core/outstore/add";
	}

	/**
	 * 新增出库单
	 * 
	 * @param request
	 * @param pid
	 *            库存ID
	 * @param supply
	 *            出库数量
	 * @param price
	 *            销售单价
	 * @param reward
	 *            销售提成
	 * @return
	 */
	@RightCode(RightCode.NEW)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request, String pid,
			@RequestParam(value = "supply", defaultValue = "0") int supply,
			@RequestParam(value = "price", defaultValue = "0") double price,
			@RequestParam(value = "reward", defaultValue = "0") double reward,
			String remarks) {
		outstoreService.addOutstore(request, pid, supply, price, reward,remarks);
		return "redirect:list";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String update(Model model, String id) {
		Outstore outstore = outstoreService.findById(id, Outstore.class);
		model.addAttribute("outstore", outstore);
		return "core/outstore/update";
	}

	@RightCode(RightCode.UPDATE)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(HttpServletRequest request, Outstore outstore) {
		outstoreService.updateOutstore(request, outstore);
		return "redirect:list";
	}

	@RightCode(RightCode.DELETE)
	@RequestMapping("/delete")
	public @ResponseBody String delete(HttpServletRequest request, String id) {
		return outstoreService.deleteOutstore(request, id);
	}

	@RightCode(RightCode.AUDIT)
	@RequestMapping("/audit")
	public String audit(HttpServletRequest request, String id) {
		outstoreService.auditOutstore(request, id);
		return "redirect:list";
	}

	@RightCode(RightCode.AUDIT)
	@RequestMapping("/unaudit")
	public String unaudit(HttpServletRequest request, String id) {
		outstoreService.unauditOutstore(request, id);
		return "redirect:list";
	}

	@RightCode(RightCode.CONFIRM)
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirm(Model model, String id) {
		Outstore outstore = outstoreService.findById(id, Outstore.class);
		model.addAttribute("outstore", outstore);
		return "core/outstore/confirm";
	}

	@RightCode(RightCode.CONFIRM)
	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public String confirm(String id,@RequestParam(value = "reward", defaultValue = "0") double reward) {
		outstoreService.comfirmOutstore(id, reward);
		return "redirect:list";
	}

}
