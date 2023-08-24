package com.te.flinko.repository.it;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.it.CompanyHardwareItems;

@Repository
public interface ITOtherItemRepository extends JpaRepository<CompanyHardwareItems, String> {

	List<CompanyHardwareItems> findByCompanyInfoCompanyId(Long companyId);

	Optional<CompanyHardwareItems> findByIndentificationNumberAndCompanyInfoCompanyId(String indentificationNumber,
			Long companyId);
	
	List<CompanyHardwareItems> findByCompanyInfoCompanyIdAndIsWorking(Long companyId,Boolean isWorking);

}
