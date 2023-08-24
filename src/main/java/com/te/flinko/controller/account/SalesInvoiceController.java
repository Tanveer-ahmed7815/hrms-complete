package com.te.flinko.controller.account;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.SalesInvoiceDetailsDTO;
import com.te.flinko.dto.account.SalesInvoiceOrderDropdownDTO;
import com.te.flinko.dto.account.SalesOrderItemDropdownDTO;
import com.te.flinko.dto.account.SalesShippingBillingAddressDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.SalesInvoiceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/salesInvoice")
@CrossOrigin(origins = "https://hrms.flinko.app")
public class SalesInvoiceController extends BaseConfigController {

	@Autowired
	private SalesInvoiceService salesInvoiceService;

	@PostMapping("/saveInvoiceDetails")
	ResponseEntity<SuccessResponse> saveInvoiceDetails( @Valid @RequestBody SalesInvoiceDetailsDTO invoiceDetailsDTO) {
		Boolean isUpdate = invoiceDetailsDTO.getSalesInvoiceId() != null;
		SalesInvoiceDetailsDTO dto = salesInvoiceService.saveInvoiceDetails(getUserId(), getCompanyId(), invoiceDetailsDTO);
		log.info("Info Saved Successfully");
		if(Boolean.FALSE.equals(isUpdate)) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Sales Invoice Details Saved Successfully", dto),
					HttpStatus.ACCEPTED);
		}
			return new ResponseEntity<>(
					new SuccessResponse(false, "Sales Invoice Details Updated Successfully", dto),
					HttpStatus.ACCEPTED);
	}
	

	@PutMapping("/addAttachments/{salesInvoiceId}")
	ResponseEntity<SuccessResponse> addAttachments(@RequestBody SalesInvoiceDetailsDTO invoiceDetailsDTO,
			@PathVariable Long salesInvoiceId) {

		SalesInvoiceDetailsDTO dto = salesInvoiceService.addAttachments(getUserId(), getCompanyId(), invoiceDetailsDTO,
				salesInvoiceId);
		log.info("Attachment Saved Successfully");
		return new ResponseEntity<>(
				SuccessResponse.builder().data(dto).error(false).message("Attachment Saved/Updated Successfully").build(),
				HttpStatus.OK);

	}

	@PutMapping("/addTermsAndConditions/{salesInvoiceId}")
	ResponseEntity<SuccessResponse> addTermsAndConditions(@RequestBody SalesInvoiceDetailsDTO invoiceDetailsDTO,
			@PathVariable Long salesInvoiceId) {

		SalesInvoiceDetailsDTO dto = salesInvoiceService.addTermsAndConditions(getUserId(), getCompanyId(),
				invoiceDetailsDTO, salesInvoiceId);
		log.info("TermsAndConditions Saved Successfully");
		return new ResponseEntity<>(SuccessResponse.builder().data(dto).error(false)
				.message("Terms And Conditions Saved Successfully").build(), HttpStatus.OK);

	}

	@PutMapping("/adddescription/{salesInvoiceId}")
	ResponseEntity<SuccessResponse> adddescription(@RequestBody SalesInvoiceDetailsDTO invoiceDetailsDTO,
			@PathVariable Long salesInvoiceId) {

		SalesInvoiceDetailsDTO dto = salesInvoiceService.adddescription(getUserId(), getCompanyId(), invoiceDetailsDTO,
				salesInvoiceId);
		log.info("description Saved Successfully");
		return new ResponseEntity<>(
				SuccessResponse.builder().data(dto).error(false).message("Invoice Description Saved/Updated Successfully").build(),
				HttpStatus.OK);

	}

	@PutMapping("/saveInvoiceItems/{salesInvoiceId}")
	ResponseEntity<SuccessResponse> saveInvoiceItems(@RequestBody SalesInvoiceDetailsDTO invoiceDetailsDTO,
			@PathVariable Long salesInvoiceId) {

		SalesInvoiceDetailsDTO dto = salesInvoiceService.saveInvoiceItems(getUserId(), getCompanyId(), invoiceDetailsDTO,
				salesInvoiceId);
		log.info("Invoice Items Details Saved Successfully");
		return new ResponseEntity<>(SuccessResponse.builder().data(dto).error(false)
				.message("Invoice Items Saved/Updated Successfully").build(), HttpStatus.OK);

	}

	@GetMapping("/getSalesOrderIdList")
	ResponseEntity<SuccessResponse> getSalesOrderIdList() {

		List<SalesInvoiceOrderDropdownDTO> dto = salesInvoiceService.getSalesOrderId(getCompanyId());
		if (!dto.isEmpty()) {
			log.info("Sales Order List Fetched Successfully");
			return new ResponseEntity<>(SuccessResponse.builder().data(dto).error(false)
					.message("Sales Order List Fetched Successfully").build(), HttpStatus.OK);
		} else {
			log.warn("Sales Order List Not Present");
			return new ResponseEntity<>(
					SuccessResponse.builder().data(dto).error(true).message("Sales Order List Not Present").build(),
					HttpStatus.OK);
		}

	}

	@PostMapping("/saveAddressInfo/{salesInvoiceId}")
	ResponseEntity<SuccessResponse> saveAddressInfo(
			@RequestBody List<SalesShippingBillingAddressDTO> salesShippingBillingAddressDTOList,
			@PathVariable Long salesInvoiceId) {

		List<SalesShippingBillingAddressDTO> dto = salesInvoiceService.saveAddressInfo(getUserId(), getCompanyId(),
				salesShippingBillingAddressDTOList, salesInvoiceId);
		return new ResponseEntity<>(
				SuccessResponse.builder().data(dto).error(false).message("Address Fetched Successfully").build(),
				HttpStatus.OK);

	}

	@GetMapping("/getSalesOrderItemsList/{salesOrderId}")
	ResponseEntity<SuccessResponse> getSalesOrderItemsList(@PathVariable Long salesOrderId) {

		List<SalesOrderItemDropdownDTO> dto = salesInvoiceService.getSalesOrderItemsList(salesOrderId);
		if (!dto.isEmpty()) {
			log.info("Sales Order Item List Fetched Successfully");
			return new ResponseEntity<>(SuccessResponse.builder().data(dto).error(false)
					.message("Sales Order Item List Fetched Successfully").build(), HttpStatus.OK);
		} else {
			log.warn("Sales Order Item List Not Present");
			return new ResponseEntity<>(SuccessResponse.builder().data(dto).error(true)
					.message("Sales Order Item List Not Present").build(), HttpStatus.OK);
		}

	}

	@PostMapping("/saveSalesOrderItems/{salesInvoiceId}")
	ResponseEntity<SuccessResponse> saveSalesOrderItems(@PathVariable Long salesInvoiceId,
			@RequestBody List<SalesOrderItemDropdownDTO> salesOrderItemDropdownDTOList) {

		List<SalesOrderItemDropdownDTO> items = salesInvoiceService.saveSalesOrderItems(getCompanyId(), salesInvoiceId,
				salesOrderItemDropdownDTOList);
		return new ResponseEntity<>(
				SuccessResponse.builder().data(items).error(false).message("Invoice Items Saved/Updated Successfully").build(),
				HttpStatus.OK);

	}

}
