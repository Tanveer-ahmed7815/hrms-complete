package com.te.flinko.service.account;

import java.time.Month;
import java.util.ArrayList;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_FINALIZE_SUCCESSFULLY;
import static com.te.flinko.common.hr.HrConstants.COMPANY_INFORMATION_NOT_PRESENT;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.AccountSalaryDTO;
import com.te.flinko.dto.account.AccountSalaryInputDTO;
import com.te.flinko.dto.account.MarkAsPaidInputDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryListDTO;
import com.te.flinko.entity.Department;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.exception.CompanyIdNotFoundException;
import com.te.flinko.exception.account.CustomExceptionForAccount;
import com.te.flinko.repository.DepartmentRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountSalaryApprovalServiceImpl implements AccountSalaryApprovalService {
	@Autowired
	private EmployeeSalaryDetailsRepository employeeSalaryDetailsRepo;
	@Autowired
	private CompanyInfoRepository companyInfoRepo;
	@Autowired
	private EmployeeSalaryDetailsRepository salaryDetailsRepo;
	@Autowired
	private DepartmentRepository departmentRepo;

	@Override
	public List<MarkAsPaidSalaryListDTO> finalizeSalary(MarkAsPaidInputDTO finalizesalaryInputDTO, Long companyId) {
		
		ArrayList<Long> salaryIdList = finalizesalaryInputDTO.getAdvanceSalaryId();
		List<EmployeeSalaryDetails> salaryDetails = employeeSalaryDetailsRepo
				.findByEmployeeSalaryIdInAndCompanyInfoCompanyId(salaryIdList, companyId);
		if (salaryDetails == null || salaryDetails.isEmpty()) {
			throw new CustomExceptionForAccount(EMPLOYEE_SALARY_DETAILS_NOT_FOUND);
		}
		List<MarkAsPaidSalaryListDTO> finalizeList = new ArrayList<>();
		for (EmployeeSalaryDetails employeeSalaryDetails : salaryDetails) {
			employeeSalaryDetails.setIsFinalized(true);
			EmployeeSalaryDetails employeeSalary = employeeSalaryDetailsRepo.save(employeeSalaryDetails);
			MarkAsPaidSalaryListDTO markAsPaidSalaryListDTO = new MarkAsPaidSalaryListDTO();
			BeanUtils.copyProperties(employeeSalary, markAsPaidSalaryListDTO);
			finalizeList.add(markAsPaidSalaryListDTO);
		}
		log.info(EMPLOYEE_SALARY_FINALIZE_SUCCESSFULLY);
		return finalizeList;
	}

	@Override
	public List<AccountSalaryDTO> salaryApproval(AccountSalaryInputDTO accountSalaryInputDTO) {

		List<EmployeeSalaryDetails> salaryDetails;
		if (accountSalaryInputDTO.getDepartment() == null || accountSalaryInputDTO.getDepartment().isEmpty()) {
			salaryDetails = salaryDetailsRepo.findByCompanyInfoCompanyIdAndMonthInAndIsFinalizedFalse(
					accountSalaryInputDTO.getCompanyId(), accountSalaryInputDTO.getMonth());
		} else {
			List<Long> departmentIdList = accountSalaryInputDTO.getDepartment().stream().map(Long::parseLong)
					.collect(Collectors.toList());
			List<Department> findAllById = departmentRepo.findAllById(departmentIdList);
			
			List<String> departmentNameList = findAllById.stream().map(Department::getDepartmentName)
					.collect(Collectors.toList());
			salaryDetails = salaryDetailsRepo
					.findByCompanyInfoCompanyIdAndMonthInAndIsFinalizedFalseAndEmployeePersonalInfoEmployeeOfficialInfoDepartmentIn(
							accountSalaryInputDTO.getCompanyId(), accountSalaryInputDTO.getMonth(), departmentNameList);
		}
		// method for adding data
		List<AccountSalaryDTO> addData = addData(salaryDetails);

		log.info("employees Salary details fetch successfully");
		return addData;
	}

	private List<AccountSalaryDTO> addData(List<EmployeeSalaryDetails> salaryDetails) {
		ArrayList<AccountSalaryDTO> dropDownlist = new ArrayList<>();
		Double totalDeduction = 0.0d;
		Double totalAdditional = 0.0d;
		for (EmployeeSalaryDetails employeeSalaryDetails : salaryDetails) {
			Map<String, String> deduction = employeeSalaryDetails.getDeduction();
			Map<String, String> additional = employeeSalaryDetails.getAdditional();
			EmployeePersonalInfo employeePersonalInfo = employeeSalaryDetails.getEmployeePersonalInfo();
			EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
			if ((employeePersonalInfo != null) && (employeeOfficialInfo != null)) {
				String dummyStatus;
				if (Boolean.TRUE.equals(employeeSalaryDetails.getIsPayslipGenerated())) {
					dummyStatus = "Payslip Generated";
				} else if (Boolean.TRUE.equals(employeeSalaryDetails.getIsPaid())) {
					dummyStatus = "Paid";
				} else if (Boolean.TRUE.equals(employeeSalaryDetails.getIsFinalized())) {
					dummyStatus = "Finalised";
				} else {
					dummyStatus = "Pending";
				}

				String lop = null;
				if (additional != null) {
					for (Entry<String, String> entry : additional.entrySet()) {
						Double parseDouble = Double.parseDouble(entry.getValue());
						totalAdditional += parseDouble;
					}
				}
				if (deduction != null) {
					for (Map.Entry<String, String> entry : deduction.entrySet()) {
						Double parseDouble = Double.parseDouble(entry.getValue());
						totalDeduction += parseDouble;

					}
				}
				if ((deduction != null)) {
					for (Map.Entry<String, String> entry : deduction.entrySet()) {
						if (entry.getKey().equalsIgnoreCase("lop")) {
							lop = entry.getValue();
							break;
						}
					}
				}

				lop = (lop == null) ? Integer.toString(0) : lop;
				dropDownlist.add(new AccountSalaryDTO(employeeOfficialInfo.getEmployeeId(),
						employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName(),
						employeeSalaryDetails.getTotalSalary(), employeeSalaryDetails.getAdditional(),
						employeeSalaryDetails.getDeduction(), lop, employeeSalaryDetails.getNetPay(), dummyStatus,
						employeeSalaryDetails.getEmployeeSalaryId(), totalAdditional == null ? null : totalAdditional,
						totalDeduction == null ? null : totalDeduction,
						Month.of(employeeSalaryDetails.getMonth()).name()));
				totalAdditional = 0.0d;
				totalDeduction = 0.0d;
			}

		}
		return dropDownlist;
	}
}
