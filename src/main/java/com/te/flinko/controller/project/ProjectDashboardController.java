package com.te.flinko.controller.project;

import javax.websocket.server.PathParam;

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
import com.te.flinko.service.project.ProjectDashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/project/dashboard")
@RequiredArgsConstructor
@RestController
public class ProjectDashboardController extends BaseConfigController {

	@Autowired
	private ProjectDashboardService dashboardService;

	@PostMapping("/all")
	public ResponseEntity<SuccessResponse> getProjectDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getProjectDetails(dashboardRequestDTO, getCompanyId())).build());

	}
	
	@GetMapping("/drop-down")
	public ResponseEntity<SuccessResponse> getProjectDetails() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Projects fetched")
						.data(dashboardService.getAllProjectNames(getCompanyId())).build());

	}
	
	@GetMapping("/by-id/{projectId}")
	public ResponseEntity<SuccessResponse> getProjectDetails(@PathVariable Long projectId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Projects fetched")
						.data(dashboardService.getProjectDetailsById(projectId,getCompanyId())).build());

	}
	
	@PostMapping("/project-details")
	public ResponseEntity<SuccessResponse> getProjectDetailsByStatus(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getProjectDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType())).build());

	}

}
