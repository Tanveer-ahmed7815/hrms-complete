package com.te.flinko.service.admin;

import java.util.List;

import com.te.flinko.dto.admin.TermsAndConditionDTO;

public interface TermsAndConditionService {
	
	TermsAndConditionDTO addTermsAndCondition(TermsAndConditionDTO termsAndConditionDTO, Long companyId);
	
	List<TermsAndConditionDTO> getAllTermsAndConditions(Long companyId);

}
