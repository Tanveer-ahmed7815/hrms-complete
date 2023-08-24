package com.te.flinko.service.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.te.flinko.dto.admin.TermsAndConditionDTO;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyTermsAndConditions;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admin.CompanyTermsAndConditionsRepository;

@Service
public class TermsAndConditionServiceImpl implements TermsAndConditionService {

	@Autowired
	private CompanyTermsAndConditionsRepository companyTermsAndConditionsRepository;

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	@Override
	@Transactional
	public TermsAndConditionDTO addTermsAndCondition(TermsAndConditionDTO termsAndConditionDTO, Long companyId) {
		CompanyInfo companyInfo = companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company Not Found"));

		CompanyTermsAndConditions companyTermsAndConditions = new CompanyTermsAndConditions();
		if (termsAndConditionDTO.getTermsAndConditionId() == null) {
			List<CompanyTermsAndConditions> companyTermsAndConditionsList = companyTermsAndConditionsRepository
					.findByCompanyInfoCompanyIdAndTypeIgnoreCase(companyId, termsAndConditionDTO.getType());
			if (!companyTermsAndConditionsList.isEmpty()) {
				throw new DataNotFoundException("Terms and Condition Already Exists");
			}
		}
		BeanUtils.copyProperties(termsAndConditionDTO, companyTermsAndConditions);
		companyTermsAndConditions.setCompanyInfo(companyInfo);
		companyTermsAndConditions = companyTermsAndConditionsRepository.save(companyTermsAndConditions);
		BeanUtils.copyProperties(companyTermsAndConditions, termsAndConditionDTO);
		return termsAndConditionDTO;
	}

	@Override
	public List<TermsAndConditionDTO> getAllTermsAndConditions(Long companyId) {
		CompanyInfo companyInfo = companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company Not Found"));
		List<CompanyTermsAndConditions> companyTermsAndConditionsList = companyInfo.getCompanyTermsAndConditionsList();
		return companyTermsAndConditionsList.stream()
				.map(termsAndCondition -> 
					new TermsAndConditionDTO(termsAndCondition.getTermsAndConditionId(),
							termsAndCondition.getType(), termsAndCondition.getDescription())
				).collect(Collectors.toList());
	}
}
