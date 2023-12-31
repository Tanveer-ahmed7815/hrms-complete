package com.te.flinko.dto.employee.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTaskListDTO {

	private String taskId;
	
	private String taskName;
}
