package com.te.flinko.dto.tally;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)

public class OrderItemDTO {

	private String productName;
	private Long quantity;
	private BigDecimal discount;
	private BigDecimal amount;
	private BigDecimal tax;
	private String ledgerName;
	private String stockGroup;
	private Long purchaseOrderId;
	private Long salesOrderId;
	private BigDecimal totalPayableAmount;
	private BigDecimal taxTotal;
	private BigDecimal totalReceivableAmount;
}
