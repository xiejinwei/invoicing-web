package com.invoicing.controller.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.invoicing.annotation.RightCode;
import com.invoicing.entity.business.Stock;
import com.invoicing.service.business.StockService;
import com.invoicing.utils.DateUtil;

/**
 * 库存统计
 * @author fang
 *
 */
@Controller
@RequestMapping("/productstatistics")
public class StockstatisticsController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(StockstatisticsController.class);
	
	@Autowired
	private StockService productService;
	

	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/home")
	public String home(){
		
		logger.info("进入进货统计首页");
		
		return "statistics/productstatistics";
	}
	
	@RightCode(RightCode.BLOWSE)
	@RequestMapping("/getproducts")
	public @ResponseBody Map<String, Object> getProducts(){
		
		logger.info("获取指定时间段进货统计信息");
		
		Map<String, Object> dataSource = new HashMap<String, Object>();
		Map<String, Object> chart = new HashMap<String, Object>();
		chart.put("caption", "进货统计表");
		chart.put("subCaption", "");
		chart.put("xAxisName", "剩余库存");
		chart.put("yAxisName", "库存量");
		chart.put("theme", "fint");
		dataSource.put("chart", chart);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Stock> products = new ArrayList<Stock>();
		products = productService.findListByParams("from Product p where p.supply>0");
		if (products != null) {
			for (Stock product : products) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("label", product.getBrand() + " " + product.getSpec()
						+ " " + product.getColor() + " "+DateUtil.dateToString("yyyy-MM-dd", product.getCreatetime()));
				item.put("value", product.getSupply());
				data.add(item);
			}
		}
		dataSource.put("data", data);
		return dataSource;
	}
}
