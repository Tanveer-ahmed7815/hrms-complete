package com.te.flinko.controller.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.it.ITDashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/it/dashboard")
@RequiredArgsConstructor
@RestController
public class ITDashboardController extends BaseConfigController {

	@Autowired
	private ITDashboardService dashboardService;

	@PostMapping("/ticket")
	public ResponseEntity<SuccessResponse> getTicketDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getTicketDetails(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/ticket-details")
	public ResponseEntity<SuccessResponse> getTicketDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getTicketDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType(),
								dashboardRequestDTO.getFilterValue()))
						.build());
	}

	@PostMapping("/pc-laptop")
	public ResponseEntity<SuccessResponse> getPCLaptopDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getPCLaptopDetails(dashboardRequestDTO, getCompanyId())).build());

	}

	@PostMapping("/pc-laptop-details")
	public ResponseEntity<SuccessResponse> typeBasedPCLaptopDetails(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("PCLaptop details fetched")
						.data(dashboardService.typeBasedPCLaptopDetails(getCompanyId(), dashboardRequestDTO.getType()))
						.build());

	}

	@PostMapping("/other")
	public ResponseEntity<SuccessResponse> getHardwareItemDetails(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getHardwareItemDetails(dashboardRequestDTO, getCompanyId())).build());

	}

	@PostMapping("/other-hardware-details")
	public ResponseEntity<SuccessResponse> typeBasedHardwareItemDetails(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Hardware details fetched").data(
						dashboardService.typeBasedHardwareItemDetails(getCompanyId(), dashboardRequestDTO.getType()))
						.build());

	}

}
