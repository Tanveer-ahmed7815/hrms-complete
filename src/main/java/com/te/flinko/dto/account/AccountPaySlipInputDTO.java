package com.te.flinko.dto.account;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountPaySlipInputDTO {
	private List<Integer> month;
	private List<String> department;
	private Long companyId;

}
