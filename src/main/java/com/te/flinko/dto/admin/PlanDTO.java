package com.te.flinko.dto.admin;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = Include.NON_DEFAULT)
public class PlanDTO  implements Serializable{
	private Long planId;

	private BigDecimal amountPerMonth;
	
	private BigDecimal maxTotalAmount;
	
	private BigDecimal minTotalAmount;

	private BigDecimal duration;
	
	private Integer durationInMonths;
	
	private String currencyCode;
	
	private String currencySymbol;

	private String planName;
	
	private String msg;
	
	private Long employeeInfoId;

	private Integer noOfEmp;

	private BigDecimal additionalCostPerEmp;
	
	private Long companyId;

	private List<String> departments;
}
