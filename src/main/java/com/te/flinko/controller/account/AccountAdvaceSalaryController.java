package com.te.flinko.controller.account;

import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_ADVANCE_SALARY_SUCCESSFULLY_FETCHED;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.AdvanceSalaryByIdDTO;
import com.te.flinko.dto.account.AdvanceSalaryDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountAdvanceSalaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/account")
@RestController
/**
 * 
 * @author Ravindra
 *
 */
public class AccountAdvaceSalaryController extends BaseConfigController{
	@Autowired
	AccountAdvanceSalaryService service;
	
	@GetMapping("/advance-salary")
	public ResponseEntity<SuccessResponse> advanceSalary(){
		log.info("advance salary method execution started");
		ArrayList<AdvanceSalaryDTO> advanceSalary = service.advanceSalary(getCompanyId());
		if(advanceSalary== null || advanceSalary.isEmpty()) {
			return new ResponseEntity<>(
					new SuccessResponse(false, EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND, advanceSalary),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEES_ADVANCE_SALARY_SUCCESSFULLY_FETCHED, advanceSalary),
				HttpStatus.OK);
	}
	
	@GetMapping("/advance-salary-by-id")
	
	public ResponseEntity<SuccessResponse> advanceSalaryById(@RequestParam Long advanceSalaryId) {
		log.info("advance salary by id method execution started");
		AdvanceSalaryByIdDTO advanceSalaryById = service.advanceSalaryById(advanceSalaryId, getCompanyId());
		
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED, advanceSalaryById),
				HttpStatus.OK);
	}
}
