package com.te.flinko.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePerformanceDetailsDTO {
	private String month;
	private Long employeeInfoId;
	private Long year;
	
	

}
