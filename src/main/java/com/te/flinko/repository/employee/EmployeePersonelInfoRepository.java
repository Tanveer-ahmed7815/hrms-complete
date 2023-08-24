package com.te.flinko.repository.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.te.flinko.dto.helpandsupport.mongo.ReportingManagerDto;
import com.te.flinko.entity.employee.CompanyEmployeeResignationDetails;
import com.te.flinko.entity.employee.EmployeePersonalInfo;

@Repository
public interface EmployeePersonelInfoRepository extends JpaRepository<EmployeePersonalInfo, Long> {

	// @Modifying
	// @Query("from EmployeePersonalInfo")
	// List<EmployeePersonalInfo> findByEmployeeInfoId();

	List<EmployeePersonalInfo> findByCompanyInfoCompanyId(Long companyId);

	List<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndEmployeeOfficialInfoDepartmentIn(Long companyId,
			List<String> department);

	List<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(Long companyId, String employeeId);
	
	List<EmployeePersonalInfo> findByEmployeeInfoIdIn(List<Long> employeeInfoId);

	@Query(value = "select new com.te.flinko.dto.helpandsupport.mongo.ReportingManagerDto(epi.employeeOfficialInfo.employeeId,epi.firstName ,epi.lastName)from EmployeePersonalInfo epi where epi.companyInfo.companyId=?1 and epi.employeeOfficialInfo.department=?2")
	public List<ReportingManagerDto> findByCompanyIdAndDepartment(Long id, String department);
}
