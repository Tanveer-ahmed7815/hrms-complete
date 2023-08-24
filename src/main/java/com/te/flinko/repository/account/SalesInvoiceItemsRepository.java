package com.te.flinko.repository.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.account.SalesInvoiceItems;

public interface SalesInvoiceItemsRepository extends JpaRepository<SalesInvoiceItems, Long> {
	
	List<SalesInvoiceItems> findBySalesOrderItemsSaleItemId(Long saleItemId);

}
