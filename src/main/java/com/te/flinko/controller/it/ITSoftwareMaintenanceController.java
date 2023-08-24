package com.te.flinko.controller.it;

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
import com.te.flinko.dto.admindept.PcLaptopSoftwareDetailsDTO;
import com.te.flinko.dto.it.mongo.PcLaptopSoftwareRenewalDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.it.ITSoftwareMaintenanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping("/api/v1/it")
@RequiredArgsConstructor
@RestController
public class ITSoftwareMaintenanceController extends BaseConfigController {
	@Autowired
	ITSoftwareMaintenanceService itSoftwareMaintenanceService;

	@GetMapping("/software-maintenance/{companyId}")
	public ResponseEntity<SuccessResponse> getSoftwaremaintenanceDetails(@PathVariable Long companyId) {
		log.info("Get the list of IT software maintence details and history aganist company id:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT software maintence details list ")
						.data(itSoftwareMaintenanceService.getITSoftwareMaintenanceDetails(companyId)).build());

	}

	@GetMapping("/software-maintenance/{companyId}/{serialNumber}")
	public ResponseEntity<SuccessResponse> getSoftwaremaintenanceDetailsList(@PathVariable Long companyId,
			@PathVariable String serialNumber) {
		log.info("Get the list of IT software maintence details and history aganist company id:: ", companyId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("IT software maintence details list ")
						.data(itSoftwareMaintenanceService.getITSoftwareMaintenanceDetailsList(companyId, serialNumber))
						.build());

	}

	@PostMapping("/software-maintenance/add-new-software/{serialNumber}")
	public ResponseEntity<SuccessResponse> createOrUpdateNewSofwares(
			@RequestBody PcLaptopSoftwareDetailsDTO laptopSoftwareDetailsDTO, @PathVariable String serialNumber) {
		log.info("Create or update  the list of IT software  details against company id:: ", getCompanyId());

		PcLaptopSoftwareDetailsDTO createOrUpdateNewSoftwares = itSoftwareMaintenanceService.createOrUpdateNewSoftwares(
				laptopSoftwareDetailsDTO, getCompanyId(), getEmployeeInfoId(), serialNumber);

		if (laptopSoftwareDetailsDTO.getSoftwareDetailsId() != 0) {
			return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
					.message("Software edited successfully").data(createOrUpdateNewSoftwares).build());
		} else {

			return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
					.message("Software added successfully").data(createOrUpdateNewSoftwares).build());
		}

//		return ResponseEntity.status(HttpStatus.OK)
//				.body(SuccessResponse.builder().error(false).message("Software added successfully")
//						.data(itSoftwareMaintenanceService.createOrUpdateNewSoftwares(laptopSoftwareDetailsDTO,
//								getCompanyId(), getUserId(), serialNumber))
//						.build());

	}

	@PutMapping("/software-maintenance/reniwed/")
	public ResponseEntity<SuccessResponse> updateRenewalStatus(
			@RequestBody PcLaptopSoftwareRenewalDTO laptopSoftwareDetailsDTO) {
		log.info("Create or update  the list of IT software  details against company id:: ", getCompanyId());

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Software reniwed successfully")
						.data(itSoftwareMaintenanceService.updateRenewalStatus(laptopSoftwareDetailsDTO, getCompanyId(),
								getUserId()))
						.build());

	}

}
