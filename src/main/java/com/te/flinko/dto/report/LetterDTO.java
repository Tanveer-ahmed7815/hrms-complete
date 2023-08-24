package com.te.flinko.dto.report;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterDTO implements Serializable {
	private Long latterId;
	private String latterName;
	private Map<String, String> documents;
	private String employeeId;
	private Long companyId;
	private String content;
}
