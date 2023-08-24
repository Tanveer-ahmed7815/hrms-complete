package com.te.flinko.service;

import javax.servlet.http.HttpServletRequest;

import com.te.flinko.dto.account.CurrencyConvertDTO;

public interface CurrencySymbolService {
	
	CurrencyConvertDTO getSymbol(HttpServletRequest request);

}
