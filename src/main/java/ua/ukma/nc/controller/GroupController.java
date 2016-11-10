
package ua.ukma.nc.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.ukma.nc.entity.Group;
import ua.ukma.nc.entity.Project;
import ua.ukma.nc.entity.Role;
import ua.ukma.nc.entity.User;
import ua.ukma.nc.entity.impl.real.GroupImpl;
import ua.ukma.nc.entity.impl.real.ProjectImpl;
import ua.ukma.nc.service.GroupService;
import ua.ukma.nc.service.RoleService;

/**
 * Created by Nastasia on 05.11.2016.
 */
@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private RoleService roleService;
    private static Logger log = LoggerFactory.getLogger(HomeController.class.getName());
    
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void addGroup(@RequestParam("group_name") String groupName, @RequestParam("project_id") Long projectId){
        Project project = new ProjectImpl();
        project.setId(projectId);
        Group group = new GroupImpl();
        group.setProject(project);
        group.setName(groupName);
        groupService.createGroup(group);
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String addGroup(){
        return "group";
    }

	
	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public ModelAndView getGroup(@RequestParam Long id) {

		ModelAndView model = new ModelAndView();
		Group group = groupService.getById(id);
		List<User> users = group.getUsers();
		List<User> students = new ArrayList<User>();
		List<User> mentors = new ArrayList<User>();
		for(User us: users){
			boolean isMentor=false;
			List<Role> roles = us.getRoles();
			for(Role r : roles){
				System.out.println(r.getTitle());
				if(r.getTitle().equals("ROLE_MENTOR")){
					mentors.add(us);
					isMentor=true;
				}
				
			}
			if(!isMentor) students.add(us);
		}
		model.addObject("group-name",group.getName());
		model.addObject("group-project",group.getProject().getName());
		model.addObject("students",students);
		model.addObject("mentors",mentors);
	//	model.addObject("users",users);
		model.setViewName("group-view");
	
		for(User user : group.getUsers()){
			log.info("users name : "+ user.getFirstName() + " users' role"+ user.getRoles());
		}
		
		log.info("Getting group with name : "+group.getName()+" and project: "+group.getProject().getName());
		log.info("Group information sent");
		return model;
	}


}