package ua.ukma.nc.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ua.ukma.nc.entity.Category;
import ua.ukma.nc.query.ProjectSearch;
import ua.ukma.nc.service.CategoryService;
import ua.ukma.nc.service.ProjectService;

@Controller
public class ProjectsController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private CategoryService categoryService;

	private static Logger log = LoggerFactory.getLogger(HomeController.class.getName());

	@RequestMapping("/projects")
	public ModelAndView getProjects(@ModelAttribute("projectSearch") ProjectSearch projectSearch) {

		ModelAndView model = new ModelAndView();

		model.addObject("currPage", projectSearch.getPage());
		model.addObject("criteria", projectSearch.getCriteria());
		model.addObject("projects", projectService.search(projectSearch));
		model.addObject("maxPage", projectService.getMaxPage(projectSearch));
		model.addObject("title", "All projects");

		model.setViewName("projects");
		log.info("Projects information sent");
		return model;
	}

	@RequestMapping("/ajaxcategories")
	@ResponseBody
	public List<Category> ajaxCategories() {
		log.info("Categories information sent (AJAX)");
		return categoryService.getAllAjax();
	}

	@RequestMapping(value = "projects/create-project", method = RequestMethod.GET)
	public ModelAndView showCreateProjectForm(Model model) {
		return new ModelAndView("create-project").addObject("title", "Create project");
	}
}
