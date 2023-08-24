package com.te.flinko.dto.account;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GeneratePayslipInputDTO {
	private ArrayList<Long> employeeSalaryIdList;

}
