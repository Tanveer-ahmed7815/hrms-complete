package com.te.flinko.service.account;

import static com.te.flinko.common.hr.HrConstants.COMPANY_INFORMATION_NOT_PRESENT;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.AccountPaySlipInputDTO;
import com.te.flinko.dto.account.AccountPaySlipListDTO;
import com.te.flinko.entity.Department;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.exception.admin.CompanyNotFound;
import com.te.flinko.repository.DepartmentRepository;
import com.te.flinko.repository.account.CompanyVendorInfoRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountPaySlipServiceImpl implements AccountPaySlipService {
	@Autowired
	private CompanyInfoRepository companyInfoRepo;
	@Autowired
	private EmployeeSalaryDetailsRepository salaryDetailsRepo;
	@Autowired
	private DepartmentRepository departmentRepo;
	@Autowired
	private CompanyVendorInfoRepository companyVendorInfoRepo;

	@Override
	public List<AccountPaySlipListDTO> paySlip(AccountPaySlipInputDTO accountPaySlipInputDTO) {
		List<EmployeeSalaryDetails> salarydetails;
		List<AccountPaySlipListDTO> addData = null;
		CompanyInfo companyInfo = companyInfoRepo.findById(accountPaySlipInputDTO.getCompanyId())
				.orElseThrow(() -> new CompanyNotFound(COMPANY_INFORMATION_NOT_PRESENT));
		log.info("company details verified");
		List<Long> departmentList = accountPaySlipInputDTO.getDepartment().stream().map(Long::parseLong)
				.collect(Collectors.toList());
		if (accountPaySlipInputDTO.getDepartment() == null || accountPaySlipInputDTO.getDepartment().isEmpty()) {
			salarydetails = salaryDetailsRepo.findByCompanyInfoCompanyIdAndMonthIn(companyInfo.getCompanyId(),
					accountPaySlipInputDTO.getMonth());

		} else {

			List<Department> findAllById = departmentRepo.findAllById(departmentList);

			List<String> departmentNameList = findAllById.stream().map(Department::getDepartmentName)
					.collect(Collectors.toList());
			log.info("department" + departmentNameList);
			salarydetails = salaryDetailsRepo
					.findByCompanyInfoCompanyIdAndMonthInAndEmployeePersonalInfoEmployeeOfficialInfoDepartmentIn(
							companyInfo.getCompanyId(), accountPaySlipInputDTO.getMonth(), departmentNameList);

		}
		addData = adddata(salarydetails);
log.info(EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY);
		return addData;
	}
// These method use to add data 
	private List<AccountPaySlipListDTO> adddata(List<EmployeeSalaryDetails> salarydetails) {

		ArrayList<AccountPaySlipListDTO> paySalList = new ArrayList<>();
		for (EmployeeSalaryDetails employeeSalaryDetails : salarydetails) {
			AccountPaySlipListDTO accountPaySlipListDTO = new AccountPaySlipListDTO();
			BeanUtils.copyProperties(employeeSalaryDetails, accountPaySlipListDTO);

			if (Boolean.TRUE.equals(employeeSalaryDetails.getIsPaid())) {
				accountPaySlipListDTO.setPaid("Yes");
			} else {

				accountPaySlipListDTO.setPaid("No");
			}
			if (Boolean.TRUE.equals(employeeSalaryDetails.getIsPayslipGenerated())) {
				accountPaySlipListDTO.setStatus("Generated");
			} else {
				accountPaySlipListDTO.setStatus("Not Generated");
			}
			EmployeeOfficialInfo employeeOfficialInfo = employeeSalaryDetails.getEmployeePersonalInfo()
					.getEmployeeOfficialInfo();
			accountPaySlipListDTO
					.setEmployeeId(employeeOfficialInfo == null ? null : employeeOfficialInfo.getEmployeeId());
			accountPaySlipListDTO.setFullname(employeeSalaryDetails.getEmployeePersonalInfo().getFirstName() + " "
					+ employeeSalaryDetails.getEmployeePersonalInfo().getLastName());
			paySalList.add(accountPaySlipListDTO);
		}
		return paySalList.stream().filter(x -> x.getEmployeeId() != null).collect(Collectors.toList());
	}

}
