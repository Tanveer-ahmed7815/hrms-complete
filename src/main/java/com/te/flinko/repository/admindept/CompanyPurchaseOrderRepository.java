package com.te.flinko.repository.admindept;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.admin.CompanyInfo;

@Repository
public interface CompanyPurchaseOrderRepository extends JpaRepository<CompanyPurchaseOrder, Long> {

	Optional<CompanyInfo> findByPurchaseOrderIdAndCompanyInfoCompanyId(Long purchaseOrderId, Long companyId);

	List<CompanyPurchaseOrder> findByCompanyInfoCompanyId(Long companyId);
	
	List<CompanyPurchaseOrder> findByCompanyInfoCompanyIdAndPurchaseType(Long companyId, String purchaseType);
	
	List<CompanyPurchaseOrder> findByCompanyInfoCompanyIdAndTotalPayableAmountNotNull(Long companyId);

	List<CompanyPurchaseOrder> findByVendorIdAndSubject(String vendorInfoId, String subject);

	List<CompanyPurchaseOrder> findBySubject(String subject);
	
	List<CompanyPurchaseOrder> findBySubjectAndCompanyInfoCompanyId(String subject,Long companyId);

	
//	@Query(value="select com.te.flinko.dto.account.SalesOrderDropdownDTO(epi.subject)from CompanyPurchaseOrder epi where epi.companyInfo.companyId=?1")
//	List<CompanyPurchaseOrder> findByCompanyIdQuery(Long companyId);


// Optional<PurchaseBillingShippingAddress> findByPurchaseBillingShippingAddressPurchaseAddressIdAndAddressType(Long purchaseAddressId, String addressType);

}
