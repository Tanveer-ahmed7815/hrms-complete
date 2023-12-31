package com.te.flinko.dto.employee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class EmployeeLoginResponseDto implements Serializable {
	private String employeeId;
	private Long companyId;
	private Long employeeInfoId;
	private String name;
	private String designation;
	private String department;
	private String logo;
	private Object roles;
	private String dateFormat;
	private String msg;
	private String accessToken;
	private String refreshToken;
}
