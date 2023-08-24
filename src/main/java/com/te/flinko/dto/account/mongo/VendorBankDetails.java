package com.te.flinko.dto.account.mongo;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Builder;
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Builder
public class VendorBankDetails implements Serializable {

	@Field("bank_name")
	private String bankName;

	@Field("bank_branch")
	private String bankBranch;

	@Field("account_type")
	private String accountType;

	@Field("account_number")
	private Long accountNumber;

	@Field("ifsc_code")
	private String ifscCode;

	@Field("account_holder_name")
	private String accountHolderName;

}
