package com.te.flinko.dto.sales;

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
@JsonInclude(value = Include.NON_NULL)
public class EmployeePerformanceDTO implements Serializable{
	private Long projectId;
	private Long companyId;
	private String departmentName;
	private String employeeName;
	private String employeeId;
	private Double employeeRating;

}
