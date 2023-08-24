package com.te.flinko.controller.helpandsupport.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.constants.admin.AdminConstants;
import com.te.flinko.dto.helpandsupport.mongo.CompanyTicketDto;
import com.te.flinko.dto.helpandsupport.mongo.RaiseTicketDto;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.helpandsupport.mongo.RaiseTicketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@Slf4j
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class CompanyTicketRaiseController extends BaseConfigController {

	@Autowired
	RaiseTicketService raiseTicketService;

	@PostMapping()
	public ResponseEntity<SuccessResponse> createTickets(@RequestPart(required = false) List<MultipartFile> file,
			@RequestParam String data) throws JsonProcessingException {

		log.info("controller method createTickets is called");
		boolean createTickets = raiseTicketService.createTickets(getCompanyId(), getEmployeeInfoId(), file,
				BeanCopy.jsonProperties(data, RaiseTicketDto.class));
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(!createTickets)
				.message(createTickets ? "Ticket raised successfully" : "Failed to ticket raised").build());
	}

	@GetMapping("/{department}")
	public ResponseEntity<SuccessResponse> getAllReportingManagaer(@PathVariable String department) {
		log.info("List of Reporting Manager with respect to department");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message(AdminConstants.DATA_FETCHED_SUCCEFULLY)
						.data(raiseTicketService.getAllReportingManagaer(getCompanyId(), department)).build());

	}

	@GetMapping("/allProductsList")
	public ResponseEntity<SuccessResponse> getProducts() {
		log.info("Products List");
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message("Products Lists").data(raiseTicketService.getProducts(getCompanyId())).build());
	}

	@PostMapping("/delayedDepartmentTickets")
	public ResponseEntity<SuccessResponse> getDelayedTickets(@RequestBody CompanyTicketDto companyTicketDto) {
		log.info("Delayed Tickets List");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Delayed Tickets List")
						.data(raiseTicketService.getDelayedTickets(getCompanyId(), companyTicketDto)).build());
	}

	@PostMapping("/allDepartmentTickets")
	public ResponseEntity<SuccessResponse> getAllTickets(@RequestBody CompanyTicketDto companyTicketDto) {
		log.info("All Tickets List");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Delayed Tickets List")
						.data(raiseTicketService.getAllTickets(getCompanyId(), companyTicketDto)).build());
	}

	@GetMapping("/ticketDetails/{department}/{id}")
	public ResponseEntity<SuccessResponse> getTicketsDetails(@PathVariable String id, @PathVariable String department) {
		log.info("Products List");
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message("Ticket Details List").data(raiseTicketService.getTicketsDetails(getCompanyId(), id, department)).build());
	}
}
