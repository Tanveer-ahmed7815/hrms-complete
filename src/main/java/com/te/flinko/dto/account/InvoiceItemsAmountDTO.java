package com.te.flinko.dto.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceItemsAmountDTO {
	
	private BigDecimal subTotal;
	
	private BigDecimal discountTotal;
	
	private BigDecimal adjustment;
	
	private BigDecimal totalReceivableAmount;
	
	private BigDecimal taxTotal;

}
