package com.te.flinko.entity.report.mongo;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@Document("fa_employee_performance_details")
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePerformance implements Serializable {
	@Id
	private String performanceObjectId;

	@Field("epd_performance_id")
	private Long performanceId;

	@Field("epd_company_id")
	private Long companyId;
	
	@Field("epd_year")
	private Long year;
	
	@Field("epd_employee_info_id")
	private Long employeeInfoId;
	
	@Field("epd_employee_monthly_performance")
	private Map<String,MonthlyPerformance> monthlyPerformance;

	@Field("epd_employee_id")
	private String employeeId;
	
	@Field("epd_employee_name")
	private String employeeName;
	
	@Field("epd_department_name")
	private String departmentName;
	
}
