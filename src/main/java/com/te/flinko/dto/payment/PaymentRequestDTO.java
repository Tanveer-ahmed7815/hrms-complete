package com.te.flinko.dto.payment;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentRequestDTO {

	private BigDecimal amount;

	private String currency;
	
	private Long companyId;

	private String planName;

}
