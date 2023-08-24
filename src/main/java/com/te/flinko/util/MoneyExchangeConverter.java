package com.te.flinko.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.te.flinko.dto.account.ConversionRateDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoneyExchangeConverter {

	private static final String INR = "INR";

	private MoneyExchangeConverter() {
	}

	public static ConversionRateDTO moneyRates() {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");

		Map<String, String> params = new HashMap<>();
		params.put("apikey", "05745ee937ca49abacae60ba11cc1c72");

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

		String url = "https://api.currencyfreaks.com/latest?apikey=05745ee937ca49abacae60ba11cc1c72";

		ResponseEntity<ConversionRateDTO> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity,
				ConversionRateDTO.class);
		return exchange.getBody();
	}

	public static String moneyConversion(HttpServletRequest request) {
		Locale locale = request.getLocale();
		log.info("Current Country ",locale.getDisplayCountry());
		ConversionRateDTO moneyRates = moneyRates();
		if (moneyRates == null) {
			return "";
		}
		Map<String, BigDecimal> rates = moneyRates.getRates();

		rates.put(moneyRates.getBase(), BigDecimal.ONE);
		double inr = rates.get(INR).doubleValue();
		moneyRates.setBase(INR);
		double payment = 1000.5;
		Map<String, BigDecimal> newRate = rates.entrySet().stream().map(r -> {
			r.setValue(BigDecimal.valueOf(r.getValue().doubleValue() / inr));
			return r;
		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		locale = Locale.US;
		Currency currency = NumberFormat.getCurrencyInstance(locale).getCurrency();
		if (moneyRates.getBase().equalsIgnoreCase(currency.toString())) {
			return NumberFormat.getCurrencyInstance(locale).format(payment);
		}
		
		BigDecimal bigDecimal = newRate.get(currency.toString());
		return NumberFormat.getCurrencyInstance(locale).format(bigDecimal.multiply(BigDecimal.valueOf(payment)));
	}
}
