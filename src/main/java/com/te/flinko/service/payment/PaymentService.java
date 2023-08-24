package com.te.flinko.service.payment;

import javax.servlet.http.HttpServletRequest;

import com.te.flinko.dto.admin.PlanDTO;
import com.te.flinko.dto.payment.PaymentRequestDTO;
import com.te.flinko.dto.payment.RazorpayResponseDTO;

public interface PaymentService {
	
	String getOrderId(PaymentRequestDTO paymentRequestDTO);
	
	PlanDTO calculateAmount(String termonalId, PlanDTO planDto,HttpServletRequest request);
	
	boolean verifySignature(RazorpayResponseDTO razorpayResponseDTO,HttpServletRequest request);

}
