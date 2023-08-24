package com.te.flinko.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.helpandsupport.mongo.CompanyTicketDto;
import com.te.flinko.dto.it.mongo.ITTicketsDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.admin.AdminDashboardService;
import com.te.flinko.service.it.mongo.ITTicketsService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@RestController
public class AdminDashboardController extends BaseConfigController {

	@Autowired
	private AdminDashboardService dashboardService;

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
						.data(dashboardService.getTicketDetailsByStatus(dashboardRequestDTO, getCompanyId())).build());
	}

	@PostMapping("/ticket-by-id/{objectTicketId}")
	public ResponseEntity<SuccessResponse> getTicketDetailsById(@RequestBody DashboardRequestDTO dashboardRequestDTO,
			@PathVariable String objectTicketId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched").data(
						dashboardService.getTicketDetailsById(dashboardRequestDTO, objectTicketId, getCompanyId()))
						.build());
	}

	@PostMapping("/salary")
	public ResponseEntity<SuccessResponse> getSalaryDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getSalaryDetails(dashboardRequestDTO, getCompanyId())).build());
	}
	
	@PostMapping("/attendance")
	public ResponseEntity<SuccessResponse> getAttendanceDetails(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched")
						.data(dashboardService.getAttendanceDetails(dashboardRequestDTO, getCompanyId())).build());
	}
	
	@PostMapping("/attendance-details")
	public ResponseEntity<SuccessResponse> getEmployeeDetailsByStatus(
			@RequestBody DashboardRequestDTO dashboardRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Dashboard details fetched").data(
						dashboardService.getEmployeeDetailsByStatus(getCompanyId(), dashboardRequestDTO.getType()))
						.build());
	}

//	changes done
//	Post api for question and answer 
	@PostMapping("/tickets/remarkAdd")
	public ResponseEntity<SuccessResponse> putITTicketsHardwareAllocted(@RequestBody CompanyTicketDto updateTicketDTO) {
//		log.info("update ticket history details of question and answer aganist employeeInfo id");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Update Tickets")
						.data(dashboardService.addTicketsHardwareAllocatedDetails(updateTicketDTO, getCompanyId()))
						.build());
	}

//	put Api for writing a question and answer whole object
	@PutMapping("/tickets/remarkUpdate")
	public ResponseEntity<SuccessResponse> postITTicketsHardwareAllocted(
			@RequestBody CompanyTicketDto updateTicketDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message(updateTicketDTO.getCategory().equalsIgnoreCase("Question") ? "Question Raised Successfully"
						: "Reply Updated Successfully")
				.data(dashboardService.updateTicketRemarks(updateTicketDTO, getCompanyId())).build());
	}

}
