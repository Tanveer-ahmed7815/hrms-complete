package com.te.flinko.repository.helpandsupport.mongo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.helpandsupport.mongo.CompanyAdminDeptTickets;

public interface CompanyAdminDeptTicketsRepo extends MongoRepository<CompanyAdminDeptTickets, String> {

	Optional<CompanyAdminDeptTickets> findByObjectTicketIdAndTicketHistroysStatusIgnoreCase(String objectTicketId,
			String status);

	public List<CompanyAdminDeptTickets> findByCompanyIdAndTicketHistroysDepartment(Long companyId, String department);

	public List<CompanyAdminDeptTickets> findByCompanyIdAndTicketHistroysStatusIgnoreCase(Long companyId,
			String status);

	public List<CompanyAdminDeptTickets> findByCompanyId(Long companyId);

	public List<CompanyAdminDeptTickets> findByCompanyIdAndCategory(Long companyId, String category);

	List<CompanyAdminDeptTickets> findByCategoryAndSubCategoryAndCompanyIdAndEmployeeIdAndTicketHistroysDate(
			String category, String subCategory, Long companyId, String employeeId, LocalDate date);

	List<CompanyAdminDeptTickets> findByCompanyIdAndMonitoringDepartment(Long companyId, String monitoringDepartment);

	List<CompanyAdminDeptTickets> findByCompanyIdAndMonitoringDepartmentAndTicketHistroysStatusIgnoreCaseNot(
			Long companyId, String monitoringDepartment, String status);

	public Optional<CompanyAdminDeptTickets> findByCompanyIdAndObjectTicketIdAndTicketHistroysDepartment(Long companyId,
			String id, String department);

	Optional<CompanyAdminDeptTickets> findByCompanyIdAndObjectTicketIdAndMonitoringDepartment(Long companyId, String id,
			String monitoringDepartment);

	List<CompanyAdminDeptTickets> findByCompanyIdAndMonitoringDepartmentIgnoreCase(Long companyId,
			String monitoringDepartment);

	List<CompanyAdminDeptTickets> findByCompanyIdAndMonitoringDepartmentIgnoreCaseAndTicketHistroysStatusIgnoreCaseNot(
			Long companyId, String monitoringDepartment, String status);

	Optional<CompanyAdminDeptTickets> findByCompanyIdAndObjectTicketId(Long companyId, String id);

	public List<CompanyAdminDeptTickets> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

	List<CompanyAdminDeptTickets> findByCompanyIdAndEmployeeId(Long companyId, String employeeId);

	List<CompanyAdminDeptTickets> findByCompanyIdAndTicketHistroysByAndTicketHistroysStatus(Long companyId, Long by,
			String status);
}
