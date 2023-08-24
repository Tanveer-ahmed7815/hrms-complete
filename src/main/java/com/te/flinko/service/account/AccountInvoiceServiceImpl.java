package com.te.flinko.service.account;

import static com.te.flinko.common.account.AccountConstants.PURCHASE_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_ADDRESS_DETAILS_NOT_EXISTS;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_ATTACHMENT_DETAILS_SAVED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_DESCRIPTION_DETAILS_SAVED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY;
import static com.te.flinko.common.hr.HrConstants.COMPANY_INFORMATION_NOT_PRESENT;
import static com.te.flinko.constant.admindept.AdminDeptConstants.PURCHASE_INVOCE_DETAILS_NOT_EXIXTS;
import static com.te.flinko.constant.admindept.AdminDeptConstants.PURCHASE_ORDER_DOES_NOT_EXISTS;
import static com.te.flinko.common.account.AccountConstants.SALES_ORDER_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.SALES_INVOICE_DETAILS_NOT_FOUND;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.AddressInformationDTO;
import com.te.flinko.dto.account.AttachmentsDTO;
import com.te.flinko.dto.account.DescriptionDTO;
import com.te.flinko.dto.account.InvoiceDetailsDTO;
import com.te.flinko.dto.account.InvoiceItemDTO;
import com.te.flinko.dto.account.InvoiceItemsDTO;
import com.te.flinko.dto.account.PurchaseInvoiceDTO;
import com.te.flinko.dto.account.PurchaseInvoiceDetailsByIdDto;
import com.te.flinko.dto.account.SalesInvoiceDTO;
import com.te.flinko.dto.account.SalesOrderDropdownDTO;
import com.te.flinko.entity.account.CompanyPurchaseInvoice;
import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.account.CompanySalesInvoice;
import com.te.flinko.entity.account.CompanySalesOrder;
import com.te.flinko.entity.account.PurchaseBillingShippingAddress;
import com.te.flinko.entity.account.PurchaseInvoiceItems;
import com.te.flinko.entity.account.PurchaseOrderItems;
import com.te.flinko.entity.account.SalesBillingShippingAddress;
import com.te.flinko.entity.account.SalesInvoiceItems;
import com.te.flinko.entity.account.SalesOrderItems;
import com.te.flinko.entity.account.mongo.CompanyVendorInfo;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.sales.ClientContactPersonDetails;
import com.te.flinko.exception.CompanyIdNotFoundException;
import com.te.flinko.exception.account.CustomExceptionForAccount;
import com.te.flinko.exception.admindept.PurchaseIdNotPresentException;
import com.te.flinko.exception.admindept.PurchaseOrderDoesNotExistsException;
import com.te.flinko.repository.account.CompanySalesInvoiceRepository;
import com.te.flinko.repository.account.CompanyVendorInfoRepository;
import com.te.flinko.repository.account.PurchaseBillingShippingAddressRepository;
import com.te.flinko.repository.account.SalesBillingShippingAddressRepository;
import com.te.flinko.repository.account.SalesInvoiceItemsRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admin.CompanyPurchaseInvoiceRepository;
import com.te.flinko.repository.admin.PurchaseInvoiceItemsRepository;
import com.te.flinko.repository.admindept.CompanyPurchaseOrderRepository;
import com.te.flinko.repository.admindept.CompanySalesOrderRepository;
import com.te.flinko.repository.admindept.PurchaseOrderItemsRepository;
import com.te.flinko.repository.admindept.SalesOrderItemsRepository;
import com.te.flinko.repository.sales.ClientContactPersonDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountInvoiceServiceImpl implements AccountInvoiceService {
	@Autowired
	private CompanyInfoRepository companyInfoRepo;
	@Autowired
	private PurchaseOrderItemsRepository purchaseOrderItemsRepo;
	@Autowired
	private CompanyPurchaseInvoiceRepository companyPurchaseInvoiceRepo;
	@Autowired
	private CompanyPurchaseOrderRepository companyPurchaseOrderRepo;
	@Autowired
	private CompanyVendorInfoRepository companyVendorInfoRepo;
	@Autowired
	private PurchaseBillingShippingAddressRepository purchaseBillingShippingAddressRepo;
	@Autowired
	private PurchaseInvoiceItemsRepository purchaseInvoiceItemsRepo;
	@Autowired
	private CompanySalesOrderRepository companySalesOrderRepo;
	@Autowired
	private CompanySalesInvoiceRepository companySalesInvoiceRepo;
	@Autowired
	private SalesBillingShippingAddressRepository salesBillingShippingAddressRepo;
	@Autowired
	private ClientContactPersonDetailsRepository contactPersonDetailsRepo;
	@Autowired
	private SalesOrderItemsRepository salesOrderItemsRepo;
	@Autowired
	private SalesInvoiceItemsRepository salesInvoiceItemsRepo;

	/**
	 * this method is use to save the invoice details accept the whole object of
	 * CompanyPurchaseInvoice
	 * 
	 * @param invoiceDetailsDto object
	 * @return updated object of InvoiceDetailsDTO
	 **/
	@Override
	@Transactional
	public InvoiceDetailsDTO invoiceDetails(InvoiceDetailsDTO invoiceDetailsDto) {
		InvoiceDetailsDTO invoiceDetailDTO = new InvoiceDetailsDTO();
		CompanyInfo companyInfo = companyInfoRepo.findById(invoiceDetailsDto.getCompanyId())
				.orElseThrow(() -> new CompanyIdNotFoundException(COMPANY_INFORMATION_NOT_PRESENT));
		List<CompanyVendorInfo> vendorInfo = companyVendorInfoRepo.findByIdAndCompanyId(invoiceDetailsDto.getVendorId(),
				invoiceDetailsDto.getCompanyId());
		if (vendorInfo == null || vendorInfo.isEmpty()) {
			throw new CustomExceptionForAccount("Vendor details not found");
		}
		List<CompanyPurchaseOrder> companyPurchaseOrderInfo = companyPurchaseOrderRepo
				.findByVendorIdAndSubject(invoiceDetailsDto.getVendorId(), invoiceDetailsDto.getSubject());
		if (companyPurchaseOrderInfo == null || companyPurchaseOrderInfo.isEmpty()) {
			log.error(PURCHASE_ORDER_DOES_NOT_EXISTS);
			throw new PurchaseOrderDoesNotExistsException(PURCHASE_ORDER_DOES_NOT_EXISTS);
		}
		log.info("company information verified successfully");
		if (invoiceDetailsDto.getPurchaseInvoiceId() != null) {
			new InvoiceDetailsDTO();
			List<CompanyPurchaseInvoice> companyPurchaseInvoices = companyPurchaseInvoiceRepo
					.findByCompanyInfoCompanyIdAndPurchaseInvoiceId(invoiceDetailsDto.getCompanyId(),
							invoiceDetailsDto.getPurchaseInvoiceId());
			if (companyPurchaseInvoices == null || companyPurchaseInvoices.isEmpty()) {
				throw new CustomExceptionForAccount("Purchase invoice details not found");
			}
			CompanyPurchaseInvoice companyPurchaseInvoice = companyPurchaseInvoices.get(0);
			BeanUtils.copyProperties(invoiceDetailsDto, companyPurchaseInvoice);
			companyPurchaseInvoice.setCompanyInfo(companyInfo);
			companyPurchaseInvoice.setCompanyPurchaseOrder(companyPurchaseOrderInfo.get(0));
			CompanyPurchaseInvoice purchaseInvoice = companyPurchaseInvoiceRepo.save(companyPurchaseInvoice);
			BeanUtils.copyProperties(purchaseInvoice, invoiceDetailDTO);
			invoiceDetailDTO.setPurchaseOrderId(companyPurchaseOrderInfo.get(0).getPurchaseOrderId());
			invoiceDetailDTO.setPurchaseOrderId(companyPurchaseOrderInfo.get(0).getPurchaseOrderId());
			invoiceDetailDTO.setCompanyId(companyInfo.getCompanyId());
			invoiceDetailDTO.setDealName(vendorInfo.get(0).getVendorName());
			invoiceDetailDTO.setContactName(vendorInfo.get(0).getContactPersons().get(0).getContactPersonName());
			invoiceDetailDTO.setVendorId(vendorInfo.get(0).getId());
			invoiceDetailDTO.setSubject(companyPurchaseOrderInfo.get(0).getSubject());
			log.info("updated invoice details in account department");
		} else {

			CompanyPurchaseInvoice companyPurchaseInvoice = new CompanyPurchaseInvoice();
			BeanUtils.copyProperties(invoiceDetailsDto, companyPurchaseInvoice);
			companyPurchaseInvoice.setCompanyInfo(companyInfo);
			companyPurchaseInvoice.setCompanyPurchaseOrder(companyPurchaseOrderInfo.get(0));
			CompanyPurchaseInvoice companyPurchaseInvoiceDetails = companyPurchaseInvoiceRepo
					.save(companyPurchaseInvoice);
			BeanUtils.copyProperties(companyPurchaseInvoiceDetails, invoiceDetailDTO);
			invoiceDetailDTO.setPurchaseOrderId(companyPurchaseOrderInfo.get(0).getPurchaseOrderId());
			invoiceDetailDTO.setPurchaseInvoiceId(companyPurchaseInvoiceDetails.getPurchaseInvoiceId());
			invoiceDetailDTO.setCompanyId(companyInfo.getCompanyId());
			invoiceDetailDTO.setDealName(vendorInfo.get(0).getVendorName());
			invoiceDetailDTO.setContactName(vendorInfo.get(0).getContactPersons().get(0).getContactPersonName());
			invoiceDetailDTO.setVendorId(vendorInfo.get(0).getId());
			invoiceDetailDTO.setSubject(companyPurchaseOrderInfo.get(0).getSubject());
			log.info("Create the  invoice details in account department");

		}
		log.info(PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY);
		return invoiceDetailDTO;
	}

	/**
	 * this method is use to save the address Information accept the object of
	 * PurchaseBillingShippingAddress
	 * 
	 * @param AddressInformationDTO objects list
	 * @return updated objects list of AddressInformationDTO
	 **/
	@Transactional
	@Override
	public ArrayList<AddressInformationDTO> addressInformation(Long purchaseInvoiceId, Long purchaseOrderId) {
		CompanyPurchaseInvoice purchaseInvoice = companyPurchaseInvoiceRepo.findById(purchaseInvoiceId)
				.orElseThrow(() -> new CustomExceptionForAccount(PURCHASE_INVOCE_DETAILS_NOT_EXIXTS));

		List<PurchaseBillingShippingAddress> billingAddress = purchaseBillingShippingAddressRepo
				.findByCompanyPurchaseOrderPurchaseOrderId(purchaseOrderId);
		if (billingAddress == null || billingAddress.isEmpty()) {
			throw new CustomExceptionForAccount(PURCHASE_ADDRESS_DETAILS_NOT_EXISTS);
		}
		ArrayList<AddressInformationDTO> address = new ArrayList<>();
		for (PurchaseBillingShippingAddress purchaseBillingShippingAddress : billingAddress) {
			AddressInformationDTO addressInformationDTO = new AddressInformationDTO();
			BeanUtils.copyProperties(purchaseBillingShippingAddress, addressInformationDTO);
			addressInformationDTO.setPurchaseOrderId(purchaseOrderId);
			addressInformationDTO.setPurchaseInvoiceId(purchaseInvoice.getPurchaseInvoiceId());
			address.add(addressInformationDTO);
		}
		log.info(PURCHASE_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY);
		return address;
	}

	/**
	 * this method is use to save the attachments details accept the object of
	 * companyPurchaseInvoice
	 * 
	 * @param invoiceDetailsDto object
	 * @return updated object of InvoiceDetailsDTO
	 **/
	@Transactional
	@Override
	public AttachmentsDTO attachments(AttachmentsDTO attachmentsDTO) {
		CompanyPurchaseInvoice purchaseInvoice = companyPurchaseInvoiceRepo
				.findById(attachmentsDTO.getPurchaseInvoiceId())
				.orElseThrow(() -> new PurchaseOrderDoesNotExistsException(PURCHASE_INVOCE_DETAILS_NOT_EXIXTS));
		log.info("purchase invoice details fetched successfully");
		purchaseInvoice.setAttachment(attachmentsDTO.getAttachment());
		CompanyPurchaseInvoice companyPurchaseInvoice = companyPurchaseInvoiceRepo.save(purchaseInvoice);
		AttachmentsDTO attachmentsDtos = new AttachmentsDTO();
		attachmentsDtos.setPurchaseInvoiceId(companyPurchaseInvoice.getPurchaseInvoiceId());
		attachmentsDtos.setAttachment(purchaseInvoice.getAttachment());

		log.info(PURCHASE_ATTACHMENT_DETAILS_SAVED_SUCCESSFULLY);
		return attachmentsDtos;
	}

	/**
	 * this method created for getting all employee purchase order info and vender
	 * info which is sales order drop down in specific company
	 * 
	 * @param companyId
	 * @return list of SalesOrderDropdownDTO
	 **/
	@Override
	public List<SalesOrderDropdownDTO> salesOrderDropdown(Long companyId) {
		List<CompanyPurchaseOrder> purchaseOrder = companyPurchaseOrderRepo.findByCompanyInfoCompanyId(companyId);
		List<CompanyVendorInfo> vendorDetails = companyVendorInfoRepo.findByCompanyId(companyId);

		return purchaseOrder.stream().map(s -> {
			SalesOrderDropdownDTO dropdown = new SalesOrderDropdownDTO();
			for (CompanyVendorInfo vendorInfo : vendorDetails) {
				if (s.getVendorId() != null && vendorInfo.getId() != null
						&& s.getVendorId().equals(vendorInfo.getId())) {

					dropdown.setSubject(s.getSubject());

					dropdown.setContactPersons(vendorInfo.getContactPersons());
					dropdown.setVendorId(vendorInfo.getId());
					dropdown.setVendorName(vendorInfo.getVendorName());
					dropdown.setPurchaseOrderId(s.getPurchaseOrderId());
				}

			}

			return dropdown;
			// here we apply filter to does not collect null object
		}).filter(x -> x != null && x.getVendorId() != null).collect(Collectors.toList());

	}

	/**
	 * this method is use to save the description details accept the object of
	 * companyPurchaseInvoice
	 * 
	 * @param InvoiceItemsDTO objects list
	 * @return updated object of DescriptionDTO
	 **/
	@Override
	@Transactional
	public DescriptionDTO description(DescriptionDTO descriptionDto) {
		CompanyPurchaseInvoice purchaseInvoice = companyPurchaseInvoiceRepo
				.findById(descriptionDto.getPurchaseInvoiceId())
				.orElseThrow(() -> new PurchaseIdNotPresentException(PURCHASE_INVOCE_DETAILS_NOT_EXIXTS));
		purchaseInvoice.setDescription(descriptionDto.getDescription());
		CompanyPurchaseInvoice companyPurchaseInvoice = companyPurchaseInvoiceRepo.save(purchaseInvoice);
		DescriptionDTO descriptionsDto = new DescriptionDTO();
		descriptionsDto.setDescription(companyPurchaseInvoice.getDescription());
		descriptionsDto.setPurchaseInvoiceId(companyPurchaseInvoice.getPurchaseInvoiceId());
		log.info(PURCHASE_DESCRIPTION_DETAILS_SAVED_SUCCESSFULLY);
		return descriptionsDto;

	}

	/**
	 * this method is use to save the invoceItems details accept the object of
	 * companyPurchaseInvoice
	 * 
	 * @param InvoiceItemsDTO objects list
	 * @return updated objects list of InvoiceItemsDTO
	 **/
	@Override
	@Transactional

	public InvoiceItemDTO invoiceItems(InvoiceItemDTO invoiceItemsDTO) {
		InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
		List<InvoiceItemsDTO> invoiceList = new ArrayList<>();
		CompanyPurchaseInvoice purchaseInvoice = companyPurchaseInvoiceRepo
				.findById(invoiceItemsDTO.getPurchaseInvoiceId())
				.orElseThrow(() -> new PurchaseIdNotPresentException(PURCHASE_INVOCE_DETAILS_NOT_EXIXTS));
		BeanUtils.copyProperties(invoiceItemsDTO, purchaseInvoice);
		BigDecimal bigDecimal = new BigDecimal(invoiceItemsDTO.getAdjustment());
		purchaseInvoice.setAdjustment(bigDecimal);
		CompanyPurchaseInvoice companyPurchaseInvoice = companyPurchaseInvoiceRepo.save(purchaseInvoice);

		List<PurchaseOrderItems> purchaseOrderId = purchaseOrderItemsRepo.findAll();
		List<InvoiceItemsDTO> invoiceDTOList = invoiceItemsDTO.getInvoiceDetailsDTOs();
		Set<Long> invoiceItemListDto = invoiceDTOList.stream().map(InvoiceItemsDTO::getPurchaseInvoiceItemId)
				.collect(Collectors.toSet());
		List<PurchaseInvoiceItems> purchaseInvoiceItemsList = purchaseInvoice.getPurchaseInvoiceItemsList();
		Set<Long> entityItemListId = purchaseInvoiceItemsList.stream()
				.map(PurchaseInvoiceItems::getPurchaseInvoiceItemId).collect(Collectors.toSet());
		entityItemListId.removeAll(invoiceItemListDto);
		purchaseInvoiceItemsRepo.deleteAllByIdInBatch(entityItemListId);
		invoiceDTOList.forEach(e -> {
			if (!entityItemListId.contains(e.getPurchaseInvoiceItemId())) {
				PurchaseInvoiceItems purchaseInvoiceItems = new PurchaseInvoiceItems();
				BeanUtils.copyProperties(e, purchaseInvoiceItems);
				List<PurchaseOrderItems> collect = purchaseOrderId.stream()
						.filter(s -> Objects.equals(s.getPurchaseItemId(), e.getPurchaseItemId()))
						.collect(Collectors.toList());
				purchaseInvoiceItems.setPurchaseOrderItems(collect.get(0));
				purchaseInvoiceItems.setCompanyPurchaseInvoice(purchaseInvoice);
				PurchaseInvoiceItems invoiceItems = purchaseInvoiceItemsRepo.save(purchaseInvoiceItems);
				InvoiceItemsDTO invoiceItemsDTOs = new InvoiceItemsDTO();
				BeanUtils.copyProperties(invoiceItems, invoiceItemsDTOs);
				invoiceList.add(new InvoiceItemsDTO(invoiceItems.getPurchaseInvoiceItemId(),
						invoiceItems.getPurchaseOrderItems().getProductName(), invoiceItems.getDescription(),
						invoiceItems.getQuantity(), invoiceItems.getAmount(), invoiceItems.getDiscount(),
						invoiceItems.getTax(), invoiceItems.getPayableAmount(),
						invoiceItems.getPurchaseOrderItems().getPurchaseItemId()));
			}

		});

		invoiceItemDTO.setInvoiceDetailsDTOs(invoiceList);
		invoiceItemDTO.setPurchaseOrderId(purchaseInvoice.getCompanyPurchaseOrder().getPurchaseOrderId());
		invoiceItemDTO.setCompanyId(purchaseOrderId.get(0).getCompanyPurchaseOrder().getCompanyInfo().getCompanyId());
		BeanUtils.copyProperties(companyPurchaseInvoice, invoiceItemDTO);
		BigDecimal adjustment = companyPurchaseInvoice.getAdjustment();
		invoiceItemDTO.setAdjustment(adjustment.toString());
		log.info(PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY);
		return invoiceItemDTO;
	}

	@Override
	public ArrayList<PurchaseInvoiceDTO> purchaseInvoice(Long companyId) {
		List<CompanyPurchaseInvoice> invoiceDetialList = companyPurchaseInvoiceRepo
				.findByCompanyInfoCompanyId(companyId);
		List<String> venderIds = invoiceDetialList.stream().map(a -> a.getCompanyPurchaseOrder().getVendorId())
				.collect(Collectors.toList());
		List<CompanyVendorInfo> vendorInfoList = companyVendorInfoRepo.findByIdInAndCompanyId(venderIds, companyId);
		ArrayList<PurchaseInvoiceDTO> purchaseInvoiceList = new ArrayList<>();

		for (CompanyPurchaseInvoice companyPurchaseInvoice : invoiceDetialList) {
			PurchaseInvoiceDTO purchaseInvoiceDTO = new PurchaseInvoiceDTO();
			purchaseInvoiceDTO.setInvoiceDate(companyPurchaseInvoice.getInvoiceDate());
			purchaseInvoiceDTO.setInvoiceOwner(companyPurchaseInvoice.getCreatedBy());
			purchaseInvoiceDTO.setPurchaseInvoiceId(companyPurchaseInvoice.getPurchaseInvoiceId());
			purchaseInvoiceDTO.setStatus(companyPurchaseInvoice.getStatus());
			purchaseInvoiceDTO.setSubject(companyPurchaseInvoice.getCompanyPurchaseOrder().getSubject());
			for (CompanyVendorInfo vendorDetails : vendorInfoList) {
				if (companyPurchaseInvoice.getCompanyPurchaseOrder().getVendorId().equals(vendorDetails.getId())) {
					purchaseInvoiceDTO.setVendorName(vendorDetails.getVendorName());
				}

			}

			purchaseInvoiceList.add(purchaseInvoiceDTO);
		}
		Collections.reverse(purchaseInvoiceList);
		log.info(PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY);
		return purchaseInvoiceList;
	}

	@Override
	public PurchaseInvoiceDetailsByIdDto purchaseInvoiceById(Long companyId, Long purchaseInvoiceId) {
		PurchaseInvoiceDetailsByIdDto purchaseInvoiceDetailsByIdDto = new PurchaseInvoiceDetailsByIdDto();
		List<CompanyPurchaseInvoice> purchaseInfo = companyPurchaseInvoiceRepo
				.findByCompanyInfoCompanyIdAndPurchaseInvoiceId(companyId, purchaseInvoiceId);
		if (purchaseInfo == null || purchaseInfo.isEmpty()) {
			throw new PurchaseIdNotPresentException(PURCHASE_INVOCE_DETAILS_NOT_EXIXTS);
		}
		CompanyPurchaseInvoice companyPurchaseInvoice = purchaseInfo.get(0);
		purchaseInvoiceDetailsByIdDto.setAttachment(companyPurchaseInvoice.getAttachment());
		purchaseInvoiceDetailsByIdDto.setDescription(companyPurchaseInvoice.getDescription());
		purchaseInvoiceDetailsByIdDto.setAdjustment(companyPurchaseInvoice.getAdjustment());
		List<CompanyVendorInfo> vendorInfoList = companyVendorInfoRepo
				.findByIdAndCompanyId(companyPurchaseInvoice.getCompanyPurchaseOrder().getVendorId(), companyId);
		if (vendorInfoList == null || vendorInfoList.isEmpty()) {
			throw new CustomExceptionForAccount("Vendor details not found");
		}

		purchaseInvoiceDetailsByIdDto.setAttachment(companyPurchaseInvoice.getAttachment());
		purchaseInvoiceDetailsByIdDto.setDescription(companyPurchaseInvoice.getDescription());

		InvoiceDetailsDTO invoiceDetailDTO = new InvoiceDetailsDTO();
		BeanUtils.copyProperties(companyPurchaseInvoice, invoiceDetailDTO);
		invoiceDetailDTO.setInvoiceOwner(companyPurchaseInvoice.getCreatedBy() + "");
		invoiceDetailDTO.setDealName(vendorInfoList.get(0).getVendorName());
		invoiceDetailDTO.setContactName(vendorInfoList.get(0).getContactPersons().get(0).getContactPersonName());
		invoiceDetailDTO.setSubject(companyPurchaseInvoice.getCompanyPurchaseOrder().getSubject());
		invoiceDetailDTO.setVendorId(vendorInfoList.get(0).getId());
		invoiceDetailDTO.setPurchaseOrderId(companyPurchaseInvoice.getCompanyPurchaseOrder().getPurchaseOrderId());
		BeanUtils.copyProperties(invoiceDetailDTO, invoiceDetailDTO);
		ArrayList<InvoiceDetailsDTO> invoiceDetailList = new ArrayList<>();
		invoiceDetailList.add(invoiceDetailDTO);
		purchaseInvoiceDetailsByIdDto.setInvoiceDetails(invoiceDetailList);
		List<PurchaseBillingShippingAddress> addressInfo = purchaseInfo.get(0).getCompanyPurchaseOrder()
				.getPurchaseBillingShippingAddressList();

		ArrayList<AddressInformationDTO> addressInfoList = new ArrayList<>();
		for (PurchaseBillingShippingAddress purchaseBillingShippingAddress : addressInfo) {
			AddressInformationDTO addressInformationDTO = new AddressInformationDTO();
			BeanUtils.copyProperties(purchaseBillingShippingAddress, addressInformationDTO);
			addressInfoList.add(addressInformationDTO);
		}
		purchaseInvoiceDetailsByIdDto.setAddressInformation(addressInfoList);
		ArrayList<InvoiceItemsDTO> invoiceList = new ArrayList<>();
		List<PurchaseInvoiceItems> purchaseInvoiceList = purchaseInfo.get(0).getPurchaseInvoiceItemsList();
		for (PurchaseInvoiceItems purchaseInvoiceItems : purchaseInvoiceList) {
			InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();
			BeanUtils.copyProperties(purchaseInvoiceItems, invoiceItemsDTO);
			invoiceItemsDTO.setProductName(purchaseInvoiceItems.getPurchaseOrderItems().getProductName());
			invoiceItemsDTO.setPurchaseItemId(purchaseInvoiceItems.getPurchaseOrderItems().getPurchaseItemId());
			invoiceList.add(invoiceItemsDTO);
			purchaseInvoiceDetailsByIdDto.setInvoiceItems(invoiceList);

		}
		log.info(PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY);
		return purchaseInvoiceDetailsByIdDto;
	}

	@Override
	public InvoiceItemDTO invoiceItemsList(Long companyId, Long purchaseOrderId) {
		InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
		ArrayList<InvoiceItemsDTO> arrayList = new ArrayList<>();

		CompanyPurchaseOrder purchaseOrder = companyPurchaseOrderRepo.findById(purchaseOrderId)
				.orElseThrow(() -> new CustomExceptionForAccount(PURCHASE_ORDER_DOES_NOT_EXISTS));
		List<PurchaseOrderItems> purchaseOrderItemsList = purchaseOrder.getPurchaseOrderItemsList();
		if (purchaseOrderItemsList == null || purchaseOrderItemsList.isEmpty()) {
			throw new CustomExceptionForAccount("purchase invoice items not exist");
		}
		for (PurchaseOrderItems purchaseOrderItems : purchaseOrderItemsList) {
			BeanUtils.copyProperties(purchaseOrder, invoiceItemDTO);

			InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();
			BeanUtils.copyProperties(purchaseOrderItems, invoiceItemsDTO);
			invoiceItemsDTO.setPurchaseInvoiceItemId(purchaseOrderItems.getPurchaseItemId());
			invoiceItemDTO.setPurchaseOrderId(purchaseOrderItems.getCompanyPurchaseOrder().getPurchaseOrderId());
			invoiceItemDTO.setCompanyId(purchaseOrderItems.getCompanyPurchaseOrder().getCompanyInfo().getCompanyId());

			invoiceItemsDTO.setPurchaseItemId(purchaseOrderItems.getPurchaseItemId());

			arrayList.add(invoiceItemsDTO);

		}
		invoiceItemDTO.setInvoiceDetailsDTOs(arrayList);
		return invoiceItemDTO;
	}

	@Override
	public InvoiceItemDTO invoiceItemsListByPurchaseInvoiceId(Long companyId, Long purchaseInvoiceId) {

		InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
		ArrayList<InvoiceItemsDTO> itemList = new ArrayList<>();

		CompanyPurchaseInvoice purchaseInvoice = companyPurchaseInvoiceRepo.findById(purchaseInvoiceId)
				.orElseThrow(() -> new CustomExceptionForAccount(PURCHASE_ORDER_DOES_NOT_EXISTS));
		BeanUtils.copyProperties(purchaseInvoice, invoiceItemDTO);
		invoiceItemDTO.setAdjustment(
				purchaseInvoice.getAdjustment() == null ? null : purchaseInvoice.getAdjustment().toString());
		invoiceItemDTO.setCompanyId(purchaseInvoice.getCompanyInfo().getCompanyId());
		List<PurchaseInvoiceItems> purchaseInvoiceItemsList = purchaseInvoice.getPurchaseInvoiceItemsList();
		if (purchaseInvoiceItemsList == null || purchaseInvoiceItemsList.isEmpty()) {
			throw new CustomExceptionForAccount("purchase invoice items not exist");
		}
		for (PurchaseInvoiceItems companyPurchaseinvoice : purchaseInvoiceItemsList) {
			InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();

			BeanUtils.copyProperties(companyPurchaseinvoice, invoiceItemsDTO);
			invoiceItemsDTO.setProductName(companyPurchaseinvoice.getPurchaseOrderItems() == null ? null
					: companyPurchaseinvoice.getPurchaseOrderItems().getProductName());
			invoiceItemsDTO.setPurchaseItemId(companyPurchaseinvoice.getPurchaseOrderItems().getPurchaseItemId());
			itemList.add(invoiceItemsDTO);
		}

		invoiceItemDTO.setInvoiceDetailsDTOs(itemList);
		return invoiceItemDTO;
	}

	@Override
	public InvoiceItemDTO salesItemsList(Long companyId, Long salesOrderId) {
		InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
		ArrayList<InvoiceItemsDTO> arrayList = new ArrayList<>();

		CompanySalesOrder companySalesOrder = companySalesOrderRepo.findById(salesOrderId)
				.orElseThrow(() -> new CustomExceptionForAccount("Sales invoice details not found"));
		List<SalesOrderItems> salesOrderItemsList = companySalesOrder.getSalesOrderItemsList();
		if (salesOrderItemsList == null || salesOrderItemsList.isEmpty()) {
			throw new CustomExceptionForAccount("Sales invoice items not exist");
		}
		BeanUtils.copyProperties(companySalesOrder, invoiceItemDTO);
		for (SalesOrderItems salesOrderItem : salesOrderItemsList) {
			InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();
			BeanUtils.copyProperties(salesOrderItem, invoiceItemsDTO);
			invoiceItemsDTO.setPurchaseInvoiceItemId(salesOrderItem.getSaleItemId());
			invoiceItemDTO.setPurchaseOrderId(salesOrderItem.getCompanySalesOrder().getSalesOrderId());
			invoiceItemDTO.setCompanyId(salesOrderItem.getCompanySalesOrder().getCompanyInfo().getCompanyId());

			invoiceItemsDTO.setPurchaseItemId(salesOrderItem.getSaleItemId());
			invoiceItemsDTO.setPayableAmount(salesOrderItem.getReceivableAmount());
			arrayList.add(invoiceItemsDTO);
		}
		invoiceItemDTO.setInvoiceDetailsDTOs(arrayList);
		invoiceItemDTO.setTotalPayableAmount(companySalesOrder.getTotalReceivableAmount());
		return invoiceItemDTO;
	}

	@Override
	public InvoiceItemDTO invoiceItemsListBySalesInvoiceId(Long companyId, Long salesInvoiceId) {
		InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
		ArrayList<InvoiceItemsDTO> itemList = new ArrayList<>();

		CompanySalesInvoice companySalesInvoice = companySalesInvoiceRepo.findById(salesInvoiceId)
				.orElseThrow(() -> new CustomExceptionForAccount("Sales invoice details not found"));
		BeanUtils.copyProperties(companySalesInvoice, invoiceItemDTO);
		invoiceItemDTO.setAdjustment(companySalesInvoice.getAdjustment().toString());
		invoiceItemDTO.setCompanyId(companySalesInvoice.getCompanyInfo().getCompanyId());
		invoiceItemDTO.setTotalPayableAmount(companySalesInvoice.getTotalReceivableAmount());
		List<SalesInvoiceItems> salesInvoiceItemsList = companySalesInvoice.getSalesInvoiceItemsList();
		if (salesInvoiceItemsList == null || salesInvoiceItemsList.isEmpty()) {
			throw new CustomExceptionForAccount("purchase invoice items not exist");
		}
		for (SalesInvoiceItems companySalesInvoiceItems : salesInvoiceItemsList) {
			InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();

			BeanUtils.copyProperties(companySalesInvoiceItems, invoiceItemsDTO);
			invoiceItemsDTO.setProductName(companySalesInvoiceItems.getSalesOrderItems() == null ? null
					: companySalesInvoiceItems.getSalesOrderItems().getProductName());
			invoiceItemsDTO.setPurchaseItemId(companySalesInvoiceItems.getSalesOrderItems().getSaleItemId());
			invoiceItemsDTO.setPurchaseInvoiceItemId(companySalesInvoiceItems.getInvoiceItemId());
			invoiceItemsDTO.setPayableAmount(companySalesInvoiceItems.getReceivableAmount());
			itemList.add(invoiceItemsDTO);
		}

		invoiceItemDTO.setInvoiceDetailsDTOs(itemList);
		invoiceItemDTO.setPurchaseInvoiceId(companySalesInvoice.getSalesInvoiceId());
		invoiceItemDTO.setPurchaseOrderId(companySalesInvoice.getCompanySalesOrder().getSalesOrderId());
		return invoiceItemDTO;
	}

	@Override
	public ArrayList<AddressInformationDTO> salesAddressInformation(Long salesInvoiceId, Long salesOrderId) {
		CompanySalesInvoice companySalesInvoice = companySalesInvoiceRepo.findById(salesInvoiceId)
				.orElseThrow(() -> new CustomExceptionForAccount(PURCHASE_INVOCE_DETAILS_NOT_EXIXTS));

		List<SalesBillingShippingAddress> salesBilingAddress = salesBillingShippingAddressRepo
				.findByCompanySalesOrderSalesOrderId(salesOrderId);

		if (salesBilingAddress == null || salesBilingAddress.isEmpty()) {
			throw new CustomExceptionForAccount(PURCHASE_ADDRESS_DETAILS_NOT_EXISTS);
		}
		ArrayList<AddressInformationDTO> address = new ArrayList<>();
		for (SalesBillingShippingAddress salesBillingShippingAddress : salesBilingAddress) {
			AddressInformationDTO addressInformationDTO = new AddressInformationDTO();
			BeanUtils.copyProperties(salesBillingShippingAddress, addressInformationDTO);
			addressInformationDTO
					.setPurchaseOrderId(salesBillingShippingAddress.getCompanySalesOrder().getSalesOrderId());
			addressInformationDTO.setPurchaseInvoiceId(companySalesInvoice.getSalesInvoiceId());
			address.add(addressInformationDTO);
		}
		log.info(PURCHASE_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY);
		return address;
	}

	@Override
	public ArrayList<SalesInvoiceDTO> salesInvoice(Long companyId) {
		List<CompanySalesInvoice> salesInvoiceDetails = companySalesInvoiceRepo.findByCompanyInfoCompanyId(companyId);
		ArrayList<SalesInvoiceDTO> salesItemList = new ArrayList<>();
		if (salesInvoiceDetails == null || salesInvoiceDetails.isEmpty()) {
			return salesItemList;
		}
		for (CompanySalesInvoice companySalesInvoice : salesInvoiceDetails) {
			SalesInvoiceDTO salesInvoiceDTO = new SalesInvoiceDTO();
			BeanUtils.copyProperties(companySalesInvoice, salesInvoiceDTO);
			salesInvoiceDTO.setSubject(companySalesInvoice.getCompanySalesOrder().getSubject());
			salesInvoiceDTO.setInvoiceOwner(companySalesInvoice.getCreatedBy());

			salesInvoiceDTO
					.setClientName(companySalesInvoice.getCompanySalesOrder().getCompanyClientInfo().getClientName());

			salesItemList.add(salesInvoiceDTO);
		}
		Collections.reverse(salesItemList);
		return salesItemList;
	}

	@Override
	public InvoiceItemDTO salesInvoiceItems(InvoiceItemDTO invoiceItemsDTO) {

		InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
		List<InvoiceItemsDTO> invoiceList = new ArrayList<>();
		CompanySalesInvoice companySalesInvoice = companySalesInvoiceRepo
				.findById(invoiceItemsDTO.getPurchaseInvoiceId())
				.orElseThrow(() -> new PurchaseIdNotPresentException("Sales invoice details not found"));
		BeanUtils.copyProperties(invoiceItemsDTO, companySalesInvoice);
		BigDecimal bigDecimal = new BigDecimal(invoiceItemsDTO.getAdjustment());
		companySalesInvoice.setAdjustment(bigDecimal);
		companySalesInvoice.setTotalReceivableAmount(invoiceItemsDTO.getTotalPayableAmount());
		CompanySalesInvoice companyPurchaseInvoice = companySalesInvoiceRepo.save(companySalesInvoice);

		List<SalesOrderItems> salesorderItems = salesOrderItemsRepo.findAll();
		List<InvoiceItemsDTO> invoiceDTOList = invoiceItemsDTO.getInvoiceDetailsDTOs();
		Set<Long> invoiceItemListDto = invoiceDTOList.stream().map(InvoiceItemsDTO::getPurchaseInvoiceItemId)
				.collect(Collectors.toSet());
		List<SalesInvoiceItems> salesInvoiceItemsList = companyPurchaseInvoice.getSalesInvoiceItemsList();
		Set<Long> entityItemListId = salesInvoiceItemsList.stream().map(a -> a.getInvoiceItemId())
				.collect(Collectors.toSet());
		entityItemListId.removeAll(invoiceItemListDto);
		salesInvoiceItemsRepo.deleteAllByIdInBatch(entityItemListId);
		invoiceDTOList.forEach(e -> {
			if (!entityItemListId.contains(e.getPurchaseInvoiceItemId())) {
				SalesInvoiceItems purchaseInvoiceItems = new SalesInvoiceItems();
				BeanUtils.copyProperties(e, purchaseInvoiceItems);
				List<SalesOrderItems> collect = salesorderItems.stream()
						.filter(s -> Objects.equals(s.getSaleItemId(), e.getPurchaseItemId()))
						.collect(Collectors.toList());
				purchaseInvoiceItems.setSalesOrderItems(collect.get(0));
				purchaseInvoiceItems.setCompanySalesInvoice(companyPurchaseInvoice);
				purchaseInvoiceItems.setReceivableAmount(e.getPayableAmount());
				SalesInvoiceItems invoiceItems = salesInvoiceItemsRepo.save(purchaseInvoiceItems);
				InvoiceItemsDTO invoiceItemsDTOs = new InvoiceItemsDTO();
				BeanUtils.copyProperties(invoiceItems, invoiceItemsDTOs);
				invoiceList.add(new InvoiceItemsDTO(invoiceItems.getInvoiceItemId(),
						invoiceItems.getSalesOrderItems().getProductName(), invoiceItems.getDescription(),
						invoiceItems.getQuantity(), invoiceItems.getAmount(), invoiceItems.getDiscount(),
						invoiceItems.getTax(), invoiceItems.getReceivableAmount(),
						invoiceItems.getSalesOrderItems().getSaleItemId()));

			}

		});

		invoiceItemDTO.setInvoiceDetailsDTOs(invoiceList);
		invoiceItemDTO.setPurchaseOrderId(companySalesInvoice.getCompanySalesOrder().getSalesOrderId());
		invoiceItemDTO.setCompanyId(companySalesInvoice.getCompanySalesOrder().getCompanyInfo().getCompanyId());
		BeanUtils.copyProperties(companyPurchaseInvoice, invoiceItemDTO);
		BigDecimal adjustment = companyPurchaseInvoice.getAdjustment();
		invoiceItemDTO.setAdjustment(adjustment.toString());
		invoiceItemDTO.setTotalPayableAmount(companyPurchaseInvoice.getTotalReceivableAmount());
		invoiceItemDTO.setPurchaseInvoiceId(companyPurchaseInvoice.getSalesInvoiceId());
		log.info(PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY);
		return invoiceItemDTO;
	}

	@Override
	public PurchaseInvoiceDetailsByIdDto salesInvoiceById(Long companyId, Long salesInvoiceId) {
		List<CompanySalesInvoice> companySalesInvoices = companySalesInvoiceRepo
				.findByCompanyInfoCompanyIdAndSalesInvoiceId(companyId, salesInvoiceId);
		if (companySalesInvoices == null || companySalesInvoices.isEmpty()) {
			throw new PurchaseIdNotPresentException(SALES_INVOICE_DETAILS_NOT_FOUND);
		}
		CompanySalesInvoice companySalesInvoice = companySalesInvoices.get(0);

		PurchaseInvoiceDetailsByIdDto purchaseInvoiceDetailsByIdDto = new PurchaseInvoiceDetailsByIdDto();
		purchaseInvoiceDetailsByIdDto.setAdjustment(companySalesInvoice.getAdjustment());
		purchaseInvoiceDetailsByIdDto.setAttachment(companySalesInvoice.getAttachments());
		purchaseInvoiceDetailsByIdDto.setDescription(companySalesInvoice.getDescription());
		InvoiceDetailsDTO invoiceDetailDTO = new InvoiceDetailsDTO();
		BeanUtils.copyProperties(companySalesInvoice, invoiceDetailDTO);
		invoiceDetailDTO.setPurchaseInvoiceId(companySalesInvoice.getSalesInvoiceId());
		invoiceDetailDTO.setCompanyId(companySalesInvoice.getCompanySalesOrder().getCompanyInfo().getCompanyId());
		invoiceDetailDTO.setInvoiceOwner(companySalesInvoice.getCreatedBy() + "");
		invoiceDetailDTO.setDealName(companySalesInvoice.getCompanySalesOrder().getCompanyClientInfo().getClientName());
		invoiceDetailDTO.setContactName(
				companySalesInvoice.getCompanySalesOrder().getClientContactPersonDetails().getFirstName()
						+ companySalesInvoice.getCompanySalesOrder().getClientContactPersonDetails().getLastName());
		invoiceDetailDTO.setSubject(companySalesInvoice.getCompanySalesOrder().getSubject());
		invoiceDetailDTO
				.setVendorId(companySalesInvoice.getCompanySalesOrder().getCompanyClientInfo().getClientId() + "");
		invoiceDetailDTO.setPurchaseOrderId(companySalesInvoice.getCompanySalesOrder().getSalesOrderId());
		BeanUtils.copyProperties(invoiceDetailDTO, invoiceDetailDTO);
		ArrayList<InvoiceDetailsDTO> invoiceDetailList = new ArrayList<>();
		invoiceDetailList.add(invoiceDetailDTO);
		purchaseInvoiceDetailsByIdDto.setInvoiceDetails(invoiceDetailList);
		List<SalesBillingShippingAddress> shippingAddressList = companySalesInvoice.getCompanySalesOrder()
				.getSalesBillingShippingAddressList();

		if (shippingAddressList == null || shippingAddressList.isEmpty()) {
			throw new CustomExceptionForAccount("billing adress not found");
		}

		ArrayList<AddressInformationDTO> addressInfoList = new ArrayList<>();
		for (SalesBillingShippingAddress purchaseBillingShippingAddress : shippingAddressList) {
			AddressInformationDTO addressInformationDTO = new AddressInformationDTO();
			BeanUtils.copyProperties(purchaseBillingShippingAddress, addressInformationDTO);
			addressInformationDTO
					.setPurchaseOrderId(purchaseBillingShippingAddress.getCompanySalesOrder().getSalesOrderId());
			addressInformationDTO.setPurchaseInvoiceId(companySalesInvoice.getSalesInvoiceId());
			addressInfoList.add(addressInformationDTO);
		}
		purchaseInvoiceDetailsByIdDto.setAddressInformation(addressInfoList);
		ArrayList<InvoiceItemsDTO> invoiceList = new ArrayList<>();
		List<SalesInvoiceItems> salesInvoiceItemsList = companySalesInvoice.getSalesInvoiceItemsList();
//		if (salesInvoiceItemsList == null || salesInvoiceItemsList.isEmpty()) {
//			throw new CustomExceptionForAccount("invoice details not found");
//		}
		for (SalesInvoiceItems purchaseInvoiceItems : salesInvoiceItemsList) {
			InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();
			BeanUtils.copyProperties(purchaseInvoiceItems, invoiceItemsDTO);
			invoiceItemsDTO.setProductName(purchaseInvoiceItems.getSalesOrderItems().getProductName());
			invoiceItemsDTO.setPurchaseInvoiceItemId(purchaseInvoiceItems.getInvoiceItemId());
			invoiceItemsDTO.setPurchaseItemId(purchaseInvoiceItems.getSalesOrderItems().getSaleItemId());
			invoiceItemsDTO.setPayableAmount(purchaseInvoiceItems.getReceivableAmount());
			invoiceList.add(invoiceItemsDTO);
			purchaseInvoiceDetailsByIdDto.setInvoiceItems(invoiceList);

		}
		purchaseInvoiceDetailsByIdDto.setAttachment(companySalesInvoice.getAttachments());
		purchaseInvoiceDetailsByIdDto.setDescription(companySalesInvoice.getDescription());

		log.info(PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY);
		return purchaseInvoiceDetailsByIdDto;

	}
}
