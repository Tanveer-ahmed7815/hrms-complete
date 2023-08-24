package com.te.flinko.dto.account;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountPaySlipDTO {
	private Long employeeSalaryId;
	private String fullName;
	private BigDecimal totalSalary;
	private BigDecimal netPay;
	private String status;
	private String paymentStatus;
	private String employeeId;
}
