package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class VendorBasicDetailsDTO {

	private String id;

	private Long vendorInfoId;

	private String vendorName;

	private String emailId;

	private Long mobileNumber;

	private String contactPersonName;
	
	private BigDecimal amountPaid;

	private BigDecimal amountToBePaid;

	private String modeOfPayment;

	private LocalDate paymentDate;

}
