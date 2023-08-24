package com.te.flinko.controller.admin;

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
import com.te.flinko.dto.admin.TermsAndConditionDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.admin.TermsAndConditionService;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/terms/conditions")
public class TermsAndConditionController extends BaseConfigController{
	
	@Autowired
	private TermsAndConditionService termsAndConditionService;
	
	@PostMapping
	public ResponseEntity<SuccessResponse> saveTermsAndCondition(@RequestBody TermsAndConditionDTO termsAndConditionDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Terms and Condition Added Successfully")
						.data(termsAndConditionService.addTermsAndCondition(termsAndConditionDTO, getCompanyId())).build());
	}
	
	@GetMapping
	public ResponseEntity<SuccessResponse> getAllTermsAndCondition() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Terms and Condition Added Successfully")
						.data(termsAndConditionService.getAllTermsAndConditions(getCompanyId())).build());
	}

}
