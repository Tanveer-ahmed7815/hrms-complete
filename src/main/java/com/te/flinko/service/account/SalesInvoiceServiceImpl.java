package com.te.flinko.service.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.ClientContactPersonsDTO;
import com.te.flinko.dto.account.SalesInvoiceDetailsDTO;
import com.te.flinko.dto.account.SalesInvoiceOrderDropdownDTO;
import com.te.flinko.dto.account.SalesOrderDropdownDTO;
import com.te.flinko.dto.account.SalesOrderItemDropdownDTO;
import com.te.flinko.dto.account.SalesShippingBillingAddressDTO;
import com.te.flinko.entity.account.CompanySalesInvoice;
import com.te.flinko.entity.account.CompanySalesOrder;
import com.te.flinko.entity.account.SalesBillingShippingAddress;
import com.te.flinko.entity.account.SalesInvoiceItems;
import com.te.flinko.entity.account.SalesOrderItems;
import com.te.flinko.entity.sales.ClientContactPersonDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.account.CompanySalesInvoiceRepository;
import com.te.flinko.repository.account.SalesBillingShippingAddressRepository;
import com.te.flinko.repository.account.SalesInvoiceItemsRepository;
import com.te.flinko.repository.admindept.CompanySalesOrderRepository;
import com.te.flinko.repository.admindept.SalesOrderItemsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

	@Autowired
	private CompanySalesOrderRepository companySalesOrderRepository;

	@Autowired
	private SalesOrderItemsRepository salesOrderItemsRepository;

	@Autowired
	private CompanySalesInvoiceRepository companySalesInvoiceRepository;

	@Autowired
	private SalesBillingShippingAddressRepository salesBillingShippingAddressRepository;

	@Autowired
	private SalesInvoiceItemsRepository salesInvoiceItemsRepository;

	@Override
	public SalesInvoiceDetailsDTO saveInvoiceDetails(Long getUserId, Long getCompanyId,
			SalesInvoiceDetailsDTO invoiceDetailsDTO) {

		CompanySalesInvoice companySalesInvoice = new CompanySalesInvoice();

		if (invoiceDetailsDTO.getSalesInvoiceId() != null) {
			companySalesInvoice = companySalesInvoiceRepository
					.findBySalesInvoiceIdAndCompanyInfoCompanyId(invoiceDetailsDTO.getSalesInvoiceId(), getCompanyId);

		} else {

//			companySalesInvoice = new CompanySalesInvoice();
		}

		BeanUtils.copyProperties(invoiceDetailsDTO, companySalesInvoice);
		Optional<CompanySalesOrder> optional = companySalesOrderRepository
				.findBySalesOrderIdAndCompanyInfoCompanyId(invoiceDetailsDTO.getSalesOrder(), getCompanyId);

		if (!optional.isPresent()) {
			log.error("Info Not Found");
			throw new DataNotFoundException("Info Not Found");
		} else {

			companySalesInvoice.setStatus("Pending");
			companySalesInvoice.setCompanySalesOrder(optional.get());
			companySalesInvoice.setCompanyInfo(optional.get().getCompanyInfo());
			BeanUtils.copyProperties(companySalesInvoiceRepository.save(companySalesInvoice), invoiceDetailsDTO);
			log.info("Saved Successfully");
			return invoiceDetailsDTO;
		}
	}

	@Override
	public SalesInvoiceDetailsDTO addAttachments(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO,
			Long salesInvoiceId) {

		CompanySalesInvoice salesInvoiceDetails = companySalesInvoiceRepository
				.findBySalesInvoiceIdAndCompanyInfoCompanyId(salesInvoiceId, getCompanyId);

		if (salesInvoiceDetails == null) {
			log.error("SalesInvoiceId Not Found");
			throw new DataNotFoundException("SalesInvoiceId Not Found");
		} else {
			salesInvoiceDetails.setAttachments(invoiceDetailsDTO.getAttachments());
			BeanUtils.copyProperties(companySalesInvoiceRepository.save(salesInvoiceDetails), invoiceDetailsDTO);
			log.info("Attachment Saved Successfully");
			return invoiceDetailsDTO;
		}
	}

	@Override
	public SalesInvoiceDetailsDTO addTermsAndConditions(Long getUserId, Long getCompanyId,
			SalesInvoiceDetailsDTO invoiceDetailsDTO, Long salesInvoiceId) {
		CompanySalesInvoice salesInvoiceDetails = companySalesInvoiceRepository.findBySalesInvoiceIdAndCompanyInfoCompanyId(salesInvoiceId, getCompanyId);

		if (salesInvoiceDetails==null) {
			log.error("SalesInvoiceId Not Found");
			throw new DataNotFoundException("SalesInvoiceId Not Found");
		} else {
			invoiceDetailsDTO.setTermsAndConditions(invoiceDetailsDTO.getTermsAndConditions());
			BeanUtils.copyProperties(companySalesInvoiceRepository.save(salesInvoiceDetails), invoiceDetailsDTO);
			log.info("TermsAndConditions Saved Successfully");
			return invoiceDetailsDTO;
		}
	}

	@Override
	public SalesInvoiceDetailsDTO adddescription(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO,
			Long salesInvoiceId) {
		CompanySalesInvoice salesInvoiceDetails = companySalesInvoiceRepository.findBySalesInvoiceIdAndCompanyInfoCompanyId(salesInvoiceId,getCompanyId);

		if (salesInvoiceDetails==null) {
			log.error("SalesInvoiceId Not Found");
			throw new DataNotFoundException("SalesInvoiceId Not Found");
		} else {
			salesInvoiceDetails.setDescription(invoiceDetailsDTO.getDescription());
			BeanUtils.copyProperties(companySalesInvoiceRepository.save(salesInvoiceDetails), invoiceDetailsDTO);
			log.info("TermsAndConditions Saved Successfully");
			return invoiceDetailsDTO;
		}
	}

	@Override
	public SalesInvoiceDetailsDTO saveInvoiceItems(Long getUserId, Long getCompanyId, SalesInvoiceDetailsDTO invoiceDetailsDTO,
			Long salesInvoiceId) {

		CompanySalesInvoice salesInvoiceDetails = companySalesInvoiceRepository.findBySalesInvoiceIdAndCompanyInfoCompanyId(salesInvoiceId,getCompanyId);

		if (salesInvoiceDetails==null) {
			log.error("SalesInvoiceId Not Found");
			throw new DataNotFoundException("SalesInvoiceId Not Found");
		} else {
			salesInvoiceDetails.setAdjustment(invoiceDetailsDTO.getAdjustment());
			salesInvoiceDetails.setDiscountTotal(invoiceDetailsDTO.getDiscountTotal());
			salesInvoiceDetails.setSubTotal(invoiceDetailsDTO.getSubTotal());
			salesInvoiceDetails.setTaxTotal(invoiceDetailsDTO.getTaxTotal());
			salesInvoiceDetails.setTotalReceivableAmount(invoiceDetailsDTO.getTotalReceivableAmount());
			BeanUtils.copyProperties(companySalesInvoiceRepository.save(salesInvoiceDetails), invoiceDetailsDTO);
			log.info("Invoice Items Details Saved Successfully");
			return invoiceDetailsDTO;
		}
	}

	@Override
	public List<SalesInvoiceOrderDropdownDTO> getSalesOrderId(Long companyId) {
		List<CompanySalesOrder> companySalesOrderInfoList = companySalesOrderRepository
				.findByCompanyInfoCompanyId(companyId);
		List<SalesInvoiceOrderDropdownDTO> salesOrderDropdownDTOList = new ArrayList<>();
		if (!companySalesOrderInfoList.isEmpty()) {

			for (CompanySalesOrder companySalesOrderInfo : companySalesOrderInfoList) {

				SalesInvoiceOrderDropdownDTO dto = new SalesInvoiceOrderDropdownDTO();

				dto.setSalesOrderId(companySalesOrderInfo.getSalesOrderId());
				dto.setClientId(companySalesOrderInfo.getCompanyClientInfo().getClientId());
				dto.setClientName(companySalesOrderInfo.getCompanyClientInfo().getClientName());
				dto.setSubject(companySalesOrderInfo.getSubject());

				List<ClientContactPersonsDTO> clientContactPersonsDTOList = new ArrayList<>();

				ClientContactPersonDetails clientContactPersonDetails = companySalesOrderInfo
						.getClientContactPersonDetails();
				ClientContactPersonsDTO clientContactPersonsDTO = new ClientContactPersonsDTO();
				clientContactPersonsDTO.setContactPersonId(clientContactPersonDetails.getContactPersonId());
				clientContactPersonsDTO.setContactPersonName(
						clientContactPersonDetails.getFirstName() + " " + clientContactPersonDetails.getLastName());
				clientContactPersonsDTO.setDesignation(clientContactPersonDetails.getDesignation());
				clientContactPersonsDTO.setEmailId(clientContactPersonDetails.getEmailId());
				clientContactPersonsDTO.setMobileNumber(clientContactPersonDetails.getMobileNumber());

				clientContactPersonsDTOList.add(clientContactPersonsDTO);
				dto.setContactPersons(clientContactPersonsDTOList);
				salesOrderDropdownDTOList.add(dto);
			}

			return salesOrderDropdownDTOList;
		}
		return salesOrderDropdownDTOList;

	}

	@Override
	public List<SalesShippingBillingAddressDTO> saveAddressInfo(Long getUserId, Long getCompanyId,
			List<SalesShippingBillingAddressDTO> salesShippingBillingAddressDTOList, Long salesInvoiceId) {

		Optional<CompanySalesOrder> optional = companySalesOrderRepository
				.findBySalesOrderIdAndCompanyInfoCompanyId(salesInvoiceId, getCompanyId);

		List<SalesShippingBillingAddressDTO> list = new ArrayList<>();

		for (SalesShippingBillingAddressDTO salesShippingBillingAddressDTO : salesShippingBillingAddressDTOList) {

			SalesBillingShippingAddress address = new SalesBillingShippingAddress();
			SalesShippingBillingAddressDTO addressInformationDTO = new SalesShippingBillingAddressDTO();
			BeanUtils.copyProperties(salesShippingBillingAddressDTO, address);
			address.setCompanySalesOrder(optional.get());
			SalesBillingShippingAddress save = salesBillingShippingAddressRepository.save(address);
			BeanUtils.copyProperties(save, addressInformationDTO);
			list.add(addressInformationDTO);
		}
		return list;

	}

	@Override
	public List<SalesOrderItemDropdownDTO> getSalesOrderItemsList(Long salesOrderId) {

		List<SalesOrderItems> salesOrderItemsList = salesOrderItemsRepository
				.findByCompanySalesOrderSalesOrderId(salesOrderId);

		List<SalesOrderItemDropdownDTO> list = new ArrayList<>();

		if (!salesOrderItemsList.isEmpty()) {

			for (SalesOrderItems salesOrderItem : salesOrderItemsList) {

				List<SalesInvoiceItems> salesInvoiceItemsDetailsList = salesInvoiceItemsRepository
						.findBySalesOrderItemsSaleItemId(salesOrderItem.getSaleItemId());
				Long totalInvoiceQuantity = (long) 0;
				for (SalesInvoiceItems salesInvoiceItemsDetails : salesInvoiceItemsDetailsList) {
					totalInvoiceQuantity = totalInvoiceQuantity + salesInvoiceItemsDetails.getQuantity();
				}
				Long orderQuantity = salesOrderItem.getQuantity();
				Long pendingQuantity = orderQuantity - totalInvoiceQuantity;

				if (pendingQuantity > 0) {

					SalesOrderItemDropdownDTO salesOrderItemDropdown = new SalesOrderItemDropdownDTO();
					BeanUtils.copyProperties(salesOrderItem, salesOrderItemDropdown);
					salesOrderItemDropdown.setQuantity(pendingQuantity);

					list.add(salesOrderItemDropdown);
				}

//				Long invoiceQuantity = salesInvoiceItemsDetails.getQuantity();
//				Long orderQuantity = salesOrderItem.getQuantity();
//				Long pendingQuantity = orderQuantity - invoiceQuantity;
//				
//
//				if (pendingQuantity>0) {
//
//					SalesOrderItemDropdownDTO salesOrderItemDropdown = new SalesOrderItemDropdownDTO();
//					BeanUtils.copyProperties(salesOrderItem, salesOrderItemDropdown);
//					salesOrderItemDropdown.setQuantity(pendingQuantity);
//					
//					
//					list.add(salesOrderItemDropdown);
//				}

			}
		}
		return list;
	}

	@Override
	public List<SalesOrderItemDropdownDTO> saveSalesOrderItems(Long getCompanyId, Long salesInvoiceId,
			List<SalesOrderItemDropdownDTO> salesOrderItemDropdownDTOList) {

		List<SalesOrderItemDropdownDTO> list = new ArrayList<>();

		CompanySalesInvoice optionalSalesInvoiceDetails = companySalesInvoiceRepository
				.findBySalesInvoiceIdAndCompanyInfoCompanyId(salesInvoiceId,getCompanyId);
		
		
		
		
		
		
		
		
		for (SalesOrderItemDropdownDTO salesOrderItemDropdownDTO : salesOrderItemDropdownDTOList) {
			Optional<SalesOrderItems> optional = salesOrderItemsRepository
					.findById(salesOrderItemDropdownDTO.getSaleItemId());
			Long saleItemId = optional.get().getSaleItemId();
		
			
			SalesInvoiceItems invoiceItems = new SalesInvoiceItems();
			SalesOrderItemDropdownDTO dto = new SalesOrderItemDropdownDTO();
			BeanUtils.copyProperties(salesOrderItemDropdownDTO, invoiceItems);
			invoiceItems.setSalesOrderItems(optional.get());
			invoiceItems.setCompanySalesInvoice(optionalSalesInvoiceDetails);
			SalesInvoiceItems save = salesInvoiceItemsRepository.save(invoiceItems);
			BeanUtils.copyProperties(save, dto);
			list.add(dto);

		}
		return list;
	}

}
