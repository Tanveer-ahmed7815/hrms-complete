package com.te.flinko.service.account;

import static com.te.flinko.common.hr.HrConstants.COMPANY_INFORMATION_NOT_PRESENT;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.AdvanceSalaryByIdDTO;
import com.te.flinko.dto.account.AdvanceSalaryDTO;
import com.te.flinko.entity.employee.EmployeeAdvanceSalary;
import com.te.flinko.exception.CompanyIdNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeAdvanceSalaryRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountAdvanceSalaryServiceImpl implements AccountAdvanceSalaryService {
	@Autowired
	private CompanyInfoRepository companyInfoRepo;
	@Autowired
	private EmployeeAdvanceSalaryRepository advanceSalaryRepo;

	@Override
	public AdvanceSalaryByIdDTO advanceSalaryById(Long advanceSalaryId, Long companyId) {
		EmployeeAdvanceSalary advanceSalary = advanceSalaryRepo
				.findByAdvanceSalaryIdAndEmployeePersonalInfoCompanyInfoCompanyId(advanceSalaryId, companyId)
				.orElseThrow(() -> new CompanyIdNotFoundException(EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND));
		log.info("advance salary details fetched successfully");
		AdvanceSalaryByIdDTO advanceSalaryByIdDTO = new AdvanceSalaryByIdDTO();

		BeanUtils.copyProperties(advanceSalary, advanceSalaryByIdDTO);

		advanceSalaryByIdDTO.setFullName(advanceSalary.getEmployeePersonalInfo().getFirstName() + " "
				+ advanceSalary.getEmployeePersonalInfo().getLastName());
		advanceSalaryByIdDTO
				.setEmployeeId(advanceSalary.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId());
		advanceSalaryByIdDTO.setCompanyId(advanceSalary.getEmployeePersonalInfo().getCompanyInfo().getCompanyId());
		advanceSalaryByIdDTO.setRequestedOn(advanceSalary.getCreatedDate());
		String status = "";
		if (!advanceSalary.getStatus().equalsIgnoreCase("Approved")) {
			status = advanceSalary.getStatus();
		} else if (Boolean.TRUE.equals(advanceSalary.getIsPaid())) {
			status = "Paid";
		} else if (!Boolean.TRUE.equals(advanceSalary.getIsPaid())) {
			status = " Not paid";
		}
		advanceSalaryByIdDTO.setStatus(status);
		log.info(EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED);
		return advanceSalaryByIdDTO;
	}

	@Override
	public ArrayList<AdvanceSalaryDTO> advanceSalary(Long companyId) {
		ArrayList<AdvanceSalaryDTO> advanceSalaryList = new ArrayList<>();
		companyInfoRepo.findById(companyId)
				.orElseThrow(() -> new CompanyIdNotFoundException(COMPANY_INFORMATION_NOT_PRESENT));
		log.info("company details verified");
		List<EmployeeAdvanceSalary> advanceSalary = advanceSalaryRepo
				.findByEmployeePersonalInfoCompanyInfoCompanyId(companyId);
		for (EmployeeAdvanceSalary employeeAdvanceSalary : advanceSalary) {
			String status = "";
			if (!employeeAdvanceSalary.getStatus().equalsIgnoreCase("Approved")) {
				status = employeeAdvanceSalary.getStatus();
			} else if (Boolean.TRUE.equals(employeeAdvanceSalary.getIsPaid())) {
				status = "Paid";
			} else if (!Boolean.TRUE.equals(employeeAdvanceSalary.getIsPaid())) {
				status = " Not paid";
			}
			advanceSalaryList.add(new AdvanceSalaryDTO(
					employeeAdvanceSalary.getEmployeePersonalInfo().getFirstName() + " "
							+ employeeAdvanceSalary.getEmployeePersonalInfo().getLastName(),
					employeeAdvanceSalary.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId(),
					employeeAdvanceSalary.getAmount(), employeeAdvanceSalary.getEmi(), status,
					employeeAdvanceSalary.getEmployeePersonalInfo().getCompanyInfo().getCompanyId(),
					employeeAdvanceSalary.getAdvanceSalaryId(), employeeAdvanceSalary.getCreatedDate()));

		}

		log.info(EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED);
		return advanceSalaryList;
	}

}
