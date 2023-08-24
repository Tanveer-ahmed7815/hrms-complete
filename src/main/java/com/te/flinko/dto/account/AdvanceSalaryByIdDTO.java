package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceSalaryByIdDTO {
	private String fullName;
	private String employeeId;
	private BigDecimal amount;
	private BigDecimal emi;
	private String status;
	private Long companyId; 
	private Long advanceSalaryId;
	private LocalDateTime requestedOn;
	private String reason;
	private String rejectedBy;
	private String rejectedReason;
	private LinkedHashMap<String, String> approvedBy;
}
