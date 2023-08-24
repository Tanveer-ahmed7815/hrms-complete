package com.te.flinko.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.auth.dto.AuthenticationRequest;
import com.te.flinko.auth.service.AuthenticationService;
import com.te.flinko.dto.employee.EmployeeLoginResponseDto;
import com.te.flinko.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/authenticate")
	public ResponseEntity<SuccessResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest,
			HttpServletRequest request, HttpServletResponse response) {
		EmployeeLoginResponseDto authenticate = service.authenticate(authenticationRequest, request, response);
		return ResponseEntity.ok(SuccessResponse.builder().error(Boolean.FALSE).message(authenticate.getMsg())
				.data(authenticate).build());
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<SuccessResponse> generateRefreshToken(
			@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		return ResponseEntity.ok(
				SuccessResponse.builder().error(Boolean.FALSE).message("Generate Access Token Through Refresh Token")
						.data(service.refreshToken(authenticationRequest, request, response)).build());
	}
}
