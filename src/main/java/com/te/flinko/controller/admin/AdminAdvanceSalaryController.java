package com.te.flinko.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.admin.AdminAdvanceSalaryService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAdvanceSalaryController extends BaseConfigController {

	private final AdminAdvanceSalaryService employeeAdvanceSalaryService;

	@GetMapping("advance-salary-approvals/{status}")
	public ResponseEntity<SuccessResponse> getAllEmployeeAdvanceSalary(@PathVariable String status) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Fetch All Advance Salary Details")
						.data(employeeAdvanceSalaryService.getAllEmployeeAdvanceSalary(getCompanyId(), status))
						.build());
	}

	@GetMapping("advance-salary-approval/{advanceSalaryId}")
	public ResponseEntity<SuccessResponse> getEmployeeAdvanceSalary(@PathVariable Long advanceSalaryId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Advance Salary Details")
						.data(employeeAdvanceSalaryService.getEmployeeAdvanceSalary(getCompanyId(), advanceSalaryId))
						.build());
	}

	@PutMapping("advance-salary-approval/{advanceSalaryId}/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> addEmployeeAdvanceSalary(@PathVariable Long advanceSalaryId,
			@PathVariable Long employeeInfoId, @RequestBody AdminApprovedRejectDto adminApprovedRejectDto) {
		return ResponseEntity
				.status(HttpStatus.OK).body(
						SuccessResponse.builder().error(Boolean.FALSE)
								.message(employeeAdvanceSalaryService.addEmployeeAdvanceSalary(getCompanyId(),
										advanceSalaryId, employeeInfoId, getEmployeeId(), adminApprovedRejectDto))
								.build());
	}
}
