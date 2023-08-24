package com.te.flinko.controller.account.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.mongo.UpdateAccountTicketDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.mongo.AccountTicketsService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping(path = "/api/v1/account")
@RestController
public class CompanyAccountTicketController extends BaseConfigController {

	@Autowired
	AccountTicketsService accountTicketsService;

	@GetMapping("/tickets/sale-and-purchase/{companyId}")
	public ResponseEntity<SuccessResponse> getSalesAndPurchaseTicketsList(@PathVariable Long companyId) {

		log.info("Get the list of sales and purchase tickets against companyId:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Account tickets sales&Purchase details list")
						.data(accountTicketsService.getAccountSaleAndPurchaseTicketList(companyId, getUserId()))
						.build());

	}

	@GetMapping("/tickets/sale-and-purchase/details/{companyId}/{objectTicketId}")
	public ResponseEntity<SuccessResponse> getSalesAndPurchaseTicketsDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String objectTicketId) {

		log.info("Get the tickets details and history of sales and purchase against companyId:: ", companyId);

		return ResponseEntity
				.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
						.message("Details of account sales and purchase").data(accountTicketsService
								.getAccountSaleAndPurchaseDetailsAndHistory(companyId, objectTicketId, getUserId()))
						.build());

	}

	@GetMapping("/tickets/employee/{companyId}")
	public ResponseEntity<SuccessResponse> getEmployeeTicketsList(@PathVariable Long companyId) {

		log.info("Get the list of employee tickets against companyId:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("List of account employee tickets")
						.data(accountTicketsService.getEmployeeTicketList(companyId, getUserId())).build());

	}

	@GetMapping("/tickets/employee/details/{companyId}/{objectTicketId}")
	public ResponseEntity<SuccessResponse> getEmployeeAccountTicketsDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String objectTicketId) {

		log.info("Get the details and history of employye tickets against companyId:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message("Details of account employee tickets")
				.data(accountTicketsService.getAccountEmployeeDetailsAndHistory(companyId, objectTicketId, getUserId()))
				.build());

	}

	@GetMapping("/tickets/others/{companyId}")
	public ResponseEntity<SuccessResponse> getAccountOthersTicketsList(@PathVariable Long companyId) {

		log.info("Get the list of others tickets against companyId:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("List of account others tickets")
						.data(accountTicketsService.getOthersTicketList(companyId, getUserId())).build());

	}

	@GetMapping("/tickets/others/details/{companyId}/{objectTicketId}")
	public ResponseEntity<SuccessResponse> getAccountOthersTicketsDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String objectTicketId) {

		log.info("Get the details and history of others tickets against companyId:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Details of account others tickets").data(
						accountTicketsService.getAccountOthersDetailsAndHistory(companyId, objectTicketId, getUserId()))
						.build());

	}

	@PutMapping("/tickets/actions/")
	public ResponseEntity<SuccessResponse> updateAccountTicketHistory(
			@RequestBody UpdateAccountTicketDTO updateTicketDTO) {
		log.info("update ticket history details aganist employeeInfo id", getEmployeeInfoId());
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message("Ticket " + updateTicketDTO.getStatus() + " successfully")
				.data(accountTicketsService.updateAccountTicketHistory(updateTicketDTO, getEmployeeInfoId())).build());
	}

}
