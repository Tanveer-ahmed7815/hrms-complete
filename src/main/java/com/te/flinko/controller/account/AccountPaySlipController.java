package com.te.flinko.controller.account;

import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.hr.HrConstants.SALARY_RECORDS_NOT_FOUND;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.AccountPaySlipInputDTO;
import com.te.flinko.dto.account.AccountPaySlipListDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountPaySlipService;

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
public class AccountPaySlipController extends BaseConfigController {
	@Autowired
	AccountPaySlipService service;

	@PostMapping("/pay-slip")
	public ResponseEntity<SuccessResponse> paySlip(@RequestBody AccountPaySlipInputDTO accountPaySlipInputDTO) {
		List<AccountPaySlipListDTO> paySlip = service.paySlip(accountPaySlipInputDTO);
		if (paySlip == null || paySlip.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, SALARY_RECORDS_NOT_FOUND, paySlip),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new SuccessResponse(false, EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY, paySlip),
				HttpStatus.OK);
	}

}
