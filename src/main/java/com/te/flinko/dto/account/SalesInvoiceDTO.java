package com.te.flinko.dto.account;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SalesInvoiceDTO {
	private Long salesInvoiceId;
	private String status;
	private String subject;
	private LocalDate invoiceDate;
	private Long invoiceOwner;
	private String clientName;

}
