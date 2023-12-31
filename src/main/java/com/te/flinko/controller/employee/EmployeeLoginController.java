package com.te.flinko.controller.employee;

import static com.te.flinko.common.employee.EmployeeLoginConstants.SUCCESSFULLY_LOGGED_IN;
import static org.springframework.http.HttpStatus.OK;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.employee.EmployeeIdDto;
import com.te.flinko.dto.employee.EmployeeLoginDto;
import com.te.flinko.dto.employee.EmployeeLoginResponseDto;
import com.te.flinko.dto.employee.NewConfirmPasswordDto;
import com.te.flinko.dto.employee.ResetPasswordDto;
import com.te.flinko.dto.employee.VerifyOTPDto;
import com.te.flinko.response.employee.EmployeeLoginResponse;
import com.te.flinko.service.employee.EmployeeLoginService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("api/v1/employee")
@RequiredArgsConstructor
public class EmployeeLoginController extends BaseConfigController {

	private final EmployeeLoginService employeeLoginService;

	@PostMapping("login")
	public ResponseEntity<EmployeeLoginResponse> login(@Valid @RequestBody EmployeeLoginDto employeeLoginDto,
			HttpServletRequest request) {
		EmployeeLoginResponseDto employeeLoginResponse = employeeLoginService.login(employeeLoginDto, request);
		return ResponseEntity.status(OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE).message(
				employeeLoginResponse.getMsg() == null ? SUCCESSFULLY_LOGGED_IN : employeeLoginResponse.getMsg())
				.data(employeeLoginResponse).build());
	}

	@PostMapping("punchin")
	public ResponseEntity<EmployeeLoginResponse> punchedIn(@RequestBody EmployeeLoginDto employeeLoginDto,
			HttpServletRequest request) {
		return ResponseEntity.status(OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.punchedIn(employeeLoginDto, request)).build());
	}

	@PostMapping("punchout")
	public ResponseEntity<EmployeeLoginResponse> punchedOut(@RequestBody EmployeeLoginDto employeeLoginDto,
			HttpServletRequest request) {
		return ResponseEntity.status(OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.punchedOut(employeeLoginDto, request)).build());
	}

	@PostMapping("forgot-password")
	public ResponseEntity<EmployeeLoginResponse> forgotPassword(@Valid @RequestBody EmployeeIdDto employeeIdDto) {
		return ResponseEntity.status(HttpStatus.OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.forgotPassword(employeeIdDto)).build());
	}

	@PostMapping("resend-otp")
	public ResponseEntity<EmployeeLoginResponse> resendOTP(@Valid @RequestBody EmployeeIdDto employeeIdDto) {
		return ResponseEntity.status(HttpStatus.OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.resendOTP(employeeIdDto)).build());
	}

	@PostMapping("varify-otp")
	public ResponseEntity<EmployeeLoginResponse> validateOTP(@Valid @RequestBody VerifyOTPDto verifyOTPDto) {
		return ResponseEntity.status(HttpStatus.OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.validateOTP(verifyOTPDto)).build());
	}

	@PostMapping("reset-password")
	public ResponseEntity<EmployeeLoginResponse> resetPassword(
			@Valid @RequestBody NewConfirmPasswordDto newConfirmPasswordDto) {
		return ResponseEntity.status(HttpStatus.OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.resetPassword(newConfirmPasswordDto)).build());
	}

	@PostMapping("password")
	public ResponseEntity<EmployeeLoginResponse> updatePassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
		return ResponseEntity.status(OK).body(EmployeeLoginResponse.builder().error(Boolean.FALSE)
				.message(employeeLoginService.updatePassword(resetPasswordDto, getEmployeeInfoId())).build());
	}

}
