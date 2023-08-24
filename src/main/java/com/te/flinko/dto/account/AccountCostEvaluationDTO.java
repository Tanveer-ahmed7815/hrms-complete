package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
public class AccountCostEvaluationDTO {
	private Long costEvaluationId;
	private BigDecimal asset;
	private BigDecimal fulltime;
	private BigDecimal parttime;
	private BigDecimal hardware;
	private BigDecimal software;
	private BigDecimal salary;
	private BigDecimal overheadExpense;
	private BigDecimal grandTotal;
	private Map<String, BigDecimal> hardwareSoftwareCost;
	private Map<String, BigDecimal> manPower;
//	private Map<String, BigDecimal> otherCategory;
	private String category;
	private BigDecimal amount;
	private String duration;
	private BigDecimal additional;
	private BigDecimal deduction;
	private String reason;

}
