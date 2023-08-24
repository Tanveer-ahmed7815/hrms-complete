package com.te.flinko.service;

import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.CurrencyConvertDTO;
import com.te.flinko.entity.account.mongo.CurrencyDetails;
import com.te.flinko.repository.account.mongo.CurrencyConvertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencySymbolServiceImpl implements CurrencySymbolService {

	private final CurrencyConvertRepository currencyConvertRepository;

	private String base;

	@Override
	public CurrencyConvertDTO getSymbol(HttpServletRequest request) {
		Locale locale = request.getLocale();
		Currency instance = Currency.getInstance(locale);
		CurrencyDetails currencyDetails = currencyConvertRepository.findAll().stream().filter(Objects::nonNull)
				.map(x -> {
					x.getCurrency().keySet().forEach(p -> base = p);
					return x.getCurrency().values().stream().flatMap(Collection::stream)
							.filter(v -> v.getCurrencyCode().equals(instance.getCurrencyCode())).findFirst()
							.orElseGet(CurrencyDetails::new);
				}).findFirst().orElseThrow();
		return CurrencyConvertDTO.builder().base(base).currencyDetails(currencyDetails).build();
	}

}
