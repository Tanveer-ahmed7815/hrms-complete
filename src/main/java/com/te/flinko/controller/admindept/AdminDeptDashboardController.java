package com.te.flinko.controller.admindept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.admindept.AdminDeptDashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/admin-dept/dashboard")
@RequiredArgsConstructor
@RestController
public class AdminDeptDashboardController extends BaseConfigController {

	@Autowired
	private AdminDeptDashboardService dashboardService;

	@PostMapping("/ticket")
	public ResponseEntity<SuccessResponse> getTicketDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getTicketDetails(dashboardRequestDTO, getCompanyId())).build());

	}

	@PostMapping("/stock-group-item")
	public ResponseEntity<SuccessResponse> getStockGroupDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getStockGroupDetails(dashboardRequestDTO, getCompanyId())).build());

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

	@PostMapping("/stock-group-item-details")
	public ResponseEntity<SuccessResponse> getStockGroupDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched").data(
						dashboardService.getStockItemDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType()))
						.build());

	}

}
