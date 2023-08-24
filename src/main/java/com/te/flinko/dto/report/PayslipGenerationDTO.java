package com.te.flinko.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipGenerationDTO implements Serializable{
	
	private String label;
	private String employeeId;
	private String companyName;
	private String companyUrl;
	private String employeeName ;
	private LocalDate dob;
	private LocalDate doj;
	private Long accountNo;
	private String panNo;
	private String pfNo;
	private String bankName;
	private String designation;
	private Long lop;
	private Long ndp;
	private String uan;
	private String earningParticulars;
	private double monthRate;
	private BigDecimal earAmount;
	private String deductionParticulars;
	private BigDecimal ducAmount;
	private BigDecimal totalEar;
	private BigDecimal totalDuc;
	private String inWord;
	private BigDecimal netPay;
	private String addressDetails;
	private Long pincode;
	private String country;
	private String city;
	private String state;
}
