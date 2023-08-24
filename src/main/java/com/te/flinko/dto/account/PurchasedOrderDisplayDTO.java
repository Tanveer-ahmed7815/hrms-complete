package com.te.flinko.dto.account;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PurchasedOrderDisplayDTO {
	
	private Long purchaseOrderId;
	private String purchaseOrderOwner;
	private String purchaseOrderNumber;
	private String status;
	private BigDecimal totalPayableAmount;
	private String vendorId;
	private String vendorName;
	private String contactName;

}
