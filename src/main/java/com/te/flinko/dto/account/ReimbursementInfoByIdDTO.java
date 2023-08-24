package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReimbursementInfoByIdDTO {
	private Long reimbursementId;
	private String expenseCategoryName;
	private LocalDate expenseDate;
	private BigDecimal amount;
	private String fullName;
	private String status;
	private String attachmentUrl;
	private String description;
	
}
