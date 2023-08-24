package com.te.flinko.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.admin.CompanyTermsAndConditions;

@Repository
public interface CompanyTermsAndConditionsRepository extends JpaRepository<CompanyTermsAndConditions, Long>{
	
	List<CompanyTermsAndConditions> findByCompanyInfoCompanyIdAndTypeIgnoreCase(Long companyId, String type);

}
