
package ua.ukma.nc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ua.ukma.nc.dto.*;
import ua.ukma.nc.entity.Group;
import ua.ukma.nc.entity.GroupAttachment;
import ua.ukma.nc.entity.Meeting;
import ua.ukma.nc.entity.MeetingReview;
import ua.ukma.nc.entity.Project;
import ua.ukma.nc.entity.StudentStatus;
import ua.ukma.nc.entity.User;
import ua.ukma.nc.entity.impl.real.GroupImpl;
import ua.ukma.nc.entity.impl.real.ProjectImpl;
import ua.ukma.nc.service.CategoryService;
import ua.ukma.nc.service.CriterionService;
import ua.ukma.nc.service.GroupAttachmentService;
import ua.ukma.nc.service.GroupService;
import ua.ukma.nc.service.MeetingReviewService;
import ua.ukma.nc.service.MeetingService;
import ua.ukma.nc.service.RoleService;
import ua.ukma.nc.service.StudentStatusService;
import ua.ukma.nc.service.UserService;
import ua.ukma.nc.util.exception.MeetingDeleteException;
import ua.ukma.nc.util.exception.RemoveStudentFromGroupException;

/**
 * Created by Nastasia on 05.11.2016.
 */
@Controller
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GroupAttachmentService groupAttachmentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CriterionService criterionService;

    @Autowired
    private StudentStatusService studentStatusService;
    
    @Autowired
    private MeetingReviewService meetingReviewService;

    private static Logger log = LoggerFactory.getLogger(HomeController.class.getName());

    @RequestMapping(value = "/add.ajax", method = RequestMethod.POST)
    @ResponseBody
    public String addGroup(@RequestParam("groupName") String groupName, @RequestParam("projectId") Long projectId) {
        Project project = new ProjectImpl();
        project.setId(projectId);
        Group group = new GroupImpl();
        group.setProject(project);
        group.setName(groupName);
        groupService.createGroup(group);
        return Long.toString(groupService.getByName(groupName).getId());
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addGroup() {
        return "group";
    }

    @RequestMapping(value = "/edit.ajax", method = RequestMethod.POST)
    @ResponseBody
    public String editGroup(
            @RequestParam Long groupId,
            @RequestParam String groupName) {
        Group group = groupService.getById(groupId);
        group.setName(groupName);
        groupService.updateGroup(group);
        return "";
    }

    @RequestMapping(value = "/delete.ajax")
    @ResponseBody
    public String deleteGroup(@RequestParam Long groupId) {
        Long studentsAmount = groupService.getStudentsAmount(groupId);
        if (studentsAmount > 0) {
            return "";
        }
        Group group = groupService.getById(groupId);
        groupService.deleteGroup(group);
        return "";
    }

    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public ModelAndView getGroup(@RequestParam Long id) {

        ModelAndView model = new ModelAndView();
        GroupDto group = new GroupDto(groupService.getById(id));

        List<CategoryDto> categories = categoryService.getByProjectId(group.getProject().getId()).stream().map(CategoryDto::new)
                .collect(Collectors.toList());

        List<CriterionDto> criteria = criterionService.getByProject(group.getProject().getId()).stream().map(CriterionDto::new)
                .collect(Collectors.toList());

        List<UserDto> selectStudents = userService.studentsByGroupId(id).stream().map(UserDto::new)
                .collect(Collectors.toList());
        

        model.addObject("categories", categories);
        model.addObject("criteria", criteria);
        model.addObject("selectStudents", selectStudents);

        List<User> students = groupService.getStudents(id);
        
        List<StudentStatus> studentsWithStatus = new ArrayList<StudentStatus>();
        for (User us : students) {
            studentsWithStatus.add(studentStatusService.getByUserId(us.getId()));
        }
        
        Map<StudentStatus,List<MeetingReview>> studentAndReviews = new HashMap<>();
        for(StudentStatus us : studentsWithStatus){
        	List<MeetingReview> list = new ArrayList<>();
        	list.addAll((meetingReviewService.getByProjectStudent(group.getProject().getId(),us.getStudent().getId())));
        	studentAndReviews.put(us,list);
        }
        //studentsWithStatus.get(0).getStatus().
        List<User> mentors = groupService.getMentors(id);

        List<GroupAttachment> groupAttachments = groupAttachmentService.getByGroup(id);
        List<GroupAttachment> meetingNotes = new ArrayList<>();
        for(GroupAttachment ga : groupAttachments){
        	if(ga.getName().startsWith("meeting_note"))
        		meetingNotes.add(ga);
        }
        groupAttachments.removeAll(meetingNotes);
        List<MeetingDto> meetingDtos = new ArrayList<>();
        for(Meeting meeting: meetingService.getByGroup(id)){
            meetingDtos.add(new MeetingDto(meeting.getId(), meeting.getName(), meeting.getTime(), meeting.getPlace(), meetingService.isReviewed(meeting.getId())));

        }


        String projectName = group.getProject().getName();
        model.addObject("group", group);
        model.addObject("projectName", projectName);


        model.addObject("students", studentAndReviews);
        model.addObject("mentors", mentors);
        model.addObject("meetings", meetingDtos);
        model.addObject("group-id", group.getId());
        model.addObject("meetingNotes",meetingNotes);
        model.addObject("attachments", groupAttachments);
        model.addObject("groupId", group.getId());

        model.setViewName("group-view");


        log.info("Getting group with name : " + group.getName() + " and project: " + group.getProject().getName());
        log.info("Group information sent");
        return model;
    }

    @RequestMapping(value = "/addAttachment", method = RequestMethod.POST)
    @ResponseBody
    public void addGroupAttachment(@RequestParam("id_group") Long idGroup, @RequestParam("name") String name,
                                   @RequestParam("attachment_scope") String attachmentScope) {

        GroupAttachment attachment = new GroupAttachment();

        attachment.setAttachmentScope(attachmentScope);
        attachment.setGroup(groupService.getById(idGroup));
        attachment.setName(name);
        groupAttachmentService.createGroupAttachment(attachment);

    }

    @RequestMapping(value = "/removeMentor", method = RequestMethod.POST)
    @ResponseBody
    public String removeMentor(@RequestParam Long groupId, @RequestParam Long userId) {
        groupService.removeMentor(groupId, userId);
        return "Deleted successfully";
    }

    @RequestMapping(value = "/removeStudent", method = RequestMethod.POST)
    @ResponseBody
    public String removeStudent(@RequestParam Long groupId, @RequestParam Long userId) throws RemoveStudentFromGroupException {
        if (!userService.hasReview(userId, groupId)) {
            throw new RemoveStudentFromGroupException("Student has reviews, removing is forbidden");
        } else {
            groupService.removeStudent(groupId, userId);
            return "Deleted successfully";
        }
    }

    @RequestMapping(value = "/deleteAttachment", method = RequestMethod.POST)
    @ResponseBody
    public void deleteGroupAttachment(@RequestParam("id") Long idAttachment) {
        GroupAttachment attachment = groupAttachmentService.getById(idAttachment);
        groupAttachmentService.deleteGroupAttachment(attachment);

    }

    @RequestMapping(value = "/editMeeting", method = RequestMethod.POST)
    @ResponseBody
    public MeetingDto editMeeting(@RequestParam Long id, @RequestParam String name, @RequestParam String date, @RequestParam String place) {
        int check = meetingService.editMeeting(id, name, date, place);
        if (check == 1) {
            Meeting meeting = meetingService.getById(id);
            System.out.println(meeting.getTime());
            return new MeetingDto(meeting.getId(), meeting.getName(), meeting.getTime(), meeting.getPlace());
        }
        return null;
    }

    @RequestMapping(value = "/deleteMeeting", method = RequestMethod.POST)
    @ResponseBody
    public String deleteMeeting(@RequestParam Long meetingId) throws MeetingDeleteException {
        if(meetingService.isReviewed(meetingId))
            throw new MeetingDeleteException("This meeting was reviewed and cannot be deleted.");
        meetingService.deleteMeeting(meetingService.getById(meetingId));
        return "success";
    }

}
