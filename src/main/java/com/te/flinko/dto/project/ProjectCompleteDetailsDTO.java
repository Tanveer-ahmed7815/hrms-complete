package com.te.flinko.dto.project;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCompleteDetailsDTO {

	private Long projectId;
	private String projectName;
	private String clientName;
	private Long holidays;
	private Integer workingDays;
	private Double amountReceived;
	private Double amountPending;
	private Integer availableDays;
	private String status;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long noOfEmpWorking;
	private BigDecimal amountOfWorkDone;
	private LocalDate nextDeliverable;
	private Integer projectDuration;
	private Integer daysLaps;
	private Integer delayedBy;

}
