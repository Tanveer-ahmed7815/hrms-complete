package com.te.flinko.dto.sales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
@JsonInclude(value = Include.USE_DEFAULTS)
public class EventBirthdayOtherDetailsDTO implements Serializable{
	
	private List<EventDTO> eventDTOs;
	
	private List<EmployeeBirthdayDTO> employeeBirthdayDTOs;
	
	private List<EmployeePerformanceDTO> employeePerformanceDTOs;
	
	private Double departmentRating;
	
	private Double allottedLeaves;
	private Double approvedLeaves;
	private BigDecimal monthlySelary;
	private BigDecimal annualSelary;
}
