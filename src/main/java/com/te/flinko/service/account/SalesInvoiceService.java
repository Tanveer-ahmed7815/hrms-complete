package com.te.flinko.service.account;

import java.util.List;

import com.te.flinko.dto.account.InvoiceDetailsDTO;
import com.te.flinko.dto.account.SalesInvoiceDetailsDTO;
import com.te.flinko.dto.account.SalesInvoiceOrderDropdownDTO;
import com.te.flinko.dto.account.SalesOrderItemDropdownDTO;
import com.te.flinko.dto.account.SalesShippingBillingAddressDTO;

public interface SalesInvoiceService {

	public SalesInvoiceDetailsDTO saveInvoiceDetails(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO);

	public SalesInvoiceDetailsDTO addAttachments(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO,
			Long salesInvoiceId);

	public SalesInvoiceDetailsDTO addTermsAndConditions(Long getUserId, Long getCompanyId,
			SalesInvoiceDetailsDTO invoiceDetailsDTO, Long salesInvoiceId);

	public SalesInvoiceDetailsDTO adddescription(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO,
			Long salesInvoiceId);

	public SalesInvoiceDetailsDTO saveInvoiceItems(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO,
			Long salesInvoiceId);

	public List<SalesInvoiceOrderDropdownDTO> getSalesOrderId(Long companyId);

	public List<SalesShippingBillingAddressDTO> saveAddressInfo(Long getUserId, Long getCompanyId,
			List<SalesShippingBillingAddressDTO> salesShippingBillingAddressDTOList, Long salesInvoiceId);

	public List<SalesOrderItemDropdownDTO> getSalesOrderItemsList(Long salesOrderId);

	public List<SalesOrderItemDropdownDTO> saveSalesOrderItems(Long getCompanyId,Long salesInvoiceId,List<SalesOrderItemDropdownDTO> salesOrderItemDropdownDTOList);

}
