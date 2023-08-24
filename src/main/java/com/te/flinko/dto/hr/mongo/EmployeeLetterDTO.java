package com.te.flinko.dto.hr.mongo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeLetterDTO {
	
	private String letterObjectId;
	private Long employeeInfoId;
	private String employeeId;
	private String employeeName;
	private String department;
	private String designation;
	private String type;
	private Long id;
	private String branchName;
	private LocalDate issuedDate;
	private String issuedBy;
	private String url;
	private String rejectionReason;

}
