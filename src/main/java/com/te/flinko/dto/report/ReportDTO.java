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
public class ReportDTO implements Serializable {
	private Long reportId;
	private String reportFormat;
	private Map<String, Object> parameters;
	private String url;
}
