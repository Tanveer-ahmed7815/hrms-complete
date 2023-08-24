package com.te.flinko.repository.admindept;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.account.CompanySalesOrder;

public interface CompanySalesOrderRepository extends JpaRepository<CompanySalesOrder, Long> {

	Optional<CompanySalesOrder> findBySalesOrderIdAndCompanyInfoCompanyId(Long salesOrderId, Long companyId);


	List<CompanySalesOrder> findByCompanyInfoCompanyId(Long companyId);

	List<CompanySalesOrder> findBySubject(String subject);

	List<CompanySalesOrder> findBySubjectAndCompanyInfoCompanyId(String subject,Long companyId);

  

}
