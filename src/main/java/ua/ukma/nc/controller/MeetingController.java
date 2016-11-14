
package ua.ukma.nc.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ua.ukma.nc.dto.MarkInformation;
import ua.ukma.nc.entity.Category;
import ua.ukma.nc.entity.Criterion;
import ua.ukma.nc.entity.Meeting;
import ua.ukma.nc.entity.MeetingResult;
import ua.ukma.nc.entity.MeetingReview;
import ua.ukma.nc.entity.Role;
import ua.ukma.nc.entity.User;
import ua.ukma.nc.entity.impl.real.MeetingReviewImpl;
import ua.ukma.nc.service.CategoryService;
import ua.ukma.nc.service.CriterionService;
import ua.ukma.nc.service.GroupService;
import ua.ukma.nc.service.MeetingResultService;
import ua.ukma.nc.service.MeetingReviewService;
import ua.ukma.nc.service.MeetingService;
import ua.ukma.nc.service.UserService;

/**
 * @author Oleh Khomandiak
 */
@Controller
public class MeetingController {

	@Autowired
	private MeetingService meetingService;

	@Autowired
	private MeetingReviewService meetingReviewService;

	@Autowired
	private MeetingResultService meetingResultService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CriterionService criterionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;
	
	@RequestMapping(value = "/meeting/{id}", method = RequestMethod.GET)
	public String getMeetings(Model model, Principal principal, @PathVariable long id) {

		Meeting meeting = meetingService.getById(id);
		List<User> all = (groupService.getById(meeting.getGroup().getId())).getUsers();
		List<MeetingReview> meetingReviews = meetingReviewService.getByMeeting(id);

		List<Long> evaluated = new ArrayList<>();
		for (MeetingReview mr : meetingReviews)
			evaluated.add(mr.getStudent().getId());

		List<User> unevaluated = new ArrayList<>();
		for (User user : (all))
			for (Role role : user.getRoles())
				if (role.getId() == 4)
					if (!evaluated.contains(user.getId()))
						unevaluated.add(user);

		Map<User, List<MarkInformation>> markInformation = new HashMap<>();
		for (Long user : evaluated)
			markInformation.put(userService.getById(user), meetingResultService.getByMeeting(user, id));

		List<Criterion> criteria = criterionService.getByMeeting(id);
		List<Category> category = new ArrayList<>();
		for (Criterion criterion : criteria)
			if (!category.contains(categoryService.getById(criterion.getCategory().getId())))
				category.add(categoryService.getById(criterion.getCategory().getId()));

		
		model.addAttribute("criteria", criteria);
		model.addAttribute("marks", markInformation);
		model.addAttribute("students", unevaluated);
		model.addAttribute("meeting", meetingService.getById(id));
		model.addAttribute("categories", category);
		return "certainMeeting";
	}
}
