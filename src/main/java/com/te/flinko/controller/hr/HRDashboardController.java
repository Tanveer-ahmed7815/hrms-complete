package com.te.flinko.controller.hr;

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
import com.te.flinko.service.hr.HRDashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/hr/dashboard")
@RequiredArgsConstructor
@RestController
public class HRDashboardController extends BaseConfigController {

	@Autowired
	private HRDashboardService dashboardService;

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

	@PostMapping("/candidate")
	public ResponseEntity<SuccessResponse> getCandidateDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getCandidateDetails(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/candidate-details")
	public ResponseEntity<SuccessResponse> getCandidateDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched").data(
						dashboardService.getCandidateDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType()))
						.build());
	}

	@PostMapping("/event")
	public ResponseEntity<SuccessResponse> getEventDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getEventDetails(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/event-details")
	public ResponseEntity<SuccessResponse> getEventDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getEventDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType()))
						.build());
	}

	@PostMapping("/employee")
	public ResponseEntity<SuccessResponse> getEmployeeDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getEmployeeDetails(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/employee-details")
	public ResponseEntity<SuccessResponse> getEmployeeDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched").data(
						dashboardService.getEmployeeDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType()))
						.build());
	}

}
