package com.te.flinko.controller.it.mongo;

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
import com.te.flinko.dto.it.mongo.UpdateITTicketDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.it.mongo.ITTicketsService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping("/api/v1/it")
@RestController
public class ITTicketsController extends BaseConfigController {

	@Autowired
	ITTicketsService iTTicketsService;

	@GetMapping("/tickets/hardware-allocation/{companyId}")
	public ResponseEntity<SuccessResponse> getITTicketsHardwareAlloctedDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT ticket hardware alloction details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets hardware allocated details list ")
						.data(iTTicketsService.getTicketsHardwareAllocatedDetails(companyId, getUserId())).build());
	}

	@GetMapping("/tickets/software-issues/{companyId}")
	public ResponseEntity<SuccessResponse> getITTicketsSoftwareIssuesDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT tickets software issues details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets software issue details list ")
						.data(iTTicketsService.getTicketsSoftwareIssuesDetails(companyId, getUserId())).build());

	}

	@GetMapping("/tickets/hardware-issues/{companyId}")
	public ResponseEntity<SuccessResponse> getITTicketsHardwareIssuesDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT tickets hardware issues details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets hardware issue details list ")
						.data(iTTicketsService.getTicketsHardwareIssuesDetails(companyId, getUserId())).build());

	}

	@GetMapping("/tickets/e-mail/id-card/{companyId}")
	public ResponseEntity<SuccessResponse> getITTicketsEmailDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT tickets E-mail details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets E-mail details list ")
						.data(iTTicketsService.getTicketsEmailDetails(companyId, getUserId())).build());

	}

	@GetMapping("/tickets/hardware-allocation/details/{companyId}/{id}")
	public ResponseEntity<SuccessResponse> getITTicketsHardwareAlloctedDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String id) {
		log.info("Get the list of IT ticket hardware alloction details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets hardware allocated details ")
						.data(iTTicketsService.getTicketsHardwareAllocatedDetailsAndHistory(companyId, id, getUserId()))
						.build());
	}

	@GetMapping("/tickets/software-issues/details/{companyId}/{id}")
	public ResponseEntity<SuccessResponse> getITTicketsSoftwareIssuesDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String id) {
		log.info("Get the list of IT ticket software issues details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets software issues details ")
						.data(iTTicketsService.getTicketsSoftwareIssuesDetailsAndHistory(companyId, id, getUserId()))
						.build());
	}

	@GetMapping("/tickets/hardware-issues/details/{companyId}/{id}")
	public ResponseEntity<SuccessResponse> getITTicketsHardwareIssuesDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String id) {
		log.info("Get the list of IT ticket hardware issues details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets hardware issues details  ")
						.data(iTTicketsService.getTicketsHardwareIssuesDetailsAndHistory(companyId, id, getUserId()))
						.build());
	}

	@GetMapping("/tickets/e-mail/id-card/details/{companyId}/{id}")
	public ResponseEntity<SuccessResponse> getITTicketsEmailDetailsDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String id) {
		log.info("Get the list of IT ticket email details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT tickets email and Id cards details")
						.data(iTTicketsService.getTicketsEmailAndIdCardDetailsAndHistory(companyId, id, getUserId()))
						.build());
	}

	@PutMapping("/tickets/actions/")
	public ResponseEntity<SuccessResponse> updateITTicketHistory(@RequestBody UpdateITTicketDTO updateTicketDTO) {
		log.info("update ticket history details aganist employeeInfo id");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false)
						.message("Ticket " + updateTicketDTO.getStatus() + " successfully")
						.data(iTTicketsService.updateTicketHistory(updateTicketDTO, getEmployeeInfoId())).build());
	}
	


}
