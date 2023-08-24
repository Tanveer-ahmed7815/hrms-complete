package com.te.flinko.service.employee;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.te.flinko.dto.admin.CompanyDesignationNamesDto;
import com.te.flinko.dto.admin.CompanyInfoNamesDto;
import com.te.flinko.dto.admin.PlanDTO;
import com.te.flinko.dto.employee.EmployeeIdDto;
import com.te.flinko.dto.employee.NewConfirmPasswordDto;
import com.te.flinko.dto.employee.Registration;
import com.te.flinko.dto.employee.VerifyOTPDto;
import com.te.flinko.dto.superadmin.PlanDetailsDTO;

public interface EmployeeRegistrationService {
	
	List<CompanyInfoNamesDto> getAllCompany();
	
	List<CompanyDesignationNamesDto> getAllDesignation(Long companyId);

	String varifyEmployee(Registration employeeRegistrationDto,Long companyId,String terminalId);
	 
	String validateOTP(VerifyOTPDto verifyOTPDto);
	
	PlanDTO registration(NewConfirmPasswordDto newConfirmPasswordDto, String terminalId);
	
	String resendOTP(EmployeeIdDto employeeIdDto);
	
	List<PlanDetailsDTO> getAllPlanDetails(HttpServletRequest request,String terminalId);
}
