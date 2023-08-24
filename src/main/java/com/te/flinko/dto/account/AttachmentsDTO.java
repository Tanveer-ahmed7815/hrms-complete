package com.te.flinko.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentsDTO {
	private String attachment;
	private Long purchaseInvoiceId;	

}
