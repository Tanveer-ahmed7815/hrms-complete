package com.te.flinko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.WebContactDto;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.WebContactService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/web-contact")
public class WebContactController extends BaseConfigController{
	@Autowired
	private WebContactService webContactService;

	@PostMapping
	public ResponseEntity<SuccessResponse> webContact(@RequestBody WebContactDto webContactDto ){
		return ResponseEntity.ok(new SuccessResponse(false, "response added successfully", webContactService.webContact(webContactDto)));
	}
}
