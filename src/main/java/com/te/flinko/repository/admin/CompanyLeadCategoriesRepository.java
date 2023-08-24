package com.te.flinko.repository.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.dto.admin.CompanyLeadCategoriesDTO;
import com.te.flinko.entity.admin.CompanyLeadCategories;

/**
 * 
 * 
 * @author Vinayak More *
 *
 *
 **/

public interface CompanyLeadCategoriesRepository extends JpaRepository<CompanyLeadCategories, Long> {

	Optional<CompanyLeadCategories> findByLeadCategoryIdAndCompanyInfoCompanyId(Long leadCategoryId, Long companyId);

	Optional<CompanyLeadCategoriesDTO> findByLeadCategoryNameAndColor(CompanyLeadCategoriesDTO companyLeadCategoriesDto,
			Long companyId);

	Optional<CompanyLeadCategoriesDTO> save(CompanyLeadCategoriesDTO companyLeadCategoriesDto);

	Optional<CompanyLeadCategories> findByLeadCategoryNameAndCompanyInfoCompanyId(String leadCategoryName,
			Long companyId);

	Optional<CompanyLeadCategories> findByColorAndCompanyInfoCompanyId(String color, Long companyId);

	boolean findByLeadCategoryName(String leadCategoryName);

	public Optional<CompanyLeadCategories> findByCompanyInfoCompanyIdAndLeadCategoryId(Long companyId,Long leadCategoryId);

	public Optional<List<CompanyLeadCategories> > findByCompanyInfoCompanyId(Long companyId);

}
