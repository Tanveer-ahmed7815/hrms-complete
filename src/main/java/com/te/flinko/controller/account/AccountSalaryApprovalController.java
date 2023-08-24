package com.te.flinko.controller.account;


import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_FINALIZE_SUCCESSFULLY;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.AccountSalaryDTO;
import com.te.flinko.dto.account.AccountSalaryInputDTO;
import com.te.flinko.dto.account.MarkAsPaidInputDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryListDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountSalaryApprovalService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping(path = "/api/v1/account")
@RestController

/**
 * 
 * @author Ravindra
 *
 */
public class AccountSalaryApprovalController extends BaseConfigController{
	@Autowired 
	AccountSalaryApprovalService service;
	
	
	@PostMapping("/finalize-salary")
	public ResponseEntity<SuccessResponse> finalizeSalary(@RequestBody MarkAsPaidInputDTO finalizesalaryInputDTO){
		log.info("finalizeSalary method execution started");
		List<MarkAsPaidSalaryListDTO> finalizeSalary = service.finalizeSalary(finalizesalaryInputDTO,getCompanyId());
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEE_SALARY_FINALIZE_SUCCESSFULLY, finalizeSalary),
					HttpStatus.OK);
	}
   @PostMapping("/salary-approval")
   public ResponseEntity<SuccessResponse> salaryApproval(@RequestBody AccountSalaryInputDTO accountSalaryInputDTO){
		log.info("salaryApproval method execution started");
	   List<AccountSalaryDTO> salaryApproval = service.salaryApproval(accountSalaryInputDTO);
		if (salaryApproval == null || salaryApproval.isEmpty()) {
			return new ResponseEntity<>(
					new SuccessResponse(false, EMPLOYEE_SALARY_DETAILS_NOT_FOUND, salaryApproval),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY, salaryApproval),
				HttpStatus.OK);
   }
}
