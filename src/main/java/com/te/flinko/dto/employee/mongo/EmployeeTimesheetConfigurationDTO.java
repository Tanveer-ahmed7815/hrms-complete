package com.te.flinko.dto.employee.mongo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTimesheetConfigurationDTO {
	
	private Integer from;
		
	private Integer to;
		
	private Integer month;
		
	private Integer year;
		
	private Boolean isSubmitted;
		
	private List<String> projects;
	
	private Long companyId;
	
	private Long employeeInfoId;
	
	private Boolean isMonthlyTimesheet;
}
