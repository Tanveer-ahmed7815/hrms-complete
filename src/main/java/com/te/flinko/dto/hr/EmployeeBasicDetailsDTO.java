package com.te.flinko.dto.hr;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeBasicDetailsDTO {

	private Long employeeInfoId;

	private String name;

	private String emailId;

	private Long mobileNumber;

	private String gender;

	private String employeeId;

	private String officialEmailId;

	private LocalDate doj;

	private String branch;

	private String department;

	private String designation;

	public EmployeeBasicDetailsDTO(Long employeeInfoId, String name, String employeeId, String department,
			String designation) {
		super();
		this.employeeInfoId = employeeInfoId;
		this.name = name;
		this.employeeId = employeeId;
		this.department = department;
		this.designation = designation;
	}
	
	

}
