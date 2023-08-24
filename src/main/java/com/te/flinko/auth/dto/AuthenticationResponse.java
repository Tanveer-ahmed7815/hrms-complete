package com.te.flinko.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
	
	private String employeeId;
	
	private Long companyId;
	
	private Long employeeInfoId;
	
	private String accessToken;

	private String refreshToken;
}
