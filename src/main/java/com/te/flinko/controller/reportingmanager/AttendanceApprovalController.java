package com.te.flinko.controller.reportingmanager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.reportingmanager.AttendanceApprovalDTO;
import com.te.flinko.dto.reportingmanager.EmployeeAttendanceDetailsDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.reportingmanager.EmployeeAttendanceService;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/attendance-approval")
public class AttendanceApprovalController extends BaseConfigController {

	@Autowired
	private EmployeeAttendanceService employeeAttendanceService;

	@GetMapping
	public ResponseEntity<SuccessResponse> getAllAttendanceDetails() {
		List<EmployeeAttendanceDetailsDTO> allAttendanceDetails = employeeAttendanceService
				.getAllAttendanceDetails(getCompanyId(), getUserId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).data(allAttendanceDetails)
						.message(allAttendanceDetails.isEmpty() ? "No Record Found" : "Fetched Successfully").build());
	}

	@PostMapping
	public ResponseEntity<SuccessResponse> getAttendanceDetailsById(
			@RequestBody AttendanceApprovalDTO attendanceApprovalDTO) {
		EmployeeAttendanceDetailsDTO attendanceDetails = employeeAttendanceService
				.getAttendanceDetailsById(attendanceApprovalDTO);
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.data(attendanceDetails).message("Fetched Successfully").build());
	}
	
	@PutMapping
	public ResponseEntity<SuccessResponse> updateStatus(
			@RequestBody AttendanceApprovalDTO attendanceApprovalDTO) {
		attendanceApprovalDTO = employeeAttendanceService
				.updateStatus(attendanceApprovalDTO);
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.data(attendanceApprovalDTO).message("Updated Successfully").build());
	}
	
	@GetMapping("/punch-in")
	public ResponseEntity<SuccessResponse> getMissedPunchIn() {
		List<EmployeeAttendanceDetailsDTO> allAttendanceDetails = employeeAttendanceService
				.getMissedPunchIn(getCompanyId(), getUserId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).data(allAttendanceDetails)
						.message(allAttendanceDetails.isEmpty() ? "No Record Found" : "Fetched Successfully").build());
	}
	
	@GetMapping("/punch-out")
	public ResponseEntity<SuccessResponse> getMissedPunchOut() {
		List<EmployeeAttendanceDetailsDTO> allAttendanceDetails = employeeAttendanceService
				.getMissedPunchOut(getCompanyId(), getUserId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).data(allAttendanceDetails)
						.message(allAttendanceDetails.isEmpty() ? "No Record Found" : "Fetched Successfully").build());
	}
	
	@PutMapping("/punch-in-out")
	public ResponseEntity<SuccessResponse> updatePunchInOut(
			@RequestBody AttendanceApprovalDTO attendanceApprovalDTO) {
		attendanceApprovalDTO = employeeAttendanceService
				.updatePunchInOut(attendanceApprovalDTO);
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.data(attendanceApprovalDTO).message("Updated Successfully").build());
	}


}
