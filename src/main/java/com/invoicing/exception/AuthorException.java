package com.invoicing.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

public class AuthorException implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception ex) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("excmsg", ex.getMessage());
		if(ex instanceof ConversionNotSupportedException || ex instanceof HttpMessageNotWritableException)
			mv.setViewName("exception/500");
		else if(ex instanceof NoSuchRequestHandlingMethodException || ex instanceof MissingServletRequestParameterException || ex instanceof TypeMismatchException)
			mv.setViewName("exception/404");
		else
			mv.setViewName("exception/authorerexception");
		return mv;
	}

}
