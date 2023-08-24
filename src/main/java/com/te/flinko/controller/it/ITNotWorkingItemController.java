package com.te.flinko.controller.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.it.ITPcLaptopNonWorkingItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping("/api/v1/it")
@RequiredArgsConstructor
@RestController
public class ITNotWorkingItemController extends BaseConfigController {

	@Autowired
	ITPcLaptopNonWorkingItemService nonWorkingItemService;

	@GetMapping("/not-working/{companyId}")
	public ResponseEntity<SuccessResponse> getNotWorkingPcLaptopDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT not working details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT not working details list ")
						.data(nonWorkingItemService.getNotWorkingPCLaptopDetails(companyId)).build());

	}

	@GetMapping("/not-working/{companyId}/{serialNumber}")
	public ResponseEntity<SuccessResponse> getNotWorkingPcLaptopDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String serialNumber) {
		log.info("Get the list of IT not working details and history aganist company id:: ",
				companyId + " and serial numnber ::", serialNumber);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT not working details and history ")
						.data(nonWorkingItemService.getNotWorkingPCLaptopDetailsAndHistory(companyId, serialNumber))
						.build());

	}

	@GetMapping("/not-working/other-items/{companyId}")
	public ResponseEntity<SuccessResponse> getNotWorkingOtherItems(@PathVariable Long companyId) {
		log.info("Get the list of IT not working other items details aganist company id:: ", companyId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT not working details list ")
						.data(nonWorkingItemService.getNotWorkingOtherItems(companyId)).build());

	}

	@GetMapping("/not-working/other-items/{companyId}/{indentificationNumber}")
	public ResponseEntity<SuccessResponse> getNotWorkingOtherItemsAndHistory(@PathVariable Long companyId,
			@PathVariable String indentificationNumber) {
		log.info("Get the list of IT not working other items details and history aganist company id:: " + companyId
				+ " and ::" + indentificationNumber);
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message("IT not working details list ")
				.data(nonWorkingItemService.getNotWorkingOtherItemsDetailsAndHistory(companyId, indentificationNumber))
				.build());

	}

}
