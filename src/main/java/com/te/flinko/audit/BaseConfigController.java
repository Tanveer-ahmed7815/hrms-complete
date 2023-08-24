package com.te.flinko.audit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.te.flinko.exception.TerminalIdNotFoundException;
import com.te.flinko.exception.UserNotFoundException;
import com.te.flinko.schedule.employee.ScheduledTask;

@JsonIgnoreProperties
public class BaseConfigController {

	private static final String LOGGED_IN_USER_ID_NOT_FOUND = "Logged-In userId not found!!";
	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ScheduledTask scheduledTask;

	public Long getUserId() {
		try {
			String userIdTemp = httpServletRequest != null ? httpServletRequest.getHeader("userId") : "";
			
			if (!userIdTemp.equals("null") && !userIdTemp.equals("")) {
				return Long.parseLong(userIdTemp);
			} else {
				throw new UserNotFoundException(LOGGED_IN_USER_ID_NOT_FOUND+"getUserId");
			}
		} catch (Exception e) {
			Long userId = scheduledTask.getUserId(); 
			if (userId == null || userId == 0)
				userId=0l;
			return userId;
		}
	}

	public Long getCompanyId() {
		 String companyId = httpServletRequest != null ? httpServletRequest.getHeader("companyId") : "";
		if (companyId != null && !companyId.equals("null") && !companyId.equals("")) {
			return Long.parseLong(companyId);
		} else {
			throw new UserNotFoundException(LOGGED_IN_USER_ID_NOT_FOUND+"getCompanyId");
		}
	}

	public String getEmployeeId() {
		final String employeeId = httpServletRequest != null ? httpServletRequest.getHeader("employeeId") : "";
		if (employeeId != null && !employeeId.equals("null") && !employeeId.equals("")) {
			return employeeId;
		} else {
			throw new UserNotFoundException(LOGGED_IN_USER_ID_NOT_FOUND+"getEmployeeId");
		}
	}

	public Long getEmployeeInfoId() {
		final String employeeInfoId = httpServletRequest != null ? httpServletRequest.getHeader("employeeInfoId") : "";
		if (employeeInfoId != null && !employeeInfoId.equals("null") && !employeeInfoId.equals("")) {
			return Long.parseLong(employeeInfoId);
		} else {
			throw new UserNotFoundException("Employee Info Id Not Found");
		}
	}

	public String getTerminalId() {
		final String terminalIdTemp = httpServletRequest != null ? httpServletRequest.getHeader("terminalId") : "";
		if (terminalIdTemp != null && !terminalIdTemp.equals("null") && !terminalIdTemp.equals("")) {
			return terminalIdTemp;
		} else {
			throw new TerminalIdNotFoundException(LOGGED_IN_USER_ID_NOT_FOUND+"getTerminalId");
		}
	}
	
	public String getCompanyCode() {
		final String terminalIdTemp = httpServletRequest != null ? httpServletRequest.getHeader("companyCode") : "";
		if (terminalIdTemp != null && !terminalIdTemp.equals("null") && !terminalIdTemp.equals("")) {
			return terminalIdTemp;
		} else {
			return null;
		}
	}

}
