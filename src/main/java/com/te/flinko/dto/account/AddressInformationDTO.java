package com.te.flinko.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressInformationDTO {
	private String addressType;
	private String addressDetails;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private Long purchaseOrderId;
	private Long purchaseInvoiceId; 
	
}
