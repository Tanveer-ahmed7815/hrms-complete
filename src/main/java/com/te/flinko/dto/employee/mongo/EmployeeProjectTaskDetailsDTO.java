package com.te.flinko.dto.employee.mongo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProjectTaskDetailsDTO {

	private String projectId;
	
	private String projectName;
	
	private List<EmployeeTaskDetailsDTO> taskDetails;
}
