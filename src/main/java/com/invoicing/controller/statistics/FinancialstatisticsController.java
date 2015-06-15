package com.invoicing.controller.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.invoicing.annotation.RightCode;
import com.invoicing.entity.business.Instore;
import com.invoicing.entity.business.Outstore;
import com.invoicing.entity.global.User;
import com.invoicing.service.business.FinancialstatisticsService;
import com.invoicing.service.business.InstoreService;
import com.invoicing.service.business.OutstoreService;
import com.invoicing.service.business.StockService;
import com.invoicing.service.global.UserService;
import com.invoicing.utils.DateUtil;

/**
 * 财务统计
 * @author fang
 *
 */
@Controller
@RequestMapping("/financialstatistics")
public class FinancialstatisticsController {

	private static final Logger logger = LoggerFactory
			.getLogger(FinancialstatisticsController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private InstoreService instoreService;
	@Autowired
	private StockService productService;
	@Autowired
	private OutstoreService outstoreService;
	@Autowired
	private FinancialstatisticsService financialstatisticsService;
	
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/home")
	public String home(Model model){
		logger.info("进入财务统计");
		model.addAttribute("date", 0);
		List<User> users = userService.list();
		model.addAttribute("users", users);
		return "statistics/financialstatistics";
	}
	
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/inbydate")
	public @ResponseBody Map<String, Object> byDate(
			@RequestParam(value = "date", defaultValue = "0") int date){
		Date starttime = null;// 开始时间
		List<Instore> instores = new ArrayList<Instore>();
		if (date == 0) {
			// 周
			starttime = DateUtil.getFIRST_DAY_OF_WEEK();
		} else if (date == 1) {
			// 月
			starttime = DateUtil.get_FIRST_DAY_OF_MONTH();
		} else if (date == 2) {
			// 年
			starttime = DateUtil.get_FIRST_DAY_OF_YEAR();
		}
		instores = instoreService.findListByParams(
				"from Instore i where i.status >0 and i.createtime>=? order by i.brand",
				starttime);
		instores = financialstatisticsService.packageInstores(instores);
		Map<String, Object> dataSource = new HashMap<String, Object>();
		Map<String, Object> chart = new HashMap<String, Object>();
		String time = "";
		if (date == 0)
			time = "本周";
		else if (date == 1)
			time = "本月";
		else if (date == 2)
			time = "全年";
		chart.put("caption", time+"进货统计表");
		chart.put("subCaption", "");
		chart.put("xAxisName", time);
		chart.put("yAxisName", "进货金额");
		chart.put("theme", "fint");
		dataSource.put("chart", chart);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (instores != null) {
			for (Instore instore : instores) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("label", instore.getBrand() + " " + instore.getSpec()
						+ " " + instore.getColor()+" 共计："+instore.getSupply()+"台");
				item.put("value", instore.getAmount());
				data.add(item);
			}
		}
		dataSource.put("data", data);
		return dataSource;
	}
	

	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/outbydate")
	public @ResponseBody Map<String, Object> getFusioncharDataByDate(
			@RequestParam(value = "date", defaultValue = "0") int date) {
		logger.info("跟据时间获取销售出库信息");
		Date starttime = null;// 开始时间
		List<Outstore> outstores = new ArrayList<Outstore>();
		if (date == 0) {
			// 周
			starttime = DateUtil.getFIRST_DAY_OF_WEEK();
		} else if (date == 1) {
			// 月
			starttime = DateUtil.get_FIRST_DAY_OF_MONTH();
		} else if (date == 2) {
			// 年
			starttime = DateUtil.get_FIRST_DAY_OF_YEAR();
		}
		outstores = outstoreService
				.findListByParams("select o from Outstore o where o.createtime>=? order by o.createtime desc",
						starttime);
		Map<String, Object> dataSource = new HashMap<String, Object>();
		//chart Map
		Map<String, Object> chart = new HashMap<String, Object>();
		String time = "";
		if (date == 0)
			time = "本周";
		else if (date == 1)
			time = "本月";
		else if (date == 2)
			time = "全年";
		chart.put("caption", time+"销售统计表");
	    chart.put("showvalues", 0);
		chart.put("plotfillalpha", 95);
		chart.put("formatnumberscale", 0);
		chart.put("showborder", 0);
		dataSource.put("chart", chart);
		
		/**[{"label": "Jan"}]
		 *  "categories": [{"category": 123}]
		 */
		//categories List
		List<User> users = userService.list();
		List<Map<String, Object>> categories = new ArrayList<Map<String,Object>>();
		Map<String, Object> categorys = new HashMap<String, Object>();
		List<Map<String, Object>> category = new ArrayList<Map<String,Object>>();
		for(User user : users){
			Map<String, Object> categ = new HashMap<String, Object>();
			categ.put("label", user.getFullname());
			category.add(categ);
		}
		categorys.put("category", category);
		categories.add(categorys);
		dataSource.put("categories", categories);
		
		//dataset List
		//Y轴数据
		List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
		//抽取一个自定义分类（品牌、型号、颜色）
		List<String> typeList = financialstatisticsService.getPhoneTypelist(outstores);
		for(String type : typeList){
			//分类
			Map<String, Object> datasetcontent = new HashMap<String, Object>();
			datasetcontent.put("seriesname", type);
			//分类数据,根据人员来进行添加
			List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
			for(User user : users){
				Map<String, Object> dataval = new HashMap<String, Object>();
				int supply = financialstatisticsService.getUserTypeSupply(user.getId(),type,outstores);
				dataval.put("value", supply);
				data.add(dataval);
			}
			datasetcontent.put("data", data);
			//加入参数
			dataset.add(datasetcontent);
		}
		dataSource.put("dataset", dataset);
		return dataSource;
	}
}
