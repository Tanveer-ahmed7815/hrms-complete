package com.te.flinko.dto.superadmin;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDetailsDTO {

	private Long planId;

	private String activePlanName;

	private BigDecimal amountPerMonth;

	private BigDecimal duration;

	private Integer durationInMonths;

	private String planName;

	private Integer noOfEmp;

	private BigDecimal additionalCostPerEmp;

	private List<String> departments;

}