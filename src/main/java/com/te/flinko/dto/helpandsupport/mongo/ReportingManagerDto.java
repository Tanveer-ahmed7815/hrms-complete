package com.te.flinko.dto.helpandsupport.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportingManagerDto {
	private String employeeId;
	private String firstName;
	private String lastName;

}
