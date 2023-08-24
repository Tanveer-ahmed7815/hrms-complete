package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailsDTO {
	private Long companyId;

	private String exciseDuty;
	private LocalDate dueDate;
	private String status;

	@Max(message = "Sales Commission accepts 8 digits 2 decimals only", value = (long) 99999999.99)
	private BigDecimal salesCommission;
	private LocalDate invoiceDate;
	private String invoiceOwner;
	private String dealName;
	private String contactName;
	private String subject;
	private String vendorId;
	private Long purchaseOrderId;
	private Long purchaseInvoiceId;

}
