package com.te.flinko.dto.sales;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.sales.CompanyClientInfo;

import lombok.Data;

@Data

public class ProjectDetailsDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long projectId;
	private String projectName;
	private String projectDescription;
	
	@JsonFormat(pattern = "MM-dd-yyyy"  , timezone = "Asia/kolkata")
	private LocalDate startDate;
//	private List<EmployeePersonalInfo> employeePersonalInfoList;
	private CompanyClientInfo companyClientInfo;
	private CompanyInfo companyInfo;
	private EmployeePersonalInfo projectManager;
	private EmployeePersonalInfo reportingManager;
//	private ProjectEstimationDetails projectEstimationDetails;
}
