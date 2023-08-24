package com.te.flinko.repository.employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.te.flinko.dto.employee.EmployeeDropdownDTO;
import com.te.flinko.dto.employee.EmployeeIdDto;
import com.te.flinko.dto.employee.EmployeeName;
import com.te.flinko.dto.hr.EmployeeBasicDetailsDTO;
import com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;

/**
 * 
 * @author Ravindra
 *
 */
@Repository
public interface EmployeePersonalInfoRepository extends JpaRepository<EmployeePersonalInfo, Long> {

	List<EmployeePersonalInfo> findByFirstNameAndLastName(String employeeFirstName, String employeeLastName);

	Optional<EmployeePersonalInfo> findByEmployeeInfoIdAndCompanyInfoIsActiveTrue(Long employeeInfoId);

	Optional<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndEmployeeOfficialInfoOfficialId(Long companyId,
			Long officialId);

	List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId(String employeeId);

	List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(String employeeId,
			Long companyId);

	List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeTypeInAndCompanyInfoCompanyId(
			List<String> employeeType, Long companyId);

	List<EmployeePersonalInfo> findByEmployeeOfficialInfoIn(List<EmployeeOfficialInfo> employeeinfoUsingId);

	EmployeePersonalInfo findByEmployeeInfoIdAndCompanyInfoCompanyId(Long employeeInfoId, Long companyId);

	Optional<EmployeePersonalInfo> findByEmployeeInfoIdAndIsActiveTrue(Long employeeInfoId);

	public List<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndStatus(Long companyId, Map<String, String> status);

	public List<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndIsActiveTrue(Long companyId);
	
	List<EmployeePersonalInfo> findByCompanyInfoCompanyId(Long companyId);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeReimbursementInfoListStatus(
			Long companyId, String status);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeLeaveAppliedListStatus(Long companyId,
			String status);

	Optional<List<EmployeePersonalInfo>> findByEmployeeLeaveAppliedListStatusAndCompanyInfoCompanyId(Long long1,
			Long companyId);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeLeaveAppliedListStatusIn(Long companyId,
			List<String> status);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeReimbursementInfoListStatusIn(
			Long companyId, List<String> status);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndCompanyInfoCompanyPayrollInfoListIsAdvanceSalaryEnabledAndEmployeeAdvanceSalaryListStatusIn(
			Long companyId, Boolean isAdvanceSalaryEnabled, List<String> status);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdOrEmployeeOfficialInfoEmployeeIdOrMobileNumberOrEmployeeOfficialInfoOfficialEmailId(
			Long companyId, String employeeId, Long mobileNo, String emailId);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(Long companyId,
			String employeeId);

	List<EmployeePersonalInfo> findByMobileNumberOrEmployeeOfficialInfoOfficialEmailId(Long mobileNo, String emailId);

	List<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndEmployeeOfficialInfoDepartmentIn(Long companyId,
			List<String> department);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndCompanyInfoCompanyPayrollInfoListIsAdvanceSalaryEnabledAndEmployeeAdvanceSalaryListStatus(
			Long companyId, Boolean advancedSalaryEnable, String status);

	Optional<List<EmployeePersonalInfo>> findByIsActiveTrueAndCompanyInfoCompanyIdAndEmployeeReferenceInfoListRewardAmountNullAndEmployeeReferenceInfoListEmployeePersonalInfoNotNullAndEmployeeReferenceInfoListEmployeePersonalInfoIsActiveTrueAndEmployeeOfficialInfoDojLessThanEqual(
			Long companyId, LocalDate doj);

	Optional<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndEmployeeReferenceInfoListReferenceId(Long companyId,
			Long referenceId);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeIdIn(Long companyId,
			List<String> employeeIds);

	EmployeePersonalInfo findByEmployeeInfoIdAndIsActiveAndCompanyInfoCompanyId(Long employeeInfoId, boolean b,
			Long companyId);

	Optional<List<EmployeePersonalInfo>> findByIsActiveTrueAndCompanyInfoCompanyIdAndEmployeeReferenceInfoListRewardAmountNullAndEmployeeReferenceInfoListEmployeePersonalInfoNotNullAndEmployeeReferenceInfoListEmployeePersonalInfoIsActiveTrueAndEmployeeReferenceInfoListEmployeePersonalInfoEmployeeOfficialInfoDojLessThanEqual(
			Long companyId, LocalDate minusDays);

	Optional<List<EmployeePersonalInfo>> findByCompanyInfoCompanyIdAndEmployeeInfoIdIn(Long companyId,
			List<Long> employeeInfoIdList);

	List<EmployeePersonalInfo> findByCompanyInfoCompanyIdAndEmployeeInfoId(Long companyId, Long employeeId);

	@Query(value = "select new com.te.flinko.dto.employee.EmployeeIdDto(epi.employeeInfoId,epi.firstName,epi.lastName)from EmployeePersonalInfo epi where epi.companyInfo.companyId=?1")
	public Optional<List<EmployeeIdDto>> getEmployeeNames(@Param("companyId") Long companyId);

