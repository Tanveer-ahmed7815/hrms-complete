package com.te.flinko.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeNotificationDTO {
	
	private Long employeeNotificationId;
	
	private String description;
	
	private Boolean isSeen;

}
