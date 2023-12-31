package com.te.flinko.dto.employee;

import static com.te.flinko.common.employee.EmployeeRegistrationConstants.DESIGNATION_NAME_NAME_CAN_NOT_BE_NULL_OR_BLANK;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.EMPLOYEE_ID_CAN_NOT_BE_NULL_OR_BLANK;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.FIRST_NAME_CAN_NOT_BE_NULL_OR_BLANK;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.LAST_NAME_CAN_NOT_BE_NULL_OR_BLANK;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.OFFICIAL_EMAIL_ID_CAN_NOT_BE_NULL_OR_BLANK;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.te.flinko.util.MapToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
public class Registration implements Serializable{

	private String  companyName;
	
	private String department;
	
	private Object roles;
	
	private String  companyCode;
	
	@NotBlank(message = EMPLOYEE_ID_CAN_NOT_BE_NULL_OR_BLANK)
	private String employeeId;
	
	@NotBlank(message = FIRST_NAME_CAN_NOT_BE_NULL_OR_BLANK)
	private String firstName;
	
	@NotBlank(message = LAST_NAME_CAN_NOT_BE_NULL_OR_BLANK)
	private String lastName;
	
	@NotBlank(message = OFFICIAL_EMAIL_ID_CAN_NOT_BE_NULL_OR_BLANK)
	private String officialEmailId;
	
//	@NotBlank(message = MOBILE_NUMBER_CAN_NOT_BE_NULL_OR_BLANK)
	private Long mobileNumber;
	
	@NotBlank(message = DESIGNATION_NAME_NAME_CAN_NOT_BE_NULL_OR_BLANK)
	private String designationName;
	
	private String pan;
	
	private String gstin;
	
	private String cin;
	
	private Long noOfEmp;
	
	private String companyEmailId;
	
	private Long companyMobileNumber;
	
	private Long companyId;
	
	private String password;
	private String confirmPassword;
	private Long otp;
	private Boolean isActive;
	@Convert(converter = MapToStringConverter.class)
	private Map<String, String> status;
}
