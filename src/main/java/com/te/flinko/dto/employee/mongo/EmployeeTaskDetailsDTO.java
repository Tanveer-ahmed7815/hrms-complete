package com.te.flinko.dto.employee.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeTaskDetailsDTO {

	private String taskId;
	
	private String taskName;
	

}
