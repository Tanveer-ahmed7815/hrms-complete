package com.te.flinko.dto.account;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountPaySlipListDTO {
	private Long employeeSalaryId;
	private String employeeId;
	private String fullname;
	private BigDecimal totalSalary;
	private String paid;
	private String status;
	private String month;
	private BigDecimal netPay;
	

}
