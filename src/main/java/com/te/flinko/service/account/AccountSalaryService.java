package com.te.flinko.service.account;

import java.util.ArrayList;
import java.util.List;

import com.te.flinko.dto.account.AccountSalaryDTO;
import com.te.flinko.dto.account.AccountSalaryInputDTO;
import com.te.flinko.dto.hr.EmployeeSalaryAllDetailsDTO;

public interface AccountSalaryService {

	List<AccountSalaryDTO> salaryDetailsList(AccountSalaryInputDTO accountSalaryInputDTO);

	EmployeeSalaryAllDetailsDTO salaryDetailsById(Long salaryId, Long companyId);

}
