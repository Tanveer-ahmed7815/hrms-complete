package com.te.flinko.service.account;

import java.util.ArrayList;
import java.util.List;

import com.te.flinko.dto.account.AccountSalaryDTO;
import com.te.flinko.dto.account.AccountSalaryInputDTO;
import com.te.flinko.dto.account.MarkAsPaidInputDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryListDTO;

public interface AccountSalaryApprovalService {

	List<MarkAsPaidSalaryListDTO> finalizeSalary(MarkAsPaidInputDTO finalizesalaryInputDTO, Long companyId);

	List<AccountSalaryDTO> salaryApproval(AccountSalaryInputDTO accountSalaryInputDTO);

}
