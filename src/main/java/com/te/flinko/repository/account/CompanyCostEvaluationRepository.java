package com.te.flinko.repository.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.account.CompanyCostEvaluation;

public interface CompanyCostEvaluationRepository extends JpaRepository<CompanyCostEvaluation, Long> {

	List<CompanyCostEvaluation> findByCompanyInfoCompanyId(Long companyId);

	Optional<CompanyCostEvaluation> findByCostEvaluationIdAndCategoryIgnoreCase(Long companyId, String category);
	
	Optional<CompanyCostEvaluation> findByCompanyInfoCompanyIdAndCategoryIgnoreCase(Long companyId, String category);

}
