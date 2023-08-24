package com.te.flinko.repository.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.employee.EmployeeReimbursementInfo;

@Repository
public interface EmployeeReimbursementInfoRepository extends JpaRepository<EmployeeReimbursementInfo, Long> {

	Optional<EmployeeReimbursementInfo> findByReimbursementIdAndEmployeePersonalInfoCompanyInfoCompanyId(
			Long reimbursementId, Long companyId);

	Optional<EmployeeReimbursementInfo> findByReimbursementIdAndEmployeePersonalInfoCompanyInfoCompanyIdAndEmployeePersonalInfoEmployeeInfoId(
			Long employeeReimbursementId, Long companyId, Long employeeInfoId);

	List<EmployeeReimbursementInfo> findByStatusIgnoreCaseAndEmployeePersonalInfoCompanyInfoCompanyId(String status,
			Long companyId);
	
	List<EmployeeReimbursementInfo> findByStatusAndEmployeePersonalInfoCompanyInfoCompanyIdAndEmployeePersonalInfoEmployeeInfoIdIn(String status,
			Long companyId, List<Long> employeeInfoIdList);

	List<EmployeeReimbursementInfo> findByCompanyExpenseCategoriesCompanyInfoCompanyId(Long companyId);
	
	List<EmployeeReimbursementInfo> findByCompanyExpenseCategoriesCompanyInfoCompanyIdAndCompanyExpenseCategoriesExpenseCategoryId(Long companyId, Long expenseCategoryId);

	List<EmployeeReimbursementInfo> findByEmployeePersonalInfoCompanyInfoCompanyId(Long companyId);
	
	List<EmployeeReimbursementInfo> findByEmployeePersonalInfoCompanyInfoCompanyIdAndStatusIgnoreCaseAndIsPaidIsNullOrIsPaid(Long companyId, String status, Boolean isPaid);

	List<EmployeeReimbursementInfo> findByReimbursementIdInAndEmployeePersonalInfoCompanyInfoCompanyId(ArrayList<Long> reimbursementIds,
			Long companyId);

}
