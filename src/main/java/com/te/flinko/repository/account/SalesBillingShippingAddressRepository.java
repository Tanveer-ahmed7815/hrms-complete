package com.te.flinko.repository.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.account.SalesBillingShippingAddress;

@Repository
public interface SalesBillingShippingAddressRepository extends JpaRepository<SalesBillingShippingAddress, Long> {
	void deleteByCompanySalesOrderSalesOrderId(Long purchaseOrderId);

	List<SalesBillingShippingAddress> findByCompanySalesOrderSalesOrderId(Long salesOrderId);
}
