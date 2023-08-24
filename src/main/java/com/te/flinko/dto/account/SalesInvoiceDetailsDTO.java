package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesInvoiceDetailsDTO {

	private Long salesInvoiceId;

	private Long salesOrder;

	private LocalDate invoiceDate;

	private String exciseDuty;

	private LocalDate dueDate;

	private String status;

	@Max(message = "Sales Commission accepts 8 digits 2 decimals only", value = (long) 99999999.99)
	private BigDecimal salesCommission;

	private String dealName;

	private String contactName;

	private String attachments;

	private String termsAndConditions;

	private String description;

	private BigDecimal subTotal;

	private BigDecimal discountTotal;

	private BigDecimal adjustment;

	private BigDecimal totalReceivableAmount;

	private BigDecimal taxTotal;

}
