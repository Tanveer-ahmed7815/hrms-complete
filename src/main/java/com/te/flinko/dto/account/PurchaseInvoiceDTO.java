package com.te.flinko.dto.account;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInvoiceDTO {
	private Long purchaseInvoiceId;
	private String subject;
	private String status;
	private LocalDate invoiceDate;
	private String vendorName;
	private Long invoiceOwner;
	

}