	public Optional<List<EmployeePersonalInfo>> findByIsActiveAndCompanyInfoCompanyId(boolean b, Long companyId);

	List<EmployeePersonalInfo> findByEmployeeOfficialInfoOfficialId(Long employeeInfoId);

	List<EmployeeName> findByEmployeeInfoIdIn(Set<Long> employeeInfoIdList);

	public Optional<List<EmployeePersonalInfo>> findByIsActiveTrueAndCompanyInfoCompanyId(Long companyId);

	@Query(value = "select new com.te.flinko.dto.employee.EmployeeDropdownDTO(epi.employeeInfoId,epi.firstName, epi.lastName,epi.employeeOfficialInfo.employeeId)from EmployeePersonalInfo epi where epi.companyInfo.companyId=?1 and epi.isActive=true")
	public Optional<List<EmployeeDropdownDTO>> getEmployeeNameInfoIdOffId(Long companyId);

	// List<EmployeePersonalInfo>
	// findByCompanyInfoCompanyIdAndEmployeeReportingInfoListIn(Long
	// companyId,List<>);

//	@Query(value = "select new com.te.flinko.dto.employee.EmployeeDropdownDTO.(epi.employeeInfoId,employeeId,epi.firstName,epi.lastName)"+"(from EmployeePersonalInfo epi where epi.companyInfo.companyId=?1")
//	public Optional<List<EmployeePersonalInfo>> findByIsActiveTrueAndCompanyInfoCompanyId(Long companyId);

	List<EmployeePersonalInfo> findByIsActiveTrueAndCompanyInfoCompanyIdAndEmployeeOfficialInfoNotNull(Long companyId);

	List<EmployeePersonalInfo> findByIsActiveTrueAndCompanyInfoCompanyIdInAndEmployeeOfficialInfoNotNull(
			List<Long> companyIds);

	Optional<List<EmployeePersonalInfo>> findByIsActiveTrueAndCompanyInfoNotNullAndCompanyInfoIsActiveTrueAndCompanyInfoIsSubmitedTrueAndEmployeeOfficialInfoNotNull();

	List<EmployeePersonalInfo> findByIsActiveTrueAndCompanyInfoCompanyIdAndEmployeeOfficialInfoNotNullAndDobIn(
			Long companyId, List<LocalDate> dob);

	List<EmployeePersonalInfo> findByIsActiveTrueAndCompanyInfoCompanyIdAndDob(Long companyId, LocalDate of);

	@Query(value = "SELECT new com.te.flinko.dto.hr.EmployeeBasicDetailsDTO(epi.employeeInfoId, epi.firstName ||' '|| epi.lastName ,epi.emailId,epi.mobileNumber,epi.gender,eoi.employeeId, eoi.officialEmailId, "
			+ "eoi.doj, eoi.companyBranchInfo.branchName,eoi.department, eoi.designation) FROM EmployeePersonalInfo epi left outer join EmployeeOfficialInfo eoi on "
			+ "epi.employeeOfficialInfo.officialId = eoi.officialId where epi.companyInfo.companyId = ?1")
	public List<EmployeeBasicDetailsDTO> getBasicDetails(Long companyId);

	@Query(value = "SELECT new com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO(epi.employeeInfoId,eoi.employeeId, epi.firstName ||' '|| epi.lastName , eoi.officialEmailId, "
			+ "eoi.department, eoi.designation, epi.isActive, epi.status) FROM EmployeePersonalInfo epi left outer join EmployeeOfficialInfo eoi on "
			+ "epi.employeeOfficialInfo.officialId = eoi.officialId where epi.companyInfo.companyId = ?1")
	public List<EmployeeDisplayDetailsDTO> getEmployeeDetails(Long companyId);
	
	@Query(value = "SELECT new com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO(epi.employeeInfoId,eoi.employeeId, epi.firstName ||' '|| epi.lastName , eoi.officialEmailId, "
			+ "eoi.department, eoi.designation, epi.isActive, epi.status) FROM EmployeePersonalInfo epi left outer join EmployeeOfficialInfo eoi on "
			+ "epi.employeeOfficialInfo.officialId = eoi.officialId where epi.companyInfo.companyId = ?1 and epi.isActive = true")
	public List<EmployeeDisplayDetailsDTO> getActiveEmployeeDetails(Long companyId);

	@Query(value = "SELECT new com.te.flinko.dto.hr.EmployeeBasicDetailsDTO(epi.employeeInfoId, epi.firstName ||' '|| epi.lastName ,eoi.employeeId, "
			+ "eoi.department, eoi.designation) FROM EmployeePersonalInfo epi left outer join EmployeeOfficialInfo eoi on "
			+ "epi.employeeOfficialInfo.officialId = eoi.officialId where epi.employeeInfoId in ?1")
	public List<EmployeeBasicDetailsDTO> getEmployeeDetails(List<Long> employeeIdList);

	List<EmployeePersonalInfo> findByCandidateInfoCandidateId(Long candidateId);

}
