package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.te.flinko.dto.account.mongo.ContactPerson;
import com.te.flinko.dto.account.mongo.VendorAddress;
import com.te.flinko.dto.account.mongo.VendorBankDetails;

import lombok.Data;

@Data
public class VendorDetailsDTO {

	private String id;

	private String vendorInfoId;

	private Long companyId;

	private String vendorName;

	private List<@Valid ContactPerson> contactPersons;

	private List<VendorAddress> vendorAddress;

	private List<VendorBankDetails> vendorBankDetails;

	private Map<String, String> otherDetails;

	private BigDecimal amountPaid;

	private BigDecimal amountToBePaid;

	private String modeOfPayment;

	private LocalDate paymentDate;

}
