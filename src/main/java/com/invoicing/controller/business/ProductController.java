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
import org.springframework.web.bind.annotation.RequestParam;

import com.invoicing.annotation.RightCode;
import com.invoicing.entity.business.Stock;
import com.invoicing.service.business.StockService;
import com.invoicing.utils.DateUtil;
import com.invoicing.utils.HTMLUtil;
import com.invoicing.utils.Page;
import com.invoicing.utils.StringUtil;

@Controller
@RequestMapping("/product")
public class ProductController {

	private static final Logger logger = LoggerFactory
			.getLogger(ProductController.class);

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
			String endtime, String supplierid,String remarks) throws ParseException {

		logger.info("进入入库");

		Page p = new Page();
		p.setPage(page);
		p.setPageSize(pageSize);
		Date startDate = DateUtil.stringToDate("yyyy-MM-dd", starttime);
		Date endDate = DateUtil.stringToDate("yyyy-MM-dd", endtime);
		List<Stock> products = productService.pageList("from Product m ", p,
				"order by m.supply desc,m.createtime desc", "and m.brand like :brand",
				(brand == null ? brand : "%" + brand + "%"),
				" and m.spec like :spec", (spec == null ? spec : "%" + spec
						+ "%"), " and m.color like :color",
				(color == null ? color : "%" + color + "%"),
				" and m.createtime >= :startDate", startDate,
				" and m.createtime < :endDate", endDate,
				"and m.remarks like :remarks",(remarks==null?remarks:"%"+remarks+"%"));
		model.addAttribute("products", products);
		model.addAttribute("brand", brand);
		model.addAttribute("spec", spec);
		model.addAttribute("color", color);
		model.addAttribute("supplierid", supplierid);
		model.addAttribute("starttime", startDate);
		model.addAttribute("endtime", endDate);
		model.addAttribute("remarks", remarks);
		String url = StringUtil.getRequestGetUrl(request, "brand", brand,
				"spec", spec, "color", color, "supplierid", supplierid,
				"starttime", starttime, "endtime", endtime,"remarks",remarks);
		model.addAttribute("url", HTMLUtil.getNumberPageHTML(p, url));
		return "core/product/list";
	}
}
