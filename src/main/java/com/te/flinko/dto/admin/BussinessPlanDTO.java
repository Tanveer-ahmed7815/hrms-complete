package com.te.flinko.dto.admin;

import static com.te.flinko.common.employee.EmployeeRegistrationConstants.COMPANY_NAME_CAN_NOT_BE_NULL_OR_BLANK;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
public class BussinessPlanDTO implements Serializable {
	
	@NotBlank(message = COMPANY_NAME_CAN_NOT_BE_NULL_OR_BLANK)
	private String companyName;
	
	@NotBlank(message = "PAN is required")
	private String pan;
	
	@NotBlank(message = "GSTN is required")
	private String gstin;
	
	@NotBlank(message = "CIN is required")
	private String cin;
	
	@NotBlank(message = "No. of employee is required")
	private Long noOfEmp;
	
	@NotBlank(message = "Email id is required")
	private String companyEmailId;
	
	@NotBlank(message = "Mobile no. is required")
	private Long companyMobileNumber;
	
	private Long companyId;
}
