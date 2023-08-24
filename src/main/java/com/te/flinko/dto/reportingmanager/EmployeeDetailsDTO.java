package com.te.flinko.dto.reportingmanager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data

public class EmployeeDetailsDTO {
	private String employeeId;
	private Long employeeInfoId; 
	private String fullName;
	private String officialEmailId;
	private Long mobileNumber;
	private String department;
	private String designation;
	private List<String> projectList;
	private String meetingType;
	private String meetingDetails;
	private LocalDate meetingDate;
	private LocalTime startTime;
	private Integer duration;
	private Boolean isFeedbackGiven = Boolean.FALSE;
	private Long meetingId; 
	private String reason;
	private List<MeetingDetailsDTO> employeeDetails;
}
