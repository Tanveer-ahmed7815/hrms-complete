package com.te.flinko.auth.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.te.flinko.auth.filter.JwtAuthenticationFilter;
import com.te.flinko.exception.auth.CustomAccessDeniedException;
import com.te.flinko.exception.auth.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
//		.antMatcher("api/v1/layover/auth/*").antMatcher("api/v1/layover/airline/registration")
//		.antMatcher("api/v1/layover/airport/registration").antMatcher("api/v1/careers/career-opportunity")
//		.antMatcher("api/v1/contact-us/contact-us")
//		.antMatcher("api/v1/layover/extern-Handling-company-details/registration")
//		.antMatcher("api/v1/layover/hotel/api/v1/layover/hotel")
//		.antMatcher("api/v1/layover/transportation/registration").authorizeRequests().anyRequest().permitAll();
		http.cors().configurationSource(request -> {
			final CorsConfiguration cors = new CorsConfiguration();
			cors.setAllowCredentials(true);
			cors.setAllowedOriginPatterns(Arrays.asList("*"));
			cors.setAllowedMethods(Arrays.asList("*"));
			cors.setAllowedHeaders(Arrays.asList("*"));
			cors.setMaxAge(3600L);
			return cors;
		});

		 http.csrf().disable().authorizeHttpRequests().antMatchers(
				"/",
				"/api/v1/auth/**",
				"/socket.io/**",
				"/api/v1/auth/**/**",
				"/api/v1/employee/**",
				"/api/v1/payment/**",
				"/api/v1/plan",
				"/api/v1/companies",
				"/api/v1/designations/**",
				"/api/v1/bussiness-plan/**",
				"/api/v1/bussiness-plan",
				"/api/v1/user-plan",
				"/api/v1/plan-details",
				"/api/v1/hr/employee/in-progress/**",
				"/api/v1/candidate/interview-Feedback-Info/**",
				"/api/v1/hr/employee/generaldetials",
				"/api/v1/hr/employee/personalinformation",
				"/api/v1/hr/employee/dependentInformation/**",
				"/api/v1/hr/employee/employementinformation/**",
				"/api/v1/hr/employee/educationinformation/**",
				"/api/v1/hr/employee/bankinformation/**",
				"/api/v1/hr/employee/employeeDocument/**",
				"/api/v1/file-upload",
				"/api/v1/vendor",
				"/api/v1/vendor/dynamic-foctors",
				"/api/v1/web-contact/**"
				).permitAll()
		.antMatchers("/api/v1/employee/password").authenticated()
		.anyRequest()
				.authenticated()
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.accessDeniedHandler(new CustomAccessDeniedException()).and()
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
