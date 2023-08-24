package com.te.flinko.controller.helpandsupport.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.helpandsupport.mongo.CompanyChatDetailsDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.helpandsupport.mongo.CompanyChatDetailsService;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1")
public class CompanyChatDetailsController extends BaseConfigController {

	@Autowired
	private CompanyChatDetailsService companyChatDetailsService;

	@GetMapping("message/{companyId}/{senderEmployeeId}/{senderEmployeeInfoId}/{receiverEmployeeId}/{receiverEmployeeInfoId}")
	public ResponseEntity<SuccessResponse> getAllChatMessage(@PathVariable String companyId,
			@PathVariable String senderEmployeeId, @PathVariable String senderEmployeeInfoId,
			@PathVariable String receiverEmployeeId, @PathVariable String receiverEmployeeInfoId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse
						.builder().error(Boolean.FALSE).message("Fetch All Chat").data(companyChatDetailsService
								.getAllChatMessage(Long.parseLong(companyId), senderEmployeeId, receiverEmployeeId))
						.build());
	}

	@PostMapping("help-support/chat")
	public ResponseEntity<SuccessResponse> sendMessage(@RequestBody CompanyChatDetailsDTO companyChatDetailsDTO) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Send Message Successfully").data(
						companyChatDetailsService.sendMessage(getCompanyId(), getEmployeeId(), companyChatDetailsDTO))
						.build());
	}

}
