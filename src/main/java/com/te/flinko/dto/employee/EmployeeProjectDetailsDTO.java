package com.te.flinko.dto.employee;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeProjectDetailsDTO {

	private Long projectId;
	private String projectName;
	private String projectManagerName;
	private Long holidays;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer daysLaps;
	private Integer availableDays;
	private Integer projectDuration;
	private LocalDate nextDeliverable;
	private Integer delayedBy;

}
