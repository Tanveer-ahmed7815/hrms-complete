package com.te.flinko.service.account;

import java.util.List;

import com.te.flinko.dto.account.*;
import com.te.flinko.dto.admin.TermsAndConditionDTO;

public interface AccountDepartmentService {
	Long createUpdatePurchaseOrder(CompanyPurchaseOrderDTO companyPurchaseOrderDTO, Long companyId);

	Long addPurchaseBillingShippingAddress(BillingShippingAddressDTO billingShippingAddressDTO, Long purchaseOrderId);

	PurchaseItemsDTO addPurchasedItems(PurchaseItemsDTO purchaseItemsDTO, Long purchaseOrderId);

	Long createUpdateSalesOrder(CompanySalesOrderDTO companySalesOrderDTO, Long companyId);

	Long addSalesBillingShippingAddress(BillingShippingAddressDTO billingShippingAddressDTO, Long salesOrderId);

	SalesItemsDTO addOrderedItems(SalesItemsDTO salesItemsDTO, Long salesOrderId);

	WorkOrderDTO createWorkOrder(WorkOrderDTO workOrderDTO, Long companyId);

	List<PurchasedOrderDisplayDTO> getAllPurchaseOrder(Long companyId);
	
	CompanyPurchaseOrderDTO getPurchaseOrderDetailsById(Long purcahseOrderId);

	List<PurchasedOrderDisplayDTO> getAllSalesDetails(Long companyId);

	CompanyPurchaseOrderDTO getSalesDetailsById(Long salesOrderId);
	
	public String updateDescriptionOfPurchaseOrder(AccountDescriptionDTO accountDescriptionDTO,Long employeeInfoId);
	
	public String updateDescriptionOfSalesOrder(AccountDescriptionDTO accountDescriptionDTO,Long employeeInfoId);
	
	List<PurchasedOrderDisplayDTO> getAllPurchaseInventory(Long companyId);
	
	TermsAndConditionDTO getTermsAndConditionDetails(Long companyId, TermsAndConditionDTO termsAndConditionDTO);

}
