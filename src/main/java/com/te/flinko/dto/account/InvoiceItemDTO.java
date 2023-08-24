package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDTO {
	private Long companyId;
	private BigDecimal subTotal;
	private BigDecimal taxTotal;
	private BigDecimal discountTotal;
	private String adjustment;
	private BigDecimal totalPayableAmount;
	private Long purchaseOrderId;
	private Long purchaseInvoiceId;
	private List<InvoiceItemsDTO> invoiceDetailsDTOs;
}
