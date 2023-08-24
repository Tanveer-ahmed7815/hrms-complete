package com.te.flinko.dto.account;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SalesItemsDTO {
	private Long companyId;
	private BigDecimal subTotal;
	private BigDecimal taxTotal;
	private BigDecimal discountTotal;
	private String adjustment;
	private BigDecimal totalPayableAmount;
//	private Long salesOrderId;
    private List<SalesItemDTO> salesItems;
}
