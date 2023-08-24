package com.te.flinko.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientContactPersonsDTO {

	private Long contactPersonId;
	
	private String contactPersonName;
	
	private String designation;
	
	private String emailId;
	
	private String mobileNumber;
}
