package com.te.flinko.service.account;

import static com.te.flinko.common.account.AccountDepartmentConstants.COMPANY_CLIENT_DATA_NOT_FOUND;
import static com.te.flinko.common.account.AccountDepartmentConstants.COMPANY_CONTACT_PERSON_DATA_NOT_FOUND;
import static com.te.flinko.common.account.AccountDepartmentConstants.COMPANY_DATA_NOT_FOUND;
import static com.te.flinko.common.account.AccountDepartmentConstants.NOT_UNIQUE_SUBJECT;
import static com.te.flinko.common.account.AccountDepartmentConstants.PURCHASE_ORDER_DATA_NOT_FOUND;
import static com.te.flinko.common.account.AccountDepartmentConstants.SALES_ORDER_DATA_NOT_FOUND;
import static com.te.flinko.common.account.AccountDepartmentConstants.STOCK_GROUP_NOT_FOUND;
import static com.te.flinko.common.account.AccountDepartmentConstants.VENDOR_DATA_NOT_FOUND;
import static com.te.flinko.dto.account.AddressType.BILLING;
import static com.te.flinko.dto.account.AddressType.SHIPPING;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.te.flinko.dto.account.AccountDescriptionDTO;
import com.te.flinko.dto.account.AddressInformationDTO;
import com.te.flinko.dto.account.BillingAddressDTO;
import com.te.flinko.dto.account.BillingShippingAddressDTO;
import com.te.flinko.dto.account.CompanyPurchaseOrderDTO;
import com.te.flinko.dto.account.CompanySalesOrderDTO;
import com.te.flinko.dto.account.ProductType;
import com.te.flinko.dto.account.PurchaseItemDTO;
import com.te.flinko.dto.account.PurchaseItemsDTO;
import com.te.flinko.dto.account.PurchasedOrderDisplayDTO;
import com.te.flinko.dto.account.SalesItemDTO;
import com.te.flinko.dto.account.SalesItemsDTO;
import com.te.flinko.dto.account.ShippingAddressDTO;
import com.te.flinko.dto.account.WorkOrderDTO;
import com.te.flinko.dto.account.WorkOrderResourcesDTO;
import com.te.flinko.dto.account.mongo.ContactPerson;
import com.te.flinko.dto.admin.TermsAndConditionDTO;
import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.account.CompanySalesOrder;
import com.te.flinko.entity.account.CompanyWorkOrder;
import com.te.flinko.entity.account.PurchaseBillingShippingAddress;
import com.te.flinko.entity.account.PurchaseOrderItems;
import com.te.flinko.entity.account.SalesBillingShippingAddress;
import com.te.flinko.entity.account.SalesOrderItems;
import com.te.flinko.entity.account.WorkOrderResources;
import com.te.flinko.entity.account.mongo.CompanyVendorInfo;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyStockGroup;
import com.te.flinko.entity.admin.CompanyTermsAndConditions;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.sales.ClientContactPersonDetails;
import com.te.flinko.entity.sales.CompanyClientInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.account.SubjectNotUniqueException;
import com.te.flinko.repository.account.CompanyVendorInfoRepository;
import com.te.flinko.repository.account.CompanyWorkOrderRepository;
import com.te.flinko.repository.account.PurchaseBillingShippingAddressRepository;
import com.te.flinko.repository.account.SalesBillingShippingAddressRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admin.CompanyStockGroupRepository;
import com.te.flinko.repository.admin.CompanyTermsAndConditionsRepository;
import com.te.flinko.repository.admindept.CompanyPurchaseOrderRepository;
import com.te.flinko.repository.admindept.CompanySalesOrderRepository;
import com.te.flinko.repository.admindept.PurchaseOrderItemsRepository;
import com.te.flinko.repository.admindept.SalesOrderItemsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.sales.ClientContactPersonDetailsRepository;
import com.te.flinko.repository.sales.CompanyClientInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountDepartmentServiceImpl implements AccountDepartmentService {

	private final CompanyPurchaseOrderRepository companyPurchaseOrderRepository;
	private final CompanySalesOrderRepository companySalesOrderRepository;
	private final CompanyStockGroupRepository companyStockGroupRepository;
	private final PurchaseBillingShippingAddressRepository purchaseBillingShippingAddressRepository;
	private final SalesBillingShippingAddressRepository salesBillingShippingAddressRepository;
	private final CompanyInfoRepository companyInfoRepository;
	private final CompanyClientInfoRepository companyClientInfoRepository;
	private final CompanyVendorInfoRepository companyVendorInfoRepository;
	private final ClientContactPersonDetailsRepository clientContactPersonDetailsRepository;
	private final CompanyWorkOrderRepository companyWorkOrderRepository;
	private final CompanyClientInfoRepository clientInfoRepository;
	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;
	private final PurchaseOrderItemsRepository purchaseOrderItemsRepo;
	private final SalesOrderItemsRepository salesOrderItemsRepo;
	private final CompanyTermsAndConditionsRepository companyTermsAndConditionsRepository;

	@Transactional
	@Override
	public Long createUpdatePurchaseOrder(CompanyPurchaseOrderDTO companyPurchaseOrderDTO, Long companyId) {
		CompanyStockGroup companyStockGroup = companyStockGroupRepository
				.findById(companyPurchaseOrderDTO.getStockGroupId())
				.orElseThrow(() -> new DataNotFoundException(STOCK_GROUP_NOT_FOUND));
		CompanyInfo companyInfo = companyInfoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException(COMPANY_DATA_NOT_FOUND));
		companyVendorInfoRepository.findById(companyPurchaseOrderDTO.getVendorId())
				.orElseThrow(() -> new DataNotFoundException(VENDOR_DATA_NOT_FOUND)).getVendorInfoId();
		CompanyPurchaseOrder companyPurchaseOrder = new CompanyPurchaseOrder();
		if (companyPurchaseOrderDTO.getPurchaseOrderId() == null) {
			if (!companyPurchaseOrderRepository.findBySubject(companyPurchaseOrderDTO.getSubject()).isEmpty())
				throw new SubjectNotUniqueException(NOT_UNIQUE_SUBJECT);
			companyPurchaseOrder.setSubject(companyPurchaseOrderDTO.getSubject());
			companyPurchaseOrder.setCompanyInfo(companyInfo);
		} else {
			companyPurchaseOrder = companyPurchaseOrderRepository.findById(companyPurchaseOrderDTO.getPurchaseOrderId())
					.orElseThrow(() -> new DataNotFoundException(PURCHASE_ORDER_DATA_NOT_FOUND));
		}
		companyPurchaseOrder.setCompanyStockGroup(companyStockGroup);
		companyPurchaseOrder.setVendorContactPersonId(companyPurchaseOrderDTO.getContactName());
		companyPurchaseOrder.setPurchaseOrderNumber(companyPurchaseOrderDTO.getPurchaseOrderNumber());
		companyPurchaseOrder.setType(companyPurchaseOrderDTO.getProductType().name());
		companyPurchaseOrder.setVendorId(companyPurchaseOrderDTO.getVendorId());
		companyPurchaseOrder.setRequisitionNumber(companyPurchaseOrderDTO.getRequisitionNumber());
		companyPurchaseOrder.setTrackingNumber(companyPurchaseOrderDTO.getTrackingNumber());
		companyPurchaseOrder.setDueDate(companyPurchaseOrderDTO.getDueDate());
		companyPurchaseOrder.setCarrier(companyPurchaseOrderDTO.getCarrier());
		companyPurchaseOrder.setExciseDuty(companyPurchaseOrderDTO.getExciseDuty());
		companyPurchaseOrder.setSalesCommission(companyPurchaseOrderDTO.getSalesCommission());
		companyPurchaseOrder.setStatus(companyPurchaseOrderDTO.getStatus());
		companyPurchaseOrder.setPoDate(companyPurchaseOrderDTO.getPurchaseOrderDate());
		System.err.println(companyPurchaseOrderDTO.getPurchaseType());
		companyPurchaseOrder.setPurchaseType(companyPurchaseOrderDTO.getPurchaseType());
		return companyPurchaseOrderRepository.save(companyPurchaseOrder).getPurchaseOrderId();
	}

	@Transactional
	@Override
	public Long createUpdateSalesOrder(CompanySalesOrderDTO companySalesOrderDTO, Long companyId) {
		log.info(
				"createUpdateSalesOrder method execution start, checking existence of stock group, company, company client, client contact person and vendor data");
		CompanyStockGroup companyStockGroup = companyStockGroupRepository
				.findById(companySalesOrderDTO.getStockGroupId())
				.orElseThrow(() -> new DataNotFoundException(STOCK_GROUP_NOT_FOUND));
		CompanyInfo companyInfo = companyInfoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException(COMPANY_DATA_NOT_FOUND));
		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findById(companySalesOrderDTO.getCompanyClientInfoID())
				.orElseThrow(() -> new DataNotFoundException(COMPANY_CLIENT_DATA_NOT_FOUND));
		ClientContactPersonDetails clientContactPersonDetails = clientContactPersonDetailsRepository
				.findById(companySalesOrderDTO.getClientContactPersonID())
				.orElseThrow(() -> new DataNotFoundException(COMPANY_CONTACT_PERSON_DATA_NOT_FOUND));
		log.info("createUpdateSalesOrder method execution start, finding if the subject is unique");
		CompanySalesOrder companySalesOrder = new CompanySalesOrder();
		if (Optional.ofNullable(companySalesOrderDTO.getSalesOrderId()).isEmpty()) {
			if (companySalesOrderRepository.findBySubject(companySalesOrderDTO.getSubject()).size() > 0)
				throw new SubjectNotUniqueException(NOT_UNIQUE_SUBJECT);
			companySalesOrder.setSubject(companySalesOrderDTO.getSubject());
			companySalesOrder.setCompanyInfo(companyInfo);
		} else {
			companySalesOrder = companySalesOrderRepository.findById(companySalesOrderDTO.getSalesOrderId())
					.orElseThrow(() -> new DataNotFoundException(SALES_ORDER_DATA_NOT_FOUND));
		}
		companySalesOrder.setCompanyStockGroup(companyStockGroup);
		companySalesOrder.setCompanyClientInfo(companyClientInfo);
		companySalesOrder.setClientContactPersonDetails(clientContactPersonDetails);
		companySalesOrder.setType(companySalesOrderDTO.getProductType().name());
		companySalesOrder.setPurchaseOrder(companySalesOrderDTO.getPurchaseOrder());
		companySalesOrder.setCustomerNumber(companySalesOrderDTO.getCustomerNumber());
		companySalesOrder.setDueDate(companySalesOrderDTO.getDueDate());
		companySalesOrder.setPending(companySalesOrderDTO.getPending());
		companySalesOrder.setExciseDuty(companySalesOrderDTO.getExciseDuty());
		companySalesOrder.setCarrier(companySalesOrderDTO.getExciseDuty());
		companySalesOrder.setStatus(companySalesOrderDTO.getStatus());
		companySalesOrder.setSalesCommission(companySalesOrderDTO.getSalesCommission());
		return companySalesOrderRepository.save(companySalesOrder).getSalesOrderId();
	}

	@Transactional
	@Override
	public Long addPurchaseBillingShippingAddress(BillingShippingAddressDTO billingShippingAddressDTO,
			Long purchaseOrderId) {
		log.info(
				"addPurchaseBillingShippingAddress method execution start, retrieving billing and shipping addresses from the dto");
		BillingAddressDTO billingAddressDTO = billingShippingAddressDTO.getBillingAddress();
		ShippingAddressDTO shippingAddressDTO = billingShippingAddressDTO.getShippingAddress();
		log.debug(
				"addPurchaseBillingShippingAddress method, deleting previously saved billing and shipping addresses from the database");
		purchaseBillingShippingAddressRepository.deleteByCompanyPurchaseOrderPurchaseOrderId(purchaseOrderId);
		log.info(
				"addPurchaseBillingShippingAddress method, checking the existence of company purchase order data in the database");
		CompanyPurchaseOrder companyPurchaseOrder = companyPurchaseOrderRepository.findById(purchaseOrderId)
				.orElseThrow(() -> new DataNotFoundException(PURCHASE_ORDER_DATA_NOT_FOUND));
		log.info("addPurchaseBillingShippingAddress method, setting the required data");
		companyPurchaseOrder.setPurchaseBillingShippingAddressList(Lists.newArrayList(
				PurchaseBillingShippingAddress.builder().addressType(BILLING.name()).city(billingAddressDTO.getCity())
						.country(billingAddressDTO.getCountry()).state(billingAddressDTO.getState())
						.addressDetails(billingAddressDTO.getAddressDetails()).pinCode(billingAddressDTO.getPinCode())
						.companyPurchaseOrder(companyPurchaseOrder).build(),
				PurchaseBillingShippingAddress.builder().addressType(SHIPPING.name()).city(shippingAddressDTO.getCity())
						.country(shippingAddressDTO.getCountry()).state(shippingAddressDTO.getState())
						.addressDetails(shippingAddressDTO.getAddressDetails()).pinCode(shippingAddressDTO.getPinCode())
						.companyPurchaseOrder(companyPurchaseOrder).build()));
		log.info("addPurchaseBillingShippingAddress method, saving and returning the data");
		return companyPurchaseOrderRepository.save(companyPurchaseOrder).getPurchaseOrderId();
	}

	@Transactional
	@Override
	public Long addSalesBillingShippingAddress(BillingShippingAddressDTO billingShippingAddressDTO, Long salesOrderId) {
		log.info(
				"addSalesBillingShippingAddress method execution start, retrieving billing and shipping addresses from the dto");
		BillingAddressDTO billingAddressDTO = billingShippingAddressDTO.getBillingAddress();
		ShippingAddressDTO shippingAddressDTO = billingShippingAddressDTO.getShippingAddress();
		log.debug(
				"addSalesBillingShippingAddress method, deleting previously saved billing and shipping addresses from the database");
		salesBillingShippingAddressRepository.deleteByCompanySalesOrderSalesOrderId(salesOrderId);
		log.info(
				"addSalesBillingShippingAddress method, checking the existence of company sales order data in the database");
		CompanySalesOrder companySalesOrder = companySalesOrderRepository.findById(salesOrderId)
				.orElseThrow(() -> new DataNotFoundException(SALES_ORDER_DATA_NOT_FOUND));
		log.info("addSalesBillingShippingAddress method, setting the required data");
		companySalesOrder.setSalesBillingShippingAddressList(Lists.newArrayList(SalesBillingShippingAddress.builder()
				.addressType(BILLING.name()).city(billingAddressDTO.getCity()).state(billingAddressDTO.getState())
				.country(billingAddressDTO.getCountry()).addressDetails(billingAddressDTO.getAddressDetails())
				.pinCode(billingAddressDTO.getPinCode()).companySalesOrder(companySalesOrder).build(),
				SalesBillingShippingAddress.builder().addressType(SHIPPING.name()).city(shippingAddressDTO.getCity())
						.state(shippingAddressDTO.getState()).country(shippingAddressDTO.getCountry())
						.addressDetails(shippingAddressDTO.getAddressDetails()).pinCode(shippingAddressDTO.getPinCode())
						.companySalesOrder(companySalesOrder).build()));
		log.info("addSalesBillingShippingAddress method, saving and returning the data");
		return companySalesOrderRepository.save(companySalesOrder).getSalesOrderId();
	}

	@Transactional
	@Override
	public PurchaseItemsDTO addPurchasedItems(PurchaseItemsDTO purchaseItemsDTO, Long purchaseOrderId) {
		PurchaseItemsDTO purchaseItemsDto = new PurchaseItemsDTO();
		ArrayList<PurchaseItemDTO> purchaseItemList = new ArrayList<>();
		CompanyPurchaseOrder purchaseOrder = companyPurchaseOrderRepository.findById(purchaseOrderId)
				.orElseThrow(() -> new DataNotFoundException(PURCHASE_ORDER_DATA_NOT_FOUND));
		BeanUtils.copyProperties(purchaseItemsDTO, purchaseOrder);
		CompanyPurchaseOrder companyPurchaseOrder = companyPurchaseOrderRepository.save(purchaseOrder);
		BeanUtils.copyProperties(companyPurchaseOrder, purchaseItemsDto);
		purchaseItemsDto.setCompanyId(companyPurchaseOrder.getCompanyInfo().getCompanyId());
		List<PurchaseItemDTO> purchaseItemsDtos = purchaseItemsDTO.getPurchaseItems();
		List<PurchaseOrderItems> purchaseOrderItemsList = companyPurchaseOrder.getPurchaseOrderItemsList();
		Set<Long> entityItemsIdList = purchaseOrderItemsList.stream().map(PurchaseOrderItems::getPurchaseItemId)
				.collect(Collectors.toSet());

		Set<Long> dtoEntityIdList = purchaseItemsDtos.stream().map(PurchaseItemDTO::getPurchaseItemId)
				.collect(Collectors.toSet());
		entityItemsIdList.removeAll(dtoEntityIdList);
		// if purchase order product added in purchase invoice item entity after that
		// deleting that order items not possible it showing exception
		purchaseOrderItemsRepo.deleteAllByIdInBatch(entityItemsIdList);
		purchaseItemsDtos.forEach(e -> {
			if (!entityItemsIdList.contains(e.getPurchaseItemId())) {
				List<PurchaseOrderItems> products = purchaseOrder.getPurchaseOrderItemsList().stream()
						.filter(w -> w.getProductName().equalsIgnoreCase(e.getProductName()))
						.collect(Collectors.toList());
				List<PurchaseOrderItems> ids = purchaseOrderItemsList.stream()
						.filter(a -> a.getPurchaseItemId().equals(e.getPurchaseItemId())).collect(Collectors.toList());

				if (!products.stream().anyMatch(qq -> qq.getProductName().equalsIgnoreCase(e.getProductName()))) {
					PurchaseOrderItems purchaseOrderItems = new PurchaseOrderItems();
					BeanUtils.copyProperties(e, purchaseOrderItems);
					purchaseOrderItems.setCompanyPurchaseOrder(companyPurchaseOrder);
					PurchaseOrderItems purchaseOrderItem = purchaseOrderItemsRepo.save(purchaseOrderItems);
					PurchaseItemDTO purchaseItemDTO = new PurchaseItemDTO();
					BeanUtils.copyProperties(purchaseOrderItem, purchaseItemDTO);
					purchaseItemList.add(purchaseItemDTO);
				} else if (!ids.isEmpty()) {
					PurchaseOrderItems purchaseOrderItems = ids.get(0);

					BeanUtils.copyProperties(e, purchaseOrderItems);
					purchaseOrderItems.setCompanyPurchaseOrder(companyPurchaseOrder);
					PurchaseOrderItems purchaseOrderItem = purchaseOrderItemsRepo.save(purchaseOrderItems);
					PurchaseItemDTO purchaseItemDTO = new PurchaseItemDTO();
					BeanUtils.copyProperties(purchaseOrderItem, purchaseItemDTO);
					purchaseItemList.add(purchaseItemDTO);
				}

			}
		});
		purchaseItemsDto.setPurchaseItems(purchaseItemList);
		return purchaseItemsDto;
//		log.info("addPurchasedItems method, checking the existence of company purchase order data in the database");
//		CompanyPurchaseOrder companyPurchaseOrder = companyPurchaseOrderRepository.findById(purchaseOrderId)
//				.orElseThrow(() -> new DataNotFoundException(PURCHASE_ORDER_DATA_NOT_FOUND));
//		log.info(
//				"addPurchasedItems method, adding and setting the all the purchase order items to the purchase order items list");
//		purchaseItemsDTO.getPurchaseItems().forEach(purchaseItemDTO -> companyPurchaseOrder.getPurchaseOrderItemsList()
//				.add(PurchaseOrderItems.builder().productName(purchaseItemDTO.getProductName())
//						.quantity(purchaseItemDTO.getQuantity()).amount(purchaseItemDTO.getAmount())
//						.discount(purchaseItemDTO.getDiscount()).tax(purchaseItemDTO.getTax())
//						.payableAmount(purchaseItemDTO.getPayableAmount()).description(purchaseItemDTO.getDescription())
//						.companyPurchaseOrder(companyPurchaseOrder).build()));
//		log.info("addPurchasedItems method, saving and returning the required data");
//		return companyPurchaseOrderRepository.save(companyPurchaseOrder).getPurchaseOrderId();

	}

	@Transactional
	@Override
	public SalesItemsDTO addOrderedItems(SalesItemsDTO salesItemsDTO, Long salesOrderId) {
		SalesItemsDTO salesItemsDto = new SalesItemsDTO();
		CompanySalesOrder salesOrder = companySalesOrderRepository.findById(salesOrderId)
				.orElseThrow(() -> new DataNotFoundException(SALES_ORDER_DATA_NOT_FOUND));
		BeanUtils.copyProperties(salesItemsDTO, salesOrder);

		salesOrder.setTotalReceivableAmount(salesItemsDTO.getTotalPayableAmount());
		CompanySalesOrder companySalesOrder = companySalesOrderRepository.save(salesOrder);
		BeanUtils.copyProperties(companySalesOrder, salesItemsDto);
		salesItemsDto.setTotalPayableAmount(companySalesOrder.getTotalReceivableAmount());
		List<SalesItemDTO> salesItemsdtoList = salesItemsDTO.getSalesItems();
		ArrayList<SalesItemDTO> salesItemListDto = new ArrayList<>();
		List<SalesOrderItems> salesOrderItemsList = companySalesOrder.getSalesOrderItemsList();
		Set<Long> entityItemsIdList = salesOrderItemsList.stream().map(q -> q.getSaleItemId())
				.collect(Collectors.toSet());
		Set<Long> dtoItemIdList = salesItemsdtoList.stream().map(w -> w.getSaleItemId()).collect(Collectors.toSet());
		entityItemsIdList.removeAll(dtoItemIdList);
		salesOrderItemsRepo.deleteAllByIdInBatch(entityItemsIdList);
		salesItemsdtoList.forEach(q -> {
			if (!entityItemsIdList.contains(q.getSaleItemId())) {

				List<SalesOrderItems> productName = companySalesOrder.getSalesOrderItemsList().stream()
						.filter(w -> w.getProductName().equalsIgnoreCase(q.getProductName()))
						.collect(Collectors.toList());
				List<SalesOrderItems> itemId = salesOrderItemsList.stream()
						.filter(a -> a.getSaleItemId().equals(q.getSaleItemId())).collect(Collectors.toList());
				if (!productName.stream().anyMatch(qq -> qq.getProductName().equalsIgnoreCase(q.getProductName()))) {
					SalesOrderItems salesItem = new SalesOrderItems();
					BeanUtils.copyProperties(q, salesItem);
					salesItem.setCompanySalesOrder(companySalesOrder);
					salesItem.setReceivableAmount(q.getPayableAmount());
					SalesOrderItems salesOrderItems = salesOrderItemsRepo.save(salesItem);
					SalesItemDTO salesItemDTO = new SalesItemDTO();
					BeanUtils.copyProperties(salesOrderItems, salesItemDTO);
					salesItemDTO.setSaleItemId(salesOrderItems.getSaleItemId());
					salesItemDTO.setPayableAmount(salesOrderItems.getReceivableAmount());

					salesItemListDto.add(salesItemDTO);
				} else if ((!itemId.isEmpty())) {
					SalesOrderItems salesOrderItems = itemId.get(0);
					BeanUtils.copyProperties(q, salesOrderItems);
					if (productName.isEmpty()) {
						salesOrderItems.setProductName(q.getProductName());
					}
					BeanUtils.copyProperties(q, salesOrderItems);
					salesOrderItems.setCompanySalesOrder(companySalesOrder);
					salesOrderItems.setReceivableAmount(q.getPayableAmount());
					SalesOrderItems orderItems = salesOrderItemsRepo.save(salesOrderItems);
					SalesItemDTO salesItemDTO = new SalesItemDTO();
					BeanUtils.copyProperties(orderItems, salesItemDTO);
					salesItemDTO.setSaleItemId(orderItems.getSaleItemId());
					salesItemDTO.setPayableAmount(orderItems.getReceivableAmount());

					salesItemListDto.add(salesItemDTO);

				}
			}
		});
		salesItemsDto.setSalesItems(salesItemListDto);
		salesItemsDto.setCompanyId(companySalesOrder.getCompanyInfo().getCompanyId());
		return salesItemsDto;

//		log.info("addOrderedItems method, checking the existence of company sales order data in the database");
//		CompanySalesOrder companySalesOrder = companySalesOrderRepository.findById(salesOrderId)
//				.orElseThrow(() -> new DataNotFoundException(SALES_ORDER_DATA_NOT_FOUND));
//		log.info(
//				"addOrderedItems method, adding and setting the all the sales order items to the sales order items list");
//		salesItemsDTO.getSalesItems()
//				.forEach(salesItemDTO -> companySalesOrder.getSalesOrderItemsList().add(SalesOrderItems.builder()
//						.productName(salesItemDTO.getProductName()).quantity(salesItemDTO.getQuantity())
//						.amount(salesItemDTO.getAmount()).discount(salesItemDTO.getDiscount())
//						.tax(salesItemDTO.getTax()).receivableAmount(salesItemDTO.getPayableAmount())
//						.description(salesItemDTO.getDescription()).companySalesOrder(companySalesOrder).build()));
//		log.info("addOrderedItems method, saving and returning the required data");
//		return companySalesOrderRepository.save(companySalesOrder).getSalesOrderId();
	}

	@Override
	public WorkOrderDTO createWorkOrder(WorkOrderDTO workOrderDTO, Long companyId) {
		CompanyClientInfo clientInformation = clientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(workOrderDTO.getCompanyClientInfoId(), companyId)
				.orElseThrow(() -> new DataNotFoundException("The client information is not present"));
		EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
				.findByEmployeeInfoIdAndCompanyInfoCompanyId(workOrderDTO.getRequestTo(), companyId);
		CompanyWorkOrder companyWorkOrder = new CompanyWorkOrder();
		BeanUtils.copyProperties(workOrderDTO, companyWorkOrder);
		companyWorkOrder.setStatus("PENDING");
		if (Boolean.FALSE.equals(workOrderDTO.getIsCostEstimated()))
			companyWorkOrder.setEstimatedCost(null);
		companyWorkOrder.setCompanyClientInfo(clientInformation);
		companyWorkOrder.setEmployeePersonalInfo(employeePersonalInfo);
		companyWorkOrder.setCompanyInfo(
				companyInfoRepository.findById(companyId).orElseThrow(() -> new DataNotFoundException(null)));
		List<WorkOrderResources> workOrderResourcesList = new ArrayList<>();
		workOrderDTO.getWorkOrderResources().forEach((i) -> {
			WorkOrderResources workOrderResources = new WorkOrderResources();
			BeanUtils.copyProperties(i, workOrderResources);
			workOrderResourcesList.add(workOrderResources);
		});
		companyWorkOrder.setWorkOrderResourcesList(workOrderResourcesList);
		CompanyWorkOrder save = companyWorkOrderRepository.save(companyWorkOrder);
		WorkOrderDTO responseDTO = new WorkOrderDTO();
		List<WorkOrderResourcesDTO> workOrderResourcesDTOList = new ArrayList<>();
		BeanUtils.copyProperties(save, responseDTO);
		save.getWorkOrderResourcesList().forEach((i) -> {
			WorkOrderResourcesDTO workOrderResources = new WorkOrderResourcesDTO();
			BeanUtils.copyProperties(i, workOrderResources);
			workOrderResourcesDTOList.add(workOrderResources);
		});
		responseDTO.setWorkOrderResources(workOrderResourcesDTOList);
		return responseDTO;
	}

	@Override
	public CompanyPurchaseOrderDTO getPurchaseOrderDetailsById(Long purchaseOrderId) {
		CompanyPurchaseOrder purchaseOrderDetails = companyPurchaseOrderRepository.findById(purchaseOrderId)
				.orElseThrow(() -> new DataNotFoundException("Purchase order details not found"));
		CompanyPurchaseOrderDTO companyPurchaseOrderDTO = new CompanyPurchaseOrderDTO();
		BeanUtils.copyProperties(purchaseOrderDetails, companyPurchaseOrderDTO);
		companyPurchaseOrderDTO.setProductType(ProductType.valueOf(purchaseOrderDetails.getType().toUpperCase()));
		companyPurchaseOrderDTO.setStockGroupName(purchaseOrderDetails.getCompanyStockGroup().getStockGroupName());
//		companyPurchaseOrderDTO.setContactName(contactPersonRepository
//				.findById(purchaseOrderDetails.getVendorContactPersonId())
//				.orElseThrow(() -> new DataNotFoundException("Vendor details not found")).getContactPersonName());
		companyPurchaseOrderDTO.setPurchaseOrderDate(purchaseOrderDetails.getPoDate());

		List<AddressInformationDTO> listOfAddress = new ArrayList<>();
		purchaseOrderDetails.getPurchaseBillingShippingAddressList().forEach((i) -> {
			AddressInformationDTO addressInformationDTO = new AddressInformationDTO();
			BeanUtils.copyProperties(i, addressInformationDTO);
			listOfAddress.add(addressInformationDTO);
		});
		companyPurchaseOrderDTO.setAddressInformationDTO(listOfAddress);

		List<PurchaseItemDTO> listOfPurchaseDetails = new ArrayList<>();
		purchaseOrderDetails.getPurchaseOrderItemsList().forEach((i) -> {
			PurchaseItemDTO purchaseItemDTO = new PurchaseItemDTO();
			BeanUtils.copyProperties(i, purchaseItemDTO);
			purchaseItemDTO.setIsDeletionRequired(i.getPurchaseInvoiceItems().isEmpty());
			listOfPurchaseDetails.add(purchaseItemDTO);
		});
		companyPurchaseOrderDTO.setPurchaseOrderItemsDTO(listOfPurchaseDetails);
		if (purchaseOrderDetails.getVendorId() != null) {
			Optional<CompanyVendorInfo> vendorDetails = companyVendorInfoRepository
					.findById(purchaseOrderDetails.getVendorId());
			if (vendorDetails.isPresent()) {
				companyPurchaseOrderDTO.setVendorName(vendorDetails.get().getVendorName());
				if (vendorDetails.get().getContactPersons() != null) {
					Optional<String> contactDetails = vendorDetails.get().getContactPersons().stream()
							.map(ContactPerson::getContactPersonName).findFirst();
					companyPurchaseOrderDTO.setContactName(contactDetails.isPresent() ? contactDetails.get() : null);
				}
			}
		}
		EmployeePersonalInfo employeeInfo = employeePersonalInfoRepository.findById(purchaseOrderDetails.getCreatedBy())
				.orElseThrow(() -> new DataNotFoundException("Sales Owner Details Not Found"));

		companyPurchaseOrderDTO.setPurchaseOrderOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
		companyPurchaseOrderDTO.setStockGroupId(purchaseOrderDetails.getCompanyStockGroup().getStockGroupId());
		companyPurchaseOrderDTO.setDescription(purchaseOrderDetails.getPurchaseOrderDescription());
		companyPurchaseOrderDTO.setAdjustment(purchaseOrderDetails.getAdjustment());
		return companyPurchaseOrderDTO;
	}

	@Override
	public List<PurchasedOrderDisplayDTO> getAllPurchaseOrder(Long companyId) {
		List<CompanyPurchaseOrder> companyPurchaseOrderList = companyPurchaseOrderRepository
				.findByCompanyInfoCompanyIdAndPurchaseType(companyId, "Indoor");
		List<PurchasedOrderDisplayDTO> companyPurchaseOrderDTOList = new ArrayList<>();
		companyPurchaseOrderList.forEach((i) -> {
			PurchasedOrderDisplayDTO purchasedOrderDisplayDTO = new PurchasedOrderDisplayDTO();
			BeanUtils.copyProperties(i, purchasedOrderDisplayDTO);
			EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
					.findByEmployeeInfoIdAndCompanyInfoCompanyId(i.getCreatedBy(), companyId);
			purchasedOrderDisplayDTO.setPurchaseOrderOwner(employeePersonalInfo.getFirstName());
			if (i.getVendorId() != null) {
				Optional<CompanyVendorInfo> vendorInfoOptional = companyVendorInfoRepository.findById(i.getVendorId());
				purchasedOrderDisplayDTO.setVendorName(
						vendorInfoOptional.isPresent() ? vendorInfoOptional.get().getVendorName() : null);
			}
			purchasedOrderDisplayDTO
					.setContactName("null".equals(i.getVendorContactPersonId()) ? null : i.getVendorContactPersonId());
			companyPurchaseOrderDTOList.add(purchasedOrderDisplayDTO);
		});
		Collections.reverse(companyPurchaseOrderDTOList);
		return companyPurchaseOrderDTOList;
	}

	@Override
	public List<PurchasedOrderDisplayDTO> getAllPurchaseInventory(Long companyId) {
		List<CompanyPurchaseOrder> companyPurchaseOrderList = companyPurchaseOrderRepository
				.findByCompanyInfoCompanyIdAndPurchaseType(companyId, "Outdoor");
		List<PurchasedOrderDisplayDTO> companyPurchaseOrderDTOList = new ArrayList<>();
		companyPurchaseOrderList.forEach((i) -> {
			PurchasedOrderDisplayDTO purchasedOrderDisplayDTO = new PurchasedOrderDisplayDTO();
			BeanUtils.copyProperties(i, purchasedOrderDisplayDTO);
			EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
					.findByEmployeeInfoIdAndCompanyInfoCompanyId(i.getCreatedBy(), companyId);
			purchasedOrderDisplayDTO.setPurchaseOrderOwner(employeePersonalInfo.getFirstName());
			if (i.getVendorId() != null) {
				Optional<CompanyVendorInfo> vendorInfoOptional = companyVendorInfoRepository.findById(i.getVendorId());
				purchasedOrderDisplayDTO.setVendorName(
						vendorInfoOptional.isPresent() ? vendorInfoOptional.get().getVendorName() : null);
			}
			purchasedOrderDisplayDTO
					.setContactName("null".equals(i.getVendorContactPersonId()) ? null : i.getVendorContactPersonId());
			companyPurchaseOrderDTOList.add(purchasedOrderDisplayDTO);
		});
		Collections.reverse(companyPurchaseOrderDTOList);
		return companyPurchaseOrderDTOList;
	}

	@Override
	public List<PurchasedOrderDisplayDTO> getAllSalesDetails(Long companyId) {
		List<CompanySalesOrder> salesOrderDetailsList = companySalesOrderRepository
				.findByCompanyInfoCompanyId(companyId);
		List<PurchasedOrderDisplayDTO> displayDetailsList = Lists.newArrayList();
		salesOrderDetailsList.forEach((i) -> {
			PurchasedOrderDisplayDTO salesOrderDisplayDTO = new PurchasedOrderDisplayDTO();
			BeanUtils.copyProperties(i, salesOrderDisplayDTO);
			EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository.findById(i.getCreatedBy())
			.orElseThrow(() -> new DataNotFoundException("Owner details not found"));
			salesOrderDisplayDTO.setPurchaseOrderOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
			salesOrderDisplayDTO.setPurchaseOrderNumber(i.getSalesOrderId().toString());
			salesOrderDisplayDTO.setTotalPayableAmount(i.getTotalReceivableAmount());
			salesOrderDisplayDTO.setContactName(i.getClientContactPersonDetails() == null ? null
					: i.getClientContactPersonDetails().getFirstName() + " " + i.getClientContactPersonDetails().getLastName());
			if (i.getCompanyClientInfo() != null) {
				salesOrderDisplayDTO.setVendorName(i.getCompanyClientInfo().getClientName());
				salesOrderDisplayDTO.setVendorId(i.getCompanyClientInfo().getClientId().toString());
			}
			displayDetailsList.add(salesOrderDisplayDTO);
		});
		Collections.reverse(displayDetailsList);
		return displayDetailsList;
	}

	@Override
	public CompanyPurchaseOrderDTO getSalesDetailsById(Long salesOrderId) {
		CompanySalesOrder saleOrderDetails = companySalesOrderRepository.findById(salesOrderId)
				.orElseThrow(() -> new DataNotFoundException("Sales Details Not Found"));
		List<AddressInformationDTO> listOfAddress = Lists.newArrayList();
		saleOrderDetails.getSalesBillingShippingAddressList().forEach((i) -> {
			AddressInformationDTO addressInformationDTO = new AddressInformationDTO();
			BeanUtils.copyProperties(i, addressInformationDTO);
			listOfAddress.add(addressInformationDTO);
		});
		List<PurchaseItemDTO> listOfSaleItem = Lists.newArrayList();
		saleOrderDetails.getSalesOrderItemsList().forEach((i) -> {
			PurchaseItemDTO salesItemDTO = new PurchaseItemDTO();
			BeanUtils.copyProperties(i, salesItemDTO);
			salesItemDTO.setPurchaseItemId(i.getSaleItemId());
			salesItemDTO.setPayableAmount(i.getReceivableAmount());
			salesItemDTO.setIsDeletionRequired(i.getSalesInvoiceItemsList().isEmpty());
			listOfSaleItem.add(salesItemDTO);
		});

		return CompanyPurchaseOrderDTO.builder()
				.purchaseOrderOwner(employeePersonalInfoRepository.findById(saleOrderDetails.getCreatedBy())
						.orElseThrow(() -> new DataNotFoundException("Sales Owner Details Not Found")).getFirstName())
				.productType(saleOrderDetails.getType() == null ? null
						: ProductType
								.valueOf(Optional.ofNullable(saleOrderDetails.getType().toUpperCase()).orElseThrow()))
				.stockGroupName(saleOrderDetails.getCompanyStockGroup().getStockGroupName())
				.subject(saleOrderDetails.getSubject()).customerNumber(saleOrderDetails.getCustomerNumber())
				.dueDate(saleOrderDetails.getDueDate())
				.contactName(saleOrderDetails.getClientContactPersonDetails() == null ? null
						: saleOrderDetails.getClientContactPersonDetails().getFirstName() + " " + saleOrderDetails.getClientContactPersonDetails().getLastName())
				.exciseDuty(saleOrderDetails.getExciseDuty()).carrier(saleOrderDetails.getCarrier())
				.status(saleOrderDetails.getStatus()).salesCommission(saleOrderDetails.getSalesCommission())
				.addressInformationDTO(listOfAddress).purchaseOrderItemsDTO(listOfSaleItem)
				.clientId(saleOrderDetails.getCompanyClientInfo() == null ? null
						: saleOrderDetails.getCompanyClientInfo().getClientId())
				.clientName(saleOrderDetails.getCompanyClientInfo() == null ? null
						: saleOrderDetails.getCompanyClientInfo().getClientName())
				.purchaseOrderId(saleOrderDetails.getSalesOrderId())
				.stockGroupId(saleOrderDetails.getCompanyStockGroup().getStockGroupId())
				.description(saleOrderDetails.getSaleOrderDescription())
				.contactId(saleOrderDetails.getClientContactPersonDetails() == null ? null
						: saleOrderDetails.getClientContactPersonDetails().getContactPersonId())
				.purchaseOrderNumber(saleOrderDetails.getPurchaseOrder()).pending(saleOrderDetails.getPending())
				.adjustment(saleOrderDetails.getAdjustment()).build();

	}

	@Override
	public String updateDescriptionOfPurchaseOrder(AccountDescriptionDTO accountDescriptionDTO, Long employeeInfoId) {

		log.info("Add description for sale and purchase order against id ::", accountDescriptionDTO.getObjectId());
		CompanyPurchaseOrder purchaseOrder = companyPurchaseOrderRepository
				.findById(accountDescriptionDTO.getObjectId())
				.orElseThrow(() -> new DataNotFoundException("Purchase order data is not found"));
		purchaseOrder.setPurchaseOrderDescription(accountDescriptionDTO.getDescription());
		companyPurchaseOrderRepository.save(purchaseOrder);

		return "Added description successfully";

	}

	@Override
	public String updateDescriptionOfSalesOrder(AccountDescriptionDTO accountDescriptionDTO, Long employeeInfoId) {

		log.info("Add description for sale and purchase order against id ::", accountDescriptionDTO.getObjectId());
		CompanySalesOrder companySalesOrder = companySalesOrderRepository.findById(accountDescriptionDTO.getObjectId())
				.orElseThrow(() -> new DataNotFoundException("Sales order data is not found"));
		companySalesOrder.setSaleOrderDescription(accountDescriptionDTO.getDescription());
		companySalesOrderRepository.save(companySalesOrder);
		return "Added description successfully";

	}

	@Override
	public TermsAndConditionDTO getTermsAndConditionDetails(Long companyId, TermsAndConditionDTO termsAndConditionDTO) {
		List<CompanyTermsAndConditions> companyTermsAndConditionsList = companyTermsAndConditionsRepository
				.findByCompanyInfoCompanyIdAndTypeIgnoreCase(companyId, termsAndConditionDTO.getType());
		if (companyTermsAndConditionsList.isEmpty()) {
			throw new DataNotFoundException("Terms and Condition Not Configured");
		}
		BeanUtils.copyProperties(companyTermsAndConditionsList.get(companyTermsAndConditionsList.size() - 1),
				termsAndConditionDTO);
		return termsAndConditionDTO;
	}
}
