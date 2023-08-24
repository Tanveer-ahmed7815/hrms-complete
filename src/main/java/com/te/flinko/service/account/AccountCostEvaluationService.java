package com.te.flinko.service.account;

import java.math.BigDecimal;
import java.util.Map;

import com.te.flinko.dto.account.AccountCostEvaluationDTO;

public interface AccountCostEvaluationService {
	public Map<String, BigDecimal> getAccountCostEvaluation(int year,Long companyId);
	
	public String addAccountCostEvaluation(AccountCostEvaluationDTO accountCostEvaluationDTO,Long companyId);
	
	public AccountCostEvaluationDTO getSingleCostEvaluation(String cat,Long companyId);
}
