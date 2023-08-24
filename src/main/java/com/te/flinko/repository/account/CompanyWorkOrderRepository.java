package com.te.flinko.repository.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.account.CompanyWorkOrder;

public interface CompanyWorkOrderRepository extends JpaRepository<CompanyWorkOrder, Long> {

	List<CompanyWorkOrder> findByCompanyInfoCompanyId(Long companyId);
	
	Optional<CompanyWorkOrder> findByCompanyInfoCompanyIdAndWorkOrderId(Long companyId,Long workOrderId);
}
