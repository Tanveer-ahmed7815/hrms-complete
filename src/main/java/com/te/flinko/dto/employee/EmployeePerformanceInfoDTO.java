package com.te.flinko.dto.employee;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class EmployeePerformanceInfoDTO {
	private BigDecimal punctual;
	private BigDecimal leaves;
	private BigDecimal targetAchived;
	private BigDecimal activities;
	private BigDecimal tickets;

}
