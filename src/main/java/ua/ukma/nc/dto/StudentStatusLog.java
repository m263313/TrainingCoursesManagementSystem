package ua.ukma.nc.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import ua.ukma.nc.entity.Status;
import ua.ukma.nc.entity.StatusLog;

public class StudentStatusLog {
	private String statusDescription;
	private String statusTitle;
	private String commentary;
	private Long employeeId;
	private String firstName;
	private String lastName;
	private String secondName;
	private String date;
	
	public StudentStatusLog(){
		
	}
	
	public StudentStatusLog(StatusLog statusLog) {
		Status status = statusLog.getNewStatus();
		setCommentary(statusLog.getCommentary());
		setDate(statusLog.getDate());
		setEmployeeId(statusLog.getEmployee().getId());
		setStatusDescription(status.getDescription());
		setFirstName(statusLog.getEmployee().getFirstName());
		setLastName(statusLog.getEmployee().getLastName());
		setSecondName(statusLog.getEmployee().getSecondName());
		setStatusTitle(status.getTitle());
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public String getCommentary() {
		return commentary;
	}
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getDate() {
		return date;
	}

	public void setDate(Timestamp time) {
		this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
	}

	public String getStatusTitle() {
		return statusTitle;
	}

	public void setStatusTitle(String statusTitle) {
		this.statusTitle = statusTitle;
	}
}
