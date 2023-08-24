package com.te.flinko.dto.hr;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class PassAndVisaDTO {
	
	private String passportNumber;
	private LocalDate passportExpiryDate;
	private List<AddVisaInfoDTO> visaInfo;
	
}
