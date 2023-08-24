package com.te.flinko.dto.hr;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDisplayDetailsDTO {

	private Long employeeId;
	private String employeeOfficalId;
	private String fullName;
	private String officialEmailId;
	private String designation;
	private String department;
	private Boolean isActive;
	private Map<String, String> status;
	
}
