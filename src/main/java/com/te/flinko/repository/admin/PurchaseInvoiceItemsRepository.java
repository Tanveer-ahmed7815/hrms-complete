package com.te.flinko.repository.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.account.PurchaseInvoiceItems;

public interface PurchaseInvoiceItemsRepository extends JpaRepository<PurchaseInvoiceItems, Long>{

	List<PurchaseInvoiceItems> findByCompanyPurchaseInvoicePurchaseInvoiceId(Long purchaseInvoiceId);

	List<PurchaseInvoiceItems> findByPurchaseOrderItemsCompanyPurchaseOrderPurchaseOrderId(Long purchaseOrderId);


}
