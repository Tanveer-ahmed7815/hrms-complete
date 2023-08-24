package com.te.flinko.service.account;

import java.util.ArrayList;

import com.te.flinko.dto.account.AdvanceSalaryByIdDTO;
import com.te.flinko.dto.account.AdvanceSalaryDTO;

public interface AccountAdvanceSalaryService {

	ArrayList<AdvanceSalaryDTO> advanceSalary(Long companyId);

	AdvanceSalaryByIdDTO advanceSalaryById(Long advanceSalaryId, Long companyId);

}
