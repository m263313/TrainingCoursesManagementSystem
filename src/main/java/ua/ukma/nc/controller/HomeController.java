package ua.ukma.nc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ua.ukma.nc.service.*;

import java.security.Principal;

/**
 * Created by Алексей on 15.10.2016.
 */
@Controller
public class HomeController {

	private static Logger log = LoggerFactory.getLogger(HomeController.class.getName());

	@RequestMapping("/")
    public String getUser() {
        log.info("Sending........");
        return "redirect:/projects";
    }



}
