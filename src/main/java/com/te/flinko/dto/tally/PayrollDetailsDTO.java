package com.te.flinko.dto.tally;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)

public class PayrollDetailsDTO {
	String employeeName;
	String employeeId;
	private BigDecimal totalSalary;
	private Map<String, String> deduction;
	private Map<String, String> earning;
	Integer month;
	Integer year;
}
