package com.te.flinko.dto.account;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MarkAsPaidSalaryListDTO {
	private Long employeeSalaryId;
	private BigDecimal totalSalary;
}
