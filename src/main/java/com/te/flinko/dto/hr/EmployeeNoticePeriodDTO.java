package com.te.flinko.dto.hr;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMax;

import lombok.Data;

@Data
public class EmployeeNoticePeriodDTO {
	
	private Long resignationId;
	@DecimalMax(value  = "12.00" ,message = "please enter valid duration in months ")
	private Double noticePeriodDuration;
	private LocalDate noticePeriodStartDate;
	private LocalDate noticePeriodEndDate;

}
