package com.te.flinko.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
@Builder
public class DepartmentProjectDTO {
	
	private String departmentName;
	
	private String designationName;
	
	private String dashboardDepartmentName;
	
	private Long projectId;

}
