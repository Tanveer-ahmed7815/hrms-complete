package com.te.flinko.dto.hr;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ExitEmployeeDetailsDTO {
	
	private Long employeeInfoId;
	private String fullName;
	private String officialId;
	private String officialEmailId;
	private String department;
	private String designation;
	private String reportingManager;
	private BigDecimal annualSalary;
	private Long mobileNumber;
	private String reason;
	private String discussionType;
	private String link;
	private LocalDate discussionDate;
	private OrganiserDetialsDTO organiser;
	
}
