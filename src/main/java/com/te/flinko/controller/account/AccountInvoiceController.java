package com.te.flinko.controller.account;

import static com.te.flinko.common.account.AccountConstants.PURCHASE_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_ATTACHMENT_DETAILS_SAVED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_DESCRIPTION_DETAILS_SAVED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_INVOICE_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.SALES_ORDER_DETAILS_FETCH_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.SALES_ORDER_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.SALES_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.SALES_INVOICE_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.SALES_INVOICE_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.SALES_INVOICE_DETAILS_SAVED_SUCCESSFULLY;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.AddressInformationDTO;
import com.te.flinko.dto.account.AttachmentsDTO;
import com.te.flinko.dto.account.DescriptionDTO;
import com.te.flinko.dto.account.InvoiceDetailsDTO;
import com.te.flinko.dto.account.InvoiceItemDTO;
import com.te.flinko.dto.account.PurchaseInvoiceDTO;
import com.te.flinko.dto.account.PurchaseInvoiceDetailsByIdDto;
import com.te.flinko.dto.account.SalesInvoiceDTO;
import com.te.flinko.dto.account.SalesOrderDropdownDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountInvoiceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin(origins = "https://hrms.flinko.app")
/**
 * 
 * @author Ravindra
 *
 */
public class AccountInvoiceController extends BaseConfigController {

	@Autowired
	AccountInvoiceService service;

	@PostMapping("/invoice-details")
	public ResponseEntity<SuccessResponse> invoiceDetails(@Valid @RequestBody InvoiceDetailsDTO invoiceDetailsDto) {
		log.info("invoice details method execution started");
		InvoiceDetailsDTO invoiceDetails = service.invoiceDetails(invoiceDetailsDto);
		if(invoiceDetailsDto.getPurchaseInvoiceId()==null) {
			return new ResponseEntity<>(
					new SuccessResponse(false, PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY, invoiceDetails),
					HttpStatus.ACCEPTED);
		}
			return new ResponseEntity<>(
					new SuccessResponse(false, "Purchase invoice details updated successfully", invoiceDetails),
					HttpStatus.ACCEPTED);
	}

