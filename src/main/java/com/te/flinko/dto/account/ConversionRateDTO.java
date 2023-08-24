package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ConversionRateDTO {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss+mm")
	private LocalDateTime date;

	private String base;

	private Map<String, BigDecimal> rates;
}
