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
public class SalesOrderItemDropdownDTO {
	
	private Long saleItemId;
	
	private String productName;
	
	private String description;
	
	private Long quantity;
	
	private BigDecimal amount;
	
	private BigDecimal discount;
	
	private BigDecimal tax;
	
	private BigDecimal receivableAmount;

}
