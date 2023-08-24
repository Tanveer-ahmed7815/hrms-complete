package com.te.flinko.entity.account.mongo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
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
@Document("fa_currency_convert")
public class CurrencyConvert implements Serializable{
	
	@Id
	private String currencyObjectId;
	
	@Field("cc_currency")
	private Map<String, List<CurrencyDetails>> currency;

}
