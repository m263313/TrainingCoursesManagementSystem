package ua.ukma.nc.service.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import ua.ukma.nc.dto.CategoryDto;
import ua.ukma.nc.dto.CategoryResult;
import ua.ukma.nc.dto.CertainMarkDto;
import ua.ukma.nc.dto.CriterionResult;
import ua.ukma.nc.dto.MarkTableDto;
import ua.ukma.nc.dto.MeetingReviewDto;
import ua.ukma.nc.dto.StudentProfile;
import ua.ukma.nc.dto.UserDto;
import ua.ukma.nc.service.MarkTableService;
import ua.ukma.nc.service.StudentService;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentMarksExcel extends AbstractXlsView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<MarkTableDto> markTablesDto = (List<MarkTableDto>) model.get("markTablesDto");
		UserDto student = (UserDto) model.get("student");
		List<String> names = (List<String>) model.get("names");
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + "student-"+ student.getLastName()+"-"+ student.getFirstName() + ".xls\"");
		
		for(int i=0; i<markTablesDto.size();i++)
			createSheet(workbook, markTablesDto.get(i), names.get(i));
	}
	
	private void createSheet(Workbook workbook, MarkTableDto markTableDto, String name){
		Sheet sheet = workbook.createSheet(name);

		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("#");

		List<MeetingReviewDto> meetings = markTableDto.getMeetings();
		for (int i = 1; i < meetings.size() + 1; i++)
			header.createCell(i).setCellValue(meetings.get(i - 1).getName());

		int rowCount = 1;
		List<CategoryResult> marks = markTableDto.getTableData();
		for (CategoryResult key : marks) {

			sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, meetings.size()));
			Row keyRow = sheet.createRow(rowCount++);
			keyRow.createCell(0).setCellValue(key.getCategoryDto().getName());

			List<CriterionResult> criterionResults = key.getCriteriaResults();

			for (CriterionResult criterionResult : criterionResults) {
				Row markRow = sheet.createRow(rowCount++);
				markRow.createCell(0).setCellValue(criterionResult.getCriterionName());

				int column = 1;

				for (CertainMarkDto mark : criterionResult.getMarks()) {
					markRow.createCell(column++).setCellValue(mark.getValue());
				}
			}
		}
	}
}