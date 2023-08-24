package com.te.flinko.repository.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.account.CompanySalesInvoice;

public interface CompanySalesInvoiceRepository extends JpaRepository<CompanySalesInvoice, Long> {
	
	
	CompanySalesInvoice findBySalesInvoiceIdAndCompanyInfoCompanyId(Long salesInvoiceId,Long companyId);

	List<CompanySalesInvoice> findByCompanyInfoCompanyId(Long companyId);


	List<CompanySalesInvoice> findByCompanyInfoCompanyIdAndSalesInvoiceId(Long companyId, Long salesInvoiceId);

}
