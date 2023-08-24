package com.te.flinko.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.auth.dto.AuthenticationRequest;
import com.te.flinko.auth.dto.AuthenticationResponse;
import com.te.flinko.dto.employee.EmployeeLoginDto;
import com.te.flinko.dto.employee.EmployeeLoginResponseDto;
import com.te.flinko.entity.employee.EmployeeLoginInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.exception.auth.CustomAccessDeniedException;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.employee.EmployeeLoginInfoRepository;
import com.te.flinko.service.employee.EmployeeLoginService;
import com.te.flinko.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService extends BaseConfigController {

	private final CustomAccessDeniedException accessDenied;
	private final EmployeeLoginInfoRepository employeeLoginRepository;
	private final JwtUtil jwtService;
	private final AuthenticationManager authenticationManager;
	private final EmployeeLoginService employeeLoginService;

	public EmployeeLoginResponseDto authenticate(AuthenticationRequest authenticationRequest,
			HttpServletRequest request, HttpServletResponse response) {
		EmployeeLoginResponseDto employeeLoginResponseDto = new EmployeeLoginResponseDto();
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getEmployeeId(), authenticationRequest.getPassword()));
			employeeLoginResponseDto = employeeLoginService
					.login(EmployeeLoginDto.builder().companyCode(authenticationRequest.getCompanyCode())
							.employeeId(authenticationRequest.getEmployeeId())
							.password(authenticationRequest.getPassword()).build(), request);
			return employeeLoginResponseDto;
		} catch (Exception exception) {
			try {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				log.error(exception.getMessage().equalsIgnoreCase("Bad credentials")
						? "The Company Code, EmployeeId Or Password You Entered Is Incorrect"
						: exception.getMessage());
				accessDenied.handle(request, response,
						new AccessDeniedException(exception.getMessage().equalsIgnoreCase("Bad credentials")
								? "The Company Code, EmployeeId Or Password You Entered Is Incorrect"
								: exception.getMessage()));
			} catch (Exception exception2) {
				log.error(exception2.getMessage());
				throw new DataNotFoundException(exception2.getMessage(), exception2);
			}
			throw new DataNotFoundException(exception.getMessage(), exception);
		}
	}

	public AuthenticationResponse refreshToken(AuthenticationRequest authenticationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		EmployeeLoginInfo employeeLoginInfo = new EmployeeLoginInfo();
		String[] jwtToken = new String[2];
		try {
			jwtService.validateJwtToken(authenticationRequest.getToken());
			employeeLoginInfo = employeeLoginRepository
					.findByEmployeeIdAndEmployeePersonalInfoCompanyInfoCompanyIdAndEmployeePersonalInfoIsActiveTrue(
							jwtService.extractUsername(authenticationRequest.getToken()),
							jwtService.extractCompanyId(authenticationRequest.getToken()).longValue())
					.orElseThrow();
			jwtToken = jwtService.generateAccessToken(employeeLoginInfo, authenticationRequest.getToken(),
					"" + getTerminalId());
			EmployeePersonalInfo info = employeeLoginInfo.getEmployeePersonalInfo();
			return AuthenticationResponse.builder().companyId(info.getCompanyInfo().getCompanyId())
					.employeeId(employeeLoginInfo.getEmployeeId()).employeeInfoId(info.getEmployeeInfoId())
					.accessToken(jwtToken[0]).refreshToken(jwtToken[1]).build();
		} catch (Exception exception) {
			try {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				log.error(exception.getMessage());
				accessDenied.handle(request, response, new AccessDeniedException(exception.getMessage()));
			} catch (Exception exception2) {
				log.error(exception2.getMessage());
				throw new DataNotFoundException(exception2.getMessage(), exception2);
			}
			throw new DataNotFoundException(exception.getMessage(), exception);
		}
	}
}
