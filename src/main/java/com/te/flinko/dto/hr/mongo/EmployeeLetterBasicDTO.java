package com.te.flinko.dto.hr.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeLetterBasicDTO {
	
	private String letterObjectId;
	private Long employeeInfoId;
	private String employeeId;
	private String employeeName;
	private String department;
	private String designation;
	private String type;
	private Long id;

}
