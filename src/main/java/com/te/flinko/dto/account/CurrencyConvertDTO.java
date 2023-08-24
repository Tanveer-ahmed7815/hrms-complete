package com.te.flinko.dto.account;

import com.te.flinko.entity.account.mongo.CurrencyDetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConvertDTO {
	
	private String base;
	
	private CurrencyDetails currencyDetails;
	
}
