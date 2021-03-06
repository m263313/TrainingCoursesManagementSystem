
package ua.ukma.nc.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ua.ukma.nc.dto.CriterionDto;
import ua.ukma.nc.dto.JsonWrapperAbsent;
import ua.ukma.nc.dto.JsonWrapperReview;
import ua.ukma.nc.dto.MarkCommentDto;
import ua.ukma.nc.dto.MarkInformation;
import ua.ukma.nc.entity.Category;
import ua.ukma.nc.entity.Criterion;
import ua.ukma.nc.entity.Meeting;
import ua.ukma.nc.entity.MeetingResult;
import ua.ukma.nc.entity.MeetingReview;
import ua.ukma.nc.entity.Role;
import ua.ukma.nc.entity.User;
import ua.ukma.nc.entity.impl.real.MeetingImpl;
import ua.ukma.nc.entity.impl.real.MeetingReviewImpl;
import ua.ukma.nc.service.CategoryService;
import ua.ukma.nc.service.CriterionService;
import ua.ukma.nc.service.GroupService;
import ua.ukma.nc.service.MarkService;
import ua.ukma.nc.service.MeetingResultService;
import ua.ukma.nc.service.MeetingReviewService;
import ua.ukma.nc.service.MeetingService;
import ua.ukma.nc.service.StatusLogService;
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

	@Autowired
	private MarkService markService;

	@Autowired
	private StatusLogService statusService;

	@RequestMapping(value = "/meeting/{id}", method = RequestMethod.GET)
	public ModelAndView getMeetings(Principal principal, @PathVariable long id) {

		ModelAndView model = new ModelAndView("certainMeeting");
		Meeting meeting = meetingService.getById(id);
		long groupId = meeting.getGroup().getId();
		List<User> all = (groupService.getById(groupId)).getUsers();
		List<MeetingReview> meetingReviews = meetingReviewService.getByMeeting(id);
		List<Long> evaluated = new ArrayList<>();
		for (MeetingReview mr : meetingReviews)
			evaluated.add(mr.getStudent().getId());
		List<User> unevaluated = new ArrayList<>();
		for (User user : all)
			for (Role role : user.getRoles())
				if (role.getId() == 4 && !evaluated.contains(user.getId()) && statusService.exists(user.getId())
						&& groupId == statusService.getNewestGroup(user.getId()))
					unevaluated.add(user);
		Map<User, List<MarkInformation>> markInformation = new TreeMap<>();
		List<User> absent = new ArrayList<>();
		for (Long user : evaluated) {
			MeetingReview mr = meetingReviewService.getByMeetingStudent(id, user);
			if (!mr.getType().equals("A")){
				User userok = userService.getById(user);
				userok.setEmail(mr.getCommentary());
				markInformation.put(userok, meetingResultService.getByMeeting(user, id));
			}else
				absent.add(userService.getById(user));
		}
		// Criteria set
		List<Criterion> criteria = criterionService.getByMeeting(id);
		List<CriterionDto> criterionDtos = new ArrayList<>();
		for (Criterion criterion : criteria) {
			criterionDtos.add(new CriterionDto(criterion.getId(), criterion.getTitle(),
					criterionService.isRatedInMeeting(id, criterion)));
		}

		List<Category> category = new ArrayList<>();
		for (Criterion criterion : criteria)
			if (!category.contains(categoryService.getById(criterion.getCategory().getId())))
				category.add(categoryService.getById(criterion.getCategory().getId()));


		model.addObject("criteria", criterionDtos);
		model.addObject("marks", markInformation);
		model.addObject("students", unevaluated);
		model.addObject("meeting", meetingService.getById(id));
		model.addObject("categories", category);
		model.addObject("absent", absent);
		model.addObject("title", "Meeting " + meeting.getName());
		return model;
	}

	@RequestMapping(value = "/create-meeting", method = RequestMethod.GET)
	public String createMeeting(Model model, Principal principal, @RequestParam("project") long projectId) {
		Meeting meeting = new MeetingImpl();
		List<Criterion> criteria = criterionService.getByProject(projectId);
		model.addAttribute("meetingForm", meeting);
		model.addAttribute("criteria", criteria);
		model.addAttribute("url", "/create-meeting?project=" + projectId);
		return "createMeeting";
	}

	@RequestMapping(value = "/create-meeting", method = RequestMethod.POST)
	public String createMeeting(@ModelAttribute("meetingForm") @Validated MeetingImpl meeting, BindingResult result,
			final RedirectAttributes redirectAttributes, @RequestParam("project") long projectId) {
		if (!result.hasErrors()) {
			meeting.setGroup(groupService.getByProjectId(projectId).get(0));
			meetingService.createMeeting(meeting);
			redirectAttributes.addFlashAttribute("msg", "Meeting added successfully!");
			return "redirect:/projects";
		} else {
			return "createMeeting";
		}
	}

	@RequestMapping(value = "/getAvailableMeetingCriteria", method = RequestMethod.GET)
	@ResponseBody
	public List<CriterionDto> getAvailableMeetingCriteria(@RequestParam Long meetingId) {
		List<CriterionDto> criterionDtos = new ArrayList<>();
		for (Criterion criterion : criterionService.getMeetingUnusedCriteria(meetingId,
				meetingService.getProjectByMeetingId(meetingId))) {
			criterionDtos.add(new CriterionDto(criterion));
		}
		return criterionDtos;
	}

	@RequestMapping(value = "/addMeetingCriteria", method = RequestMethod.POST)
	@ResponseBody
	public CriterionDto addMeetingCriteria(@RequestParam Long meetingId, @RequestParam String criteriaTitle) {
		Criterion criterion = criterionService.getByName(criteriaTitle);
		meetingService.addCriteria(meetingId, criterion);
		return new CriterionDto(criterion.getId(), criterion.getTitle(),
				criterionService.isRatedInProject(meetingId, criterion));
	}

	@RequestMapping(value = "/deleteMeetingCriteria", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity deleteProjectCriteria(@RequestParam Long meetingId, @RequestParam String criteriaTitle) {
		Criterion criterion = criterionService.getByName(criteriaTitle);
		if (criterionService.isRatedInMeeting(meetingId, criterion))
			return ResponseEntity.status(HttpStatus.CONFLICT).body("This criteria was rated and cannot be deleted");
		meetingService.deleteMeetingCriterion(meetingId, criterion);
		return ResponseEntity.ok().body("Success");
	}

	@RequestMapping(value = "/ajax/post/evaluate/{id}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public String postEvaluate(Principal principal, @PathVariable("id") Long userId,
			@RequestBody JsonWrapperReview data) {
		User mentor = userService.getByEmail(principal.getName());
		MeetingReview review = null;
		if (meetingReviewService.getByMeetingStudent(data.getMeetingId(), userId) == null) {
			review = new MeetingReviewImpl((long) 0, "E");
			review.setStudent(userService.getById(userId));
			review.setMeeting(meetingService.getById(data.getMeetingId()));
			review.setMentor(mentor);
			review.setCommentary(data.getComment());
			meetingReviewService.createMeetingReview(review);
			for (MarkCommentDto value : data.getData()) {
				MeetingResult result = new MeetingResult();
				result.setId((long) 0);
				result.setCommentary(value.getCommentary());
				result.setMark(markService.getByValue(value.getValue()));
				result.setCriterion(criterionService.getById((long) value.getCriterionId()));
				result.setMeetingReview(meetingReviewService.getByMeetingStudent(data.getMeetingId(), userId));
				meetingResultService.createMeetingResult(result);
			}
		} else {
			review = meetingReviewService.getByMeetingStudent(data.getMeetingId(), userId);
			if (data.getComment() != null) {
				review.setCommentary(data.getComment());
				meetingReviewService.updateMeetingReview(review);
			}
			List<MeetingResult> previous = meetingResultService.getByReview(review.getId());
			List<MarkCommentDto> value = data.getData();

			for (MarkCommentDto mcd : value) {
				boolean check = true;
				for (MeetingResult mr : previous)
					if (mr.getCriterion().getId() == mcd.getCriterionId()) {
						mr.setCommentary(mcd.getCommentary());
						mr.setMark(markService.getByValue(mcd.getValue()));
						meetingResultService.updateMeetingResult(mr);
						check = false;
					}
				if (check) {
					MeetingResult resultus = new MeetingResult();
					resultus.setId((long) 0);
					resultus.setCommentary(mcd.getCommentary());
					resultus.setMark(markService.getByValue(mcd.getValue()));
					resultus.setCriterion(criterionService.getById((long) mcd.getCriterionId()));
					resultus.setMeetingReview(review);
					meetingResultService.createMeetingResult(resultus);
				}
			}
		}

		return "true";
	}

	@RequestMapping(value = "/ajax/post/absent/{id}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public String postAbsent(Principal principal, @PathVariable("id") Long userId,
			@RequestBody JsonWrapperAbsent data) {
		User mentor = userService.getByEmail(principal.getName());
		MeetingReview review = null;
		review = new MeetingReviewImpl((long) 0, "A");
		review.setStudent(userService.getById(userId));
		review.setMeeting(meetingService.getById(data.getMeetingId()));
		review.setMentor(mentor);
		review.setCommentary("");
		meetingReviewService.createMeetingReview(review);
		return "true";

	}

	@RequestMapping(value = "/isMeetingReviewed", method = RequestMethod.GET)
	@ResponseBody
	public boolean isMeetingReviewed(@RequestParam Long meetingId) {
		return meetingService.isReviewed(meetingId);
	}

}
