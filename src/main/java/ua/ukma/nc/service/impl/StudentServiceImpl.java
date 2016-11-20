package ua.ukma.nc.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.ukma.nc.dto.CategoryChartDto;
import ua.ukma.nc.dto.CategoryDto;
import ua.ukma.nc.dto.CriterionDto;
import ua.ukma.nc.dto.FinalReviewDto;
import ua.ukma.nc.dto.MarkInformation;
import ua.ukma.nc.dto.MeetingResultDto;
import ua.ukma.nc.dto.MeetingReviewDto;
import ua.ukma.nc.dto.StudentMeetingReview;
import ua.ukma.nc.dto.StudentProfile;
import ua.ukma.nc.dto.StudentStatusLog;
import ua.ukma.nc.dto.StudyResultDto;
import ua.ukma.nc.entity.MeetingResult;
import ua.ukma.nc.entity.MeetingReview;
import ua.ukma.nc.entity.StatusLog;
import ua.ukma.nc.entity.User;
import ua.ukma.nc.service.CategoryService;
import ua.ukma.nc.service.ChartService;
import ua.ukma.nc.service.CriterionService;
import ua.ukma.nc.service.FinalReviewService;
import ua.ukma.nc.service.MarkTableService;
import ua.ukma.nc.service.MeetingResultService;
import ua.ukma.nc.service.MeetingReviewService;
import ua.ukma.nc.service.ProjectService;
import ua.ukma.nc.service.StatusLogService;
import ua.ukma.nc.service.StudentService;
import ua.ukma.nc.service.UserService;

@Service
public class StudentServiceImpl implements StudentService {
	
	@Autowired
	private CriterionService criterionService;
	
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ChartService chartService;

	@Autowired
	private MeetingResultService meetingResultService;

	@Autowired
	private StatusLogService statusLogService;

	@Autowired
	private MeetingReviewService meetingReviewService;

	@Autowired
	private MarkTableService markTableService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private FinalReviewService finalReviewService;

	@Override
	public StudentProfile generateStudentProfile(long studentId, long projectId) {
		List<MarkInformation> allMarkInfo = meetingResultService.generateMarkInformation(studentId, projectId);

		StudentProfile studentProfile = new StudentProfile();
		List<StatusLog> statusLoges = statusLogService.getByProjectStudent(projectId, studentId);
		List<StudentStatusLog> studentStatusLoges = new ArrayList<StudentStatusLog>();

		for (StatusLog statusLog : statusLoges) {
			StudentStatusLog studentStatusLog = new StudentStatusLog(statusLog);
			studentStatusLoges.add(studentStatusLog);
		}

		studentProfile.setStudentStatuses(studentStatusLoges);

		List<StudentMeetingReview> studentMeetingReviews = new ArrayList<StudentMeetingReview>();
		List<MeetingReview> meetingReviews = meetingReviewService.getByProjectStudent(projectId, studentId);

		for (MeetingReview meetingReview : meetingReviews) {
			StudentMeetingReview studentMeetingReview = new StudentMeetingReview(meetingReview);
			studentMeetingReviews.add(studentMeetingReview);
		}

		studentProfile.setMeetingReviews(studentMeetingReviews);
		studentProfile.setMarkTableDto(markTableService.getMarkTableDto(studentId, projectId, allMarkInfo));

		studentProfile.setChartInfo(convert(chartService.getChartData(projectId, studentId)));
		studentProfile.setChartInfoFinal(convert(chartService.getChartDataFinalReview(projectId, studentId)));
		
		User user = userService.getById(studentId);
		studentProfile.setLastName(user.getLastName());
		studentProfile.setFirstName(user.getFirstName());
		studentProfile.setSecondName(user.getSecondName());
		
		studentProfile.setProjectName(projectService.getById(projectId).getName());
		
		if(finalReviewService.existsForProject(studentId, projectId, "F"))
			studentProfile.setFinalReview(new FinalReviewDto(finalReviewService.getByStudent(projectId, studentId, "F")));
		
		if(finalReviewService.existsForProject(studentId, projectId, "G"))
			studentProfile.setGeneralReview(new FinalReviewDto(finalReviewService.getByStudent(projectId, studentId, "G")));
		
		if(finalReviewService.existsForProject(studentId, projectId, "T"))
			studentProfile.setTechnicalReview(new FinalReviewDto(finalReviewService.getByStudent(projectId, studentId, "T")));
		
		List<CategoryDto> categories = categoryService.getByProjectId(projectId).stream().map(CategoryDto::new).collect(Collectors.toList());
		List<CriterionDto> criteria = criterionService.getByProject(projectId).stream().map(CriterionDto::new).collect(Collectors.toList());
		
		studentProfile.setProjectCategories(categories);
		studentProfile.setProjectCriteria(criteria);
		
		List<MeetingReviewDto> fullMeetingReviews = new ArrayList<MeetingReviewDto>();
		
		for(MeetingReview meetingReview: meetingReviews){
			MeetingReviewDto meetingReviewDto = new MeetingReviewDto(meetingReview);
			
			List<MeetingResultDto> meetingResultsDto = new ArrayList<MeetingResultDto>();
			
			for(MeetingResult meetingResult: meetingResultService.getByReview(meetingReview.getId())){
				MeetingResultDto meetingResultDto = new MeetingResultDto(meetingResult);
				meetingResultsDto.add(meetingResultDto);
			}
			
			meetingReviewDto.setMarks(meetingResultsDto);
			
			fullMeetingReviews.add(meetingReviewDto);
		}
		Collections.sort(fullMeetingReviews);
		studentProfile.setFullMeetingReviews(fullMeetingReviews);
		return studentProfile;
	}

	private List<CategoryChartDto> convert(Map<String, List<StudyResultDto>> data) {
		List<CategoryChartDto> result = new ArrayList<CategoryChartDto>();

		for (String category : data.keySet()) {
			CategoryChartDto categoryChartDto = new CategoryChartDto();
			categoryChartDto.setCategory(category);
			categoryChartDto.setStudyResults(data.get(category));

			result.add(categoryChartDto);
		}
		return result;
	}

}
