package com.te.flinko.controller.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountDashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/account/dashboard")
@RequiredArgsConstructor
@RestController
public class AccountDashboardController extends BaseConfigController {

	@Autowired
	private AccountDashboardService dashboardService;

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

	@PostMapping("/sales-purchase")
	public ResponseEntity<SuccessResponse> getSalesAndPurchaseDetails(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getSalesAndPurchaseDetails(dashboardRequestDTO, getCompanyId()))
						.build());
	}

	@PostMapping("/vendor")
	public ResponseEntity<SuccessResponse> getVendorDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getVendorDetails(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/vendor-details")
	public ResponseEntity<SuccessResponse> getVendorDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getVendorDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType()))
						.build());
	}

	@PostMapping("/salary")
	public ResponseEntity<SuccessResponse> getSalaryDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getSalaryDetails(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/salary-details")
	public ResponseEntity<SuccessResponse> getSalaryDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getSalaryDetailsByStatus(getCompanyId(), dashboardRequestDTO)).build());
	}

	@GetMapping("/purchase-details")
	public ResponseEntity<SuccessResponse> getAllPurchaseOrder() {
		return ResponseEntity.ok(SuccessResponse.builder().error(Boolean.FALSE).message("Purchase Details List")
				.data(dashboardService.getAllPurchaseOrder(getCompanyId())).build());
	}

}
