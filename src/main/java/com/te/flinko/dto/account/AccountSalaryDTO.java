package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSalaryDTO {
	private String employeeId;
	private String fullName;
	private BigDecimal totalSalary;
	private Map<String, String> additional;
	private Map<String, String> deduction;
	private String lop;
	private BigDecimal netPay;
	private String status;
	private Long employeeSalaryId;
	private Double totalAdditional;
	private Double totalDeduction;
	private String month;
	

	

}
