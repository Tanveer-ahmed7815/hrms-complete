package com.te.flinko.repository.project;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.te.flinko.entity.project.ProjectDetails;

public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long> {

	List<ProjectDetails> findByProjectIdAndCompanyInfoCompanyIdAndEmployeePersonalInfoListEmployeeOfficialInfoEmployeeId(
			Long projectId, Long companyId, String employeeId);

	List<ProjectDetails> findByProjectName(String projectName);

	List<ProjectDetails> findByProjectNameAndCompanyInfoCompanyId(String projectName, Long companyId);

	List<ProjectDetails> findByCompanyInfoCompanyId(Long companyId);

	List<ProjectDetails> findByCompanyInfoCompanyIdAndProjectId(Long companyId, Long projectId);

	List<ProjectDetails> findByCompanyInfoCompanyIdAndProjectEstimationDetailsStatusAndProjectEstimationDetailsEndDateAfter(
			Long companyId, String approved, LocalDate currentDate);
	
	List<ProjectDetails> findByEmployeePersonalInfoListEmployeeInfoId(Long employeeInfoId);

}