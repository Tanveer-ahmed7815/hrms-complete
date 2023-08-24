package com.te.flinko.controller.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.employee.EmployeeExtraWorkDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.employee.EmployeeExtraWorkService;

@RestController
@RequestMapping("/api/v1/extra-work")
@CrossOrigin(origins = "https://hrms.flinko.app")
public class EmployeeExtraWorkController extends BaseConfigController {

	@Autowired
	private EmployeeExtraWorkService employeeExtraWorkService;

	@PostMapping("")
	public ResponseEntity<SuccessResponse> saveExtraWork(@RequestBody EmployeeExtraWorkDTO employeeExtraWorkDTO) {
		employeeExtraWorkDTO = employeeExtraWorkService.saveExtraWorkDetails(employeeExtraWorkDTO, getUserId());
		if (employeeExtraWorkDTO != null)
			return new ResponseEntity<>(SuccessResponse.builder().data(employeeExtraWorkDTO).error(false)
					.message("Extra Work Request Submitted Successfully").build(), HttpStatus.OK);
		else
			return new ResponseEntity<>(SuccessResponse.builder().data(employeeExtraWorkDTO).error(true)
					.message("Data Not Available").build(), HttpStatus.NOT_FOUND);
	}
	

}
