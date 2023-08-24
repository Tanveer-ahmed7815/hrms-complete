package com.te.flinko.dto.payment;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazorpayResponseDTO {
	
	private String razorpayPaymentId;

	private String razorpayOrderId;

	private String razorpaySignature;

	private String orderId;
	
	private String url;
	
	private String planName;
	
	private Long companyId;
	
	private BigDecimal totalAmount;

}
