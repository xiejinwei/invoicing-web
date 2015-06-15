package com.invoicing.aspect;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicing.annotation.RightCode;
import com.invoicing.annotation.Token;
import com.invoicing.entity.global.Modelrole;
import com.invoicing.entity.global.Smodel;
import com.invoicing.entity.global.User;
import com.invoicing.exception.RBCException;
import com.invoicing.service.global.ModelService;
import com.invoicing.service.global.ModelroleService;
import com.invoicing.service.global.UserService;

@Aspect
@Service
public class AuthorInterceptorAutoAspect {

	@Resource
	public HttpServletRequest request;
	@Autowired
	private ModelService modelService; 
	@Autowired
	private UserService userService;
	@Autowired
	private ModelroleService modelroleService;

	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object controlleraround(ProceedingJoinPoint proce) throws Throwable {
		// 调用路径
		Object target = proce.getTarget();
		MethodSignature signature = (MethodSignature) proce.getSignature();
		// 取出方法
		Method method = signature.getMethod();
		String controllername = method.getDeclaringClass().getName();
		request.setAttribute("controller", controllername);
		if (method.getDeclaringClass().isInterface()) {
			method = target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
		}
		// 增加token，防重复提交
		Token token = method.getAnnotation(Token.class);
		if (token != null && token.token()) {
			if (token.value().equals("on")) {
				request.getSession().setAttribute("token", new Date().getTime() + "");
			} else {
				String stoken = (String) request.getSession().getAttribute("token");
				if (stoken == null)
					throw new RuntimeException("请勿重复操作");
				else
					request.getSession().removeAttribute("token");
			}
		}
		// 当前登录用户
		User user = userService.getSessionuser(request);
		// 获取配置权限
		RightCode rightcode = method.getAnnotation(RightCode.class);
		// 未配置,需要登录
		if (rightcode == null) {
			if (user != null)
				return proce.proceed();
			if (user == null) {
				// 检查是什么地方调用
				throw new RBCException("logout");
			}
		}

		// 无权限限制
		if (rightcode != null && rightcode.value() == RightCode.GUEST)
			return proce.proceed();
		if (controllername.indexOf("appinterface") != -1 && rightcode != null && user != null) {
			return proce.proceed();
		}

		// 如果是超级管理员，
		if (user != null && user.getUsername().equals("admin"))
			return proce.proceed();

		// 用户未登录
		if (user == null) {
			if (rightcode.value() == RightCode.GUEST)
				return proce.proceed();
			throw new RBCException("logout");
		}

		// 具体权限验证
		List<Smodel> models = userService.getUsermodels(request,user);
		List<Modelrole> mrs = userService.getUserModelRoles(request,user);
		if (models != null) {
			for (Smodel model : models) {
				if (model.getController() != null && model.getController().equals(controllername)) {
					// 当前控制器
					if (mrs != null) {
						for (Modelrole mr : mrs) {
							if (mr.getMid().equals(model.getId()) && (mr.getCode() & rightcode.value()) == rightcode.value()) {
								// 验证操作权限
								return proce.proceed();
							}
						}
					}
				}
			}
		}
		throw new RuntimeException("对不起，你无权访问");
	}
}
