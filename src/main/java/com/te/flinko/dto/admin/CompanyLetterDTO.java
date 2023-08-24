package com.te.flinko.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompanyLetterDTO {
	
	private String type;
	
	private String url;
	
	private byte[] fileUrl;

}
