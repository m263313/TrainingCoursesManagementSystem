package ua.ukma.nc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import ua.ukma.nc.controller.HomeController;
import ua.ukma.nc.vo.AjaxResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends DefaultHandlerExceptionResolver {

	private static Logger log = LoggerFactory.getLogger(HomeController.class.getName());

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(MultipartException.class)
	@ResponseBody
	public AjaxResponse handleMaxUploadException(Exception e) {
		AjaxResponse response = new AjaxResponse();

		response.addMessage("file", messageSource.getMessage("fail.size", null, LocaleContextHolder.getLocale()));
		response.setCode("204");

		return response;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ModelAndView handleException(Exception ex) {
		log.error(ex.getMessage());
		return new ModelAndView("error/404");
	}
	
	
}