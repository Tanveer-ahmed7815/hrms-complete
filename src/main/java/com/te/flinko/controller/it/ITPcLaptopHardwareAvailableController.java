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
import com.te.flinko.service.it.ITPcLaptopHardwareAvailableService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping("/api/v1/it")
@RequiredArgsConstructor
@RestController
public class ITPcLaptopHardwareAvailableController extends BaseConfigController {

	@Autowired
	ITPcLaptopHardwareAvailableService hardwareAvailableService;

	@GetMapping("/{companyId}")
	public ResponseEntity<SuccessResponse> getITHardwareAvaliabilityDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT hardware avaliability details");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT PC Laptop details Lists")
						.data(hardwareAvailableService.getITPCLaptopDetails(companyId)).build());

	}

	@GetMapping("/pc-laptop/{companyId}/{serialNumber}")
	public ResponseEntity<SuccessResponse> getHardwareAvaliabilityDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String serialNumber) {
		log.info("Get the list of IT hardware avaliability details and History aganist company id::" + companyId
				+ " and serial Number:: " + serialNumber);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT PC Laptop details and history")
						.data(hardwareAvailableService.getITPCLaptopDetailsAndHistory(companyId, serialNumber)).build());

	}

	@GetMapping("/other-items/{companyId}")
	public ResponseEntity<SuccessResponse> getOtherItemsDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT other items details aganist company id:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message("IT other items details List ").data(hardwareAvailableService.getOtherItems(companyId)).build());

	}

	@GetMapping("/other-items/{companyId}/{indentificationNumber}")
	public ResponseEntity<SuccessResponse> getOtherItemsDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String indentificationNumber) {
		log.info("Get the list of IT other items details and History aganist company id:: ", companyId,
				"and indentification number :: ", indentificationNumber);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT other items details and history ")
						.data(hardwareAvailableService.getOtherItemsDetailsAndHistory(companyId, indentificationNumber))
						.build());

	}

	
	

}
