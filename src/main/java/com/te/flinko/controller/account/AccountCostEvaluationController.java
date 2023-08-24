package com.te.flinko.controller.account;

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
import com.te.flinko.dto.account.AccountCostEvaluationDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountCostEvaluationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/account")
@RestController
public class AccountCostEvaluationController extends BaseConfigController {

	private final AccountCostEvaluationService accountCostEvaluationService;

	@GetMapping("/cost-evaluations/{year}")
	public ResponseEntity<SuccessResponse> getAllCostEvaluation(@PathVariable int year) {
		log.info("Cost evaluation method execution started");
		return new ResponseEntity<>(new SuccessResponse(false, "Total Cost Evaluation",
				accountCostEvaluationService.getAccountCostEvaluation(year,getCompanyId())), HttpStatus.OK);
	}

	@GetMapping("/cost-evaluation/{cat}")
	public ResponseEntity<SuccessResponse> getCostEvaluation(@PathVariable String cat) {
		log.info("Cost evaluation method execution started");
		return new ResponseEntity<>(new SuccessResponse(false, "Total Cost Evaluation",
				accountCostEvaluationService.getSingleCostEvaluation(cat, getCompanyId())), HttpStatus.OK);
	}

	@PutMapping("/cost-evaluation")
	public ResponseEntity<SuccessResponse> addCostEvaluation(
			@RequestBody AccountCostEvaluationDTO accountCostEvaluationDTO) {
		log.info("Add cost evaluation method execution started");
		return new ResponseEntity<>(new SuccessResponse(false,
				accountCostEvaluationService.addAccountCostEvaluation(accountCostEvaluationDTO, getCompanyId()), null),
				HttpStatus.OK);
	}
}
