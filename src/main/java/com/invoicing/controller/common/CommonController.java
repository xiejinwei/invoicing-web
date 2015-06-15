package com.invoicing.controller.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.invoicing.entity.global.Qfiles;
import com.invoicing.service.business.QfilesService;
import com.invoicing.utils.FileUtil;

@Controller
@RequestMapping("/common")
public class CommonController {

	private static final Logger logger = LoggerFactory
			.getLogger(CommonController.class);

	@Autowired
	private QfilesService qfilesService;

	@RequestMapping("/top")
	public String top(HttpServletRequest request, Model nodel) {
		return "common/top";
	}

	// 公共文件上传调用地址，主要用于文本编辑器中
	@RequestMapping("/upload")
	public @ResponseBody Map<String, Object> editorUploadFile(
			HttpServletRequest request,
			@RequestParam(value = "imgFile", required = false) MultipartFile imgFile,
			@RequestParam(value = "localUrl", required = false) MultipartFile localUrl,
			String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String path = FileUtil.upladFile(request, imgFile==null?localUrl:imgFile);
			String url = FileUtil.getServerpath(path);
			Qfiles file = new Qfiles();
			file.setCreatetime(new Date());
			file.setPath(path);
			file.setUrl(url);
			file.setType(Qfiles.EDITOR);
			file.setSrcid(id);
			qfilesService.save(file);
			map.put("error", 0);
			map.put("url", url);
		} catch (Exception e) {
			logger.error("文本编辑器上传文件失败，error:" + e.getMessage());
			map.put("error", 1);
			map.put("message", e.getMessage());
		}
		return map;
	}
}
