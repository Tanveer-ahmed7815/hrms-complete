package com.te.flinko.dto.hr;


import java.math.BigInteger;

import javax.validation.constraints.Max;

import lombok.Data;

@Data
public class EditInterviewRoundDto {
	private Integer oldInterviewRoundId;
	private String oldInterviewRoundName;
   
	private BigInteger newInterviewRoundId;
	private String newInterviewRoundName;
	private Long companyId;
}
