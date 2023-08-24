package com.te.flinko.repository.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.employee.EmployeeSalaryDetails;
@Repository
public interface EmployeeSalaryDetailsRepository extends JpaRepository<EmployeeSalaryDetails, Long> {

	List<EmployeeSalaryDetails> findByCompanyInfoCompanyIdAndMonthInAndEmployeePersonalInfoEmployeeOfficialInfoDepartmentIn(Long companyId,
			List<Integer> month, List<String> department);

	List<EmployeeSalaryDetails>  findByCompanyInfoCompanyIdAndMonthIn(Long companyId, List<Integer> month);
	
	List<EmployeeSalaryDetails>  findByCompanyInfoCompanyIdAndIsPaidAndMonthAndYearAndEmployeePersonalInfoEmployeeInfoIdIn(Long companyId, Boolean ispaid, Integer month, Integer year, List<Long> employeeInfoIdList);
	
	List<EmployeeSalaryDetails>  findByCompanyInfoCompanyIdAndIsPaidAndIsFinalized(Long companyId, Boolean ispaid, Boolean isFinalized);
	
	List<EmployeeSalaryDetails>  findByCompanyInfoCompanyIdAndIsPaidAndIsFinalizedAndIsPayslipGenerated(Long companyId, Boolean ispaid, Boolean isFinalized, Boolean isPayslipGenerated);
	
	List<EmployeeSalaryDetails>  findByCompanyInfoCompanyIdAndIsPaidAndIsPayslipGeneratedAndMonthAndEmployeePersonalInfoEmployeeInfoIdIn(Long companyId, Boolean ispaid, Boolean isPayslipGenerated, Integer month, List<Long> employeeInfoIdList);

	List<EmployeeSalaryDetails> findByemployeeSalaryIdAndCompanyInfoCompanyId(Long employeeSalaryId, Long companyId);
	
	List<EmployeeSalaryDetails> findByCompanyInfoCompanyIdAndIsPaidAndYear(Long companyId, Boolean ispaid, Integer year);

	List<EmployeeSalaryDetails> findByEmployeeSalaryIdInAndCompanyInfoCompanyId(ArrayList<Long> employeeSalaryIdList, Long companyId);

	List<EmployeeSalaryDetails>  findByCompanyInfoCompanyIdAndMonthInAndIsFinalizedFalse(Long companyId, List<Integer> month);


	List<EmployeeSalaryDetails> findByCompanyInfoCompanyIdAndMonthInAndIsFinalizedFalseAndEmployeePersonalInfoEmployeeOfficialInfoDepartmentIn(
			Long companyId, List<Integer> month, List<String> departmentNameList);
	
	List<EmployeeSalaryDetails> findByCompanyInfoCompanyIdAndMonthAndYear(Long companyId, Integer month, Integer year);
	
	Optional<EmployeeSalaryDetails> findByEmployeePersonalInfoEmployeeInfoIdAndMonthAndYear(Long employeeInfoId, Integer month, Integer year);
	
	Optional<EmployeeSalaryDetails> findByEmployeePersonalInfoEmployeeInfoIdAndMonthAndYearAndCompanyInfoCompanyId(Long employeeInfoId, Integer month, Integer year,Long companyId);

	List<EmployeeSalaryDetails> findByMonthAndYearAndCompanyInfoCompanyIdIn(Integer monthValue, Integer year,
			List<Long> companyIds);
	
}
