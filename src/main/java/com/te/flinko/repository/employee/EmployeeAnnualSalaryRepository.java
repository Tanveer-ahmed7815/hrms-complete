package com.te.flinko.repository.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.admin.CompanyPayrollInfo;
import com.te.flinko.entity.employee.EmployeeAnnualSalary;
import com.te.flinko.entity.employee.EmployeePersonalInfo;

@Repository
public interface EmployeeAnnualSalaryRepository  extends JpaRepository<EmployeeAnnualSalary, Long>{
	
	List<EmployeeAnnualSalary> findByEmployeePersonalInfoCompanyInfoCompanyId(Long companyId);
	
	EmployeeAnnualSalary findByEmployeePersonalInfo(EmployeePersonalInfo employeePersonalInfo);

	List<EmployeeAnnualSalary> findByEmployeePersonalInfoEmployeeInfoId(Long employeeInfoId);
	
	List<EmployeeAnnualSalary> findByCompanyPayrollInfoIn(List<CompanyPayrollInfo> companyPayrollInfoList);

	List<EmployeeAnnualSalary> findByCompanyPayrollInfoInAndEmployeePersonalInfoEmployeeInfoIdNotIn(
			List<CompanyPayrollInfo> payrollList, List<Long> employeeInfoIds);
	
}
