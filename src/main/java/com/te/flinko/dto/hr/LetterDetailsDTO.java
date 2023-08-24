package com.te.flinko.dto.hr;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterDetailsDTO {

	private Long employeeInfoId;

	private String type;

	private String url;

	private LocalDate issuedDate;

}
