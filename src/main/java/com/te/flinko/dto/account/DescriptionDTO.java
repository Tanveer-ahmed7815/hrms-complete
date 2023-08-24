package com.te.flinko.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DescriptionDTO {
	private String description;
	private Long purchaseInvoiceId;

}
