package com.te.flinko.entity.account.mongo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyDetails implements Serializable{
	
	@Field("cc_dispaly_name")
	private String displayName;
	
	@Field("cc_currency_code")
	private String currencyCode;
	
	@Field("cc_amount_difference")
	private BigDecimal amountDifference;
	
	@Field("cc_currency_symbol")
	private String currencySymbol;

}
