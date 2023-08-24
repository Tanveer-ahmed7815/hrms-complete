	package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceSalaryDTO {

	private String fullName;
	private String employeeId;
	private BigDecimal amount;
	private BigDecimal emi;
	private String status;
	private Long companyId; 
	private Long advanceSalaryId;
	private LocalDateTime requestedOn;
}
