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
import com.te.flinko.service.it.ITPcLaptopHardwareAlloctedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping("/api/v1/it")
@RequiredArgsConstructor
@RestController
public class ITPcLaptopHardwareAllocatedController extends BaseConfigController {

	@Autowired
	ITPcLaptopHardwareAlloctedService hardwareAlloctedService;

	@GetMapping("/hardware-allocted/{companyId}")
	public ResponseEntity<SuccessResponse> getITHardwareAllocatedDetails(@PathVariable Long companyId) {
		log.info("Get list of IT PC Laptop allocted details against company id::", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT PC Laptop allocated details Lists")
						.data(hardwareAlloctedService.getITPCLaptopAlloctedDetails(companyId)).build());

	}

	@GetMapping("/hardware-allocted/{companyId}/{serialNumber}")
	public ResponseEntity<SuccessResponse> getITHardwareAllocatedDetailsAndHistory(@PathVariable Long companyId,
			@PathVariable String serialNumber) {
		log.info("Get list of IT PC Laptop allocted details and history against company id::" + companyId
				+ "and serial number::" + serialNumber);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT PC Laptop allocated details and history")
						.data(hardwareAlloctedService.getITPCLaptopAlloctedDetailsAndHistory(companyId, serialNumber))
						.build());

	}

	@GetMapping("/hardware-allocted/other-items/{companyId}")
	public ResponseEntity<SuccessResponse> getITHardwareAllocatedOtherItemsDetails(@PathVariable Long companyId) {
		log.info("Get list of IT PC Laptop allocted other items details against company id::" + companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT PC Laptop allocated other items details ")
						.data(hardwareAlloctedService.getAllocatedOtherItemsDetails(companyId)).build());

	}

	@GetMapping("/hardware-allocted/other-items/{companyId}/{indentificationNumber}")
	public ResponseEntity<SuccessResponse> getITHardwareAllocatedOtherItemsDetailsAndHistory(
			@PathVariable Long companyId, @PathVariable String indentificationNumber) {
		log.info("Get list of IT PC Laptop allocted other items details against company id::" + companyId);

		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message("IT PC Laptop allocated details and history")
				.data(hardwareAlloctedService.getAllocatedOtherItemsDetailsAndHistory(companyId, indentificationNumber))
				.build());

	}

}
