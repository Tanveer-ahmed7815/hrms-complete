package com.te.flinko.controller.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.dto.employee.AppraisalMeetingDTO;
import com.te.flinko.dto.employee.EmployeePerformanceDetailsDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.employee.EmployeeAppraisalDetailsService;

@RestController
@RequestMapping("/api/v1/appraisal-details")
@CrossOrigin(origins = "https://hrms.flinko.app")
public class EmployeeAppraisalDetailsController {

	@Autowired
	private EmployeeAppraisalDetailsService employeeApprisalDetailsService;

	@GetMapping("/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> getAdvanceSalary(@PathVariable("employeeInfoId") Long employeeInfoId) {
		AppraisalMeetingDTO employeeApprisalDetails = employeeApprisalDetailsService
				.getEmployeeApprisalDetails(employeeInfoId);
		return new ResponseEntity<>(SuccessResponse.builder().data(employeeApprisalDetails).error(false)
				.message("Data Fetched Successfully").build(), HttpStatus.OK);
	}

}
