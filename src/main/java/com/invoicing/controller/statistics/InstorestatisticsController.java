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
import com.invoicing.service.business.InstoreService;
import com.invoicing.utils.DateUtil;

/**
 * 进货统计
 * 
 * @author fang
 *
 */
@Controller
@RequestMapping("/instorestatistics")
public class InstorestatisticsController {

	private static final Logger logger = LoggerFactory
			.getLogger(InstorestatisticsController.class);

	@Autowired
	private InstoreService instoreService;

	/**
	 * 统计内容
	 * 
	 * @param model
	 * @param date
	 *            0：统计一周 1、统计一月 2：统计一年
	 * @return
	 */
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/home")
	public String home(Model model) {

		logger.info("进入进货统计");

		model.addAttribute("date", 0);
		return "statistics/instorestatistics";
	}

	/**
	 * 对查到的数据进行封装
	 * 
	 * @param instores
	 * @param date
	 *            0：统计一周 1、统计一月 2：统计一年
	 * @return
	 */
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/bydate")
	public @ResponseBody Map<String, Object> getFusioncharDataByDate(
			@RequestParam(value = "date", defaultValue = "0") int date) {
		
		logger.info("进货财务统计");
		
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
				"from Instore i where i.status >0 and i.createtime>=? order by i.createtime",
				starttime);
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
		chart.put("yAxisName", "进货量");
		chart.put("theme", "fint");
		dataSource.put("chart", chart);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (instores != null) {
			for (Instore instore : instores) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("label", instore.getBrand() + " " + instore.getSpec()
						+ " " + instore.getColor()+" "+DateUtil.dateToString("yyyy-MM-dd", instore.getCreatetime()));
				item.put("value", instore.getSupply());
				data.add(item);
			}
		}
		dataSource.put("data", data);
		return dataSource;
	}
}
