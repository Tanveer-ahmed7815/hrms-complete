package com.te.flinko.dto.hr;

import java.util.Map;

import lombok.Data;

@Data
public class EmployeeLeaveDetailsDTO {
	
	private Long employeeLeaveId;
	
	private Map<String,String> leavesDetails;
	
	private Long employeeInfoId;

}
