package com.invoicing.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 首页控制器
 * 
 * @author xiejinwei
 * 
 * @since 2015-06-12
 * 
 */

@Controller
public class HomeController {

	@RequestMapping(value="/",method=RequestMethod.GET)
	public String home(HttpServletRequest request){
		System.out.println(request.getCharacterEncoding()+":"+request.getContextPath()+" : "+request.getServletContext());
		return "home";
	}
}
