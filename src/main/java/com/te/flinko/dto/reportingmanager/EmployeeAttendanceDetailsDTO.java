package com.te.flinko.dto.reportingmanager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeAttendanceDetailsDTO {
	
	private String objectId;
	private Integer detailsId;
	private String employeeId;
	private Long employeeInfoId;
	private String employeeName;
	private String branch;
	private String department;
	private String designation;
	private LocalDate date;
	private LocalDateTime loginTime;
	private LocalDateTime logoutTime;
 
}
