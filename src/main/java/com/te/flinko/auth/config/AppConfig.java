package com.te.flinko.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.repository.employee.EmployeeLoginInfoRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig extends BaseConfigController {

	private static final String COMPANY_CODE = "Company_Code";
	private final EmployeeLoginInfoRepository employeeLoginInfoRepository;

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> employeeLoginInfoRepository
				.findByEmployeeIdAndEmployeePersonalInfoIsActiveTrueAndEmployeePersonalInfoCompanyInfoIsActiveTrueAndEmployeePersonalInfoCompanyInfoCompanyCode(
						username.split(COMPANY_CODE)[0],
						username.split(COMPANY_CODE).length > 1 && username.split(COMPANY_CODE)[1] != null
								? username.split(COMPANY_CODE)[1]
								: getCompanyCode())
				.orElseThrow(() -> new UsernameNotFoundException(
						"The Company Code, EmployeeId Or Password You Entered Is Incorrect"));
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}

}