	@GetMapping("/address-information")
	public ResponseEntity<SuccessResponse> addressInformation(@RequestParam Long purchaseOrderId,
			@RequestParam Long purchaseInvoiceId) {
		log.info("addressInformation method execution started");
		ArrayList<AddressInformationDTO> addressInformation = service.addressInformation(purchaseInvoiceId,
				purchaseOrderId);
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY, addressInformation),
				HttpStatus.ACCEPTED);

	}
	@GetMapping("invoice-items")
	public ResponseEntity<SuccessResponse> invoiceItemsList(@RequestParam Long purchaseOrderId){
		log.info("invoiceItems method execution started");
		InvoiceItemDTO invoiceItemsList = service.invoiceItemsList(getCompanyId(),purchaseOrderId);
		return new ResponseEntity<>(
				new SuccessResponse(false, "Purchase invoice items fetched successfully", invoiceItemsList),
				HttpStatus.ACCEPTED);
	}
	

	@PostMapping("/description")
	public ResponseEntity<SuccessResponse> description(@RequestBody DescriptionDTO descriptionDto) {
		log.info("addressInformation method execution started");
		DescriptionDTO description = service.description(descriptionDto);
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_DESCRIPTION_DETAILS_SAVED_SUCCESSFULLY, description),
				HttpStatus.ACCEPTED);
	}

	@PostMapping("/invoice-items")
	public ResponseEntity<SuccessResponse> invoiceItems(@RequestBody InvoiceItemDTO invoiceItemsDTO) {
		log.info("invoceItems method execution started");
		InvoiceItemDTO invoceItems = service.invoiceItems(invoiceItemsDTO);
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_INVOICE_DETAILS_SAVED_SUCCESSFULLY, invoceItems),
				HttpStatus.ACCEPTED);

	}

	@PostMapping("/attachments")
	public ResponseEntity<SuccessResponse> attachments(@RequestBody AttachmentsDTO attachmentsDTO) {
		log.info("attachments method execution started");

		AttachmentsDTO attachments = service.attachments(attachmentsDTO);
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_ATTACHMENT_DETAILS_SAVED_SUCCESSFULLY, attachments),
				HttpStatus.ACCEPTED);

	}

	@GetMapping("/sales-order-dropdown")
	public ResponseEntity<SuccessResponse> salesOrderDropdown() {
		log.info("salesOrderDropdown method execution started");

		List<SalesOrderDropdownDTO> salesOrderDropdown = service.salesOrderDropdown(getCompanyId());
		if (salesOrderDropdown.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, SALES_ORDER_DETAILS_NOT_FOUND, salesOrderDropdown),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(
				new SuccessResponse(false, SALES_ORDER_DETAILS_FETCH_SUCCESSFULLY, salesOrderDropdown), HttpStatus.OK);
	}

	@GetMapping("/purchase-invoice")
	public ResponseEntity<SuccessResponse> purchaseInvoice() {
		log.info("purchaseInvoice method execution started");
		ArrayList<PurchaseInvoiceDTO> purchaseInvoice = service.purchaseInvoice(getCompanyId());
		if (purchaseInvoice.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, PURCHASE_INVOICE_DETAILS_NOT_FOUND, purchaseInvoice),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY, purchaseInvoice),
				HttpStatus.OK);
	}

	@GetMapping("/purchase-invoice-by-id")
	public ResponseEntity<SuccessResponse> purchaseInvoiceById(@RequestParam Long purchaseInvoiceId) {
		log.info("purchaseInvoiceById method execution started");
		PurchaseInvoiceDetailsByIdDto purchaseInvoiceById = service.purchaseInvoiceById(getCompanyId(),
				purchaseInvoiceId);
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY, purchaseInvoiceById),
				HttpStatus.OK);
	}
	@GetMapping("invoice-items-by-purchase-id")
	public ResponseEntity<SuccessResponse> invoiceItemsListByPurchaseInvoiceId(@RequestParam Long purchaseInvoiceId){
		log.info("invoiceItems method execution started");
		InvoiceItemDTO invoiceItemsList = service.invoiceItemsListByPurchaseInvoiceId(getCompanyId(),purchaseInvoiceId);
		return new ResponseEntity<>(
				new SuccessResponse(false, PURCHASE_INVOICE_DETAILS_FETCHED_SUCCESSFULLY, invoiceItemsList),
				HttpStatus.ACCEPTED);
	}
	//sales apis
	@GetMapping("sales-invoice-items")
	public ResponseEntity<SuccessResponse> salesItemsList(@RequestParam Long salesOrderId){
		log.info("salesItemsList method execution started");
		InvoiceItemDTO invoiceItemsList = service.salesItemsList(getCompanyId(),salesOrderId);
		return new ResponseEntity<>(
				new SuccessResponse(false, "Sales invoice items fetched successfully", invoiceItemsList),
				HttpStatus.ACCEPTED);
	}
	@GetMapping("invoice-items-by-sales-id")
	public ResponseEntity<SuccessResponse> invoiceItemsListBySalesInvoiceId(@RequestParam Long salesInvoiceId){
		log.info("invoiceItemsListBySalesInvoiceId method execution started");
		 InvoiceItemDTO invoiceItemsListBySalesInvoiceId = service.invoiceItemsListBySalesInvoiceId(getCompanyId(),salesInvoiceId);
		return new ResponseEntity<>(
				new SuccessResponse(false, "Sales invoice items fetched successfully", invoiceItemsListBySalesInvoiceId),
				HttpStatus.ACCEPTED);
	
	}
	@GetMapping("/sales-address-information")
	public ResponseEntity<SuccessResponse> salesAddressInformation(@RequestParam Long salesOrderId,
			@RequestParam Long salesInvoiceId) {
		log.info("addressInformation method execution started");
		ArrayList<AddressInformationDTO> addressInformation = service.salesAddressInformation(salesInvoiceId,
				salesOrderId);
		return new ResponseEntity<>(
				new SuccessResponse(false, SALES_ADDRESS_DETAILS_FETCHED_SUCCESSFULLY, addressInformation),
				HttpStatus.OK);
	}

	@GetMapping("/sales-invoice")
	public ResponseEntity<SuccessResponse> salesInvoice() {
		log.info("salesInvoice method execution started");
	ArrayList<SalesInvoiceDTO> salesInvoice = service.salesInvoice(getCompanyId());
		if (salesInvoice.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, SALES_INVOICE_DETAILS_NOT_FOUND, salesInvoice),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, SALES_INVOICE_DETAILS_FETCHED_SUCCESSFULLY, salesInvoice),
				HttpStatus.OK);
	}
	@PostMapping("/sales-invoice-items")
	public ResponseEntity<SuccessResponse> salesInvoiceItems(@RequestBody InvoiceItemDTO invoiceItemsDTO) {
		log.info("salesInvoiceItems method execution started");
		InvoiceItemDTO invoceItems = service.salesInvoiceItems(invoiceItemsDTO);
		return new ResponseEntity<>(
				new SuccessResponse(false, SALES_INVOICE_DETAILS_SAVED_SUCCESSFULLY, invoceItems),
				HttpStatus.ACCEPTED);

	}
	@GetMapping("/sales-invoice-by-id")
	public ResponseEntity<SuccessResponse> salesInvoiceById(@RequestParam Long salesInvoiceId) {
		log.info("purchaseInvoiceById method execution started");
           PurchaseInvoiceDetailsByIdDto salesInvoiceById = service.salesInvoiceById(getCompanyId(),salesInvoiceId);
		return new ResponseEntity<>(
				new SuccessResponse(false, SALES_INVOICE_DETAILS_FETCHED_SUCCESSFULLY, salesInvoiceById),
				HttpStatus.OK);
	}

	 
	
}
