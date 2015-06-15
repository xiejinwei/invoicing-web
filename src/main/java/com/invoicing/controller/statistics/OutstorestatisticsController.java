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
import com.invoicing.entity.business.Outstore;
import com.invoicing.service.business.OutstoreService;
import com.invoicing.utils.DateUtil;

/**
 * 销售统计
 * 
 * @author fang
 *
 */
@Controller
@RequestMapping("/outstorestat")
public class OutstorestatisticsController {

	private static final Logger logger = LoggerFactory
			.getLogger(OutstorestatisticsController.class);

	@Autowired
	private OutstoreService outstoreService;

	/**
	 * 进入销售出库统计页
	 * 
	 * @param model
	 * @return
	 */
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/home")
	public String home(Model model) {
		logger.info("进入销售出库统计页");
		model.addAttribute("date", 0);
		return "statistics/outstorestatistics";
	}

	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/bydate")
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
				.findListByParams("from Outstore o where o.createtime>=? order by o.createtime desc",
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
		chart.put("caption", time+"销售统计表");
		chart.put("subCaption", "");
		chart.put("xAxisName", time);
		chart.put("yAxisName", "销售量");
		chart.put("theme", "fint");
		dataSource.put("chart", chart);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (outstores != null) {
			for (Outstore outstore : outstores) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(
						"label",
						outstore.getBrand()
								+ " "
								+ outstore.getSpec()
								+ " "
								+ outstore.getColor()
								+ " "
								+ DateUtil.dateToString("yyyy-MM-dd",outstore.getCreatetime())
								+ " "
								+ outstore.getUser().getFullname());
				item.put("value", outstore.getSupply());
				data.add(item);
			}
		}
		dataSource.put("data", data);
		return dataSource;
	}
}
