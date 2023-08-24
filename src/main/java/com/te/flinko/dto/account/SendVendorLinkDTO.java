package com.te.flinko.dto.account;

import lombok.Data;

@Data
public class SendVendorLinkDTO {
	
	private String email;
	
	private Long companyId;
	
	private String url;

}
