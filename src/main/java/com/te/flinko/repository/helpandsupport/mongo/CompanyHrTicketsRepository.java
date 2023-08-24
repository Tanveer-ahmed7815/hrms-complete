package com.te.flinko.repository.helpandsupport.mongo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.helpandsupport.mongo.CompanyHrTickets;

public interface CompanyHrTicketsRepository extends MongoRepository<CompanyHrTickets, String> {

	List<CompanyHrTickets> findByCompanyId(Long companyId);

	List<CompanyHrTickets> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

	CompanyHrTickets findByTicketId(Long hrTicketId);

	List<CompanyHrTickets> findByCategoryAndSubCategoryAndCompanyIdAndEmployeeIdAndTicketHistroysDate(String category,
			String subCategory, Long companyId, String employeeId, LocalDate date);

	public Optional<CompanyHrTickets> findByCompanyIdAndTicketObjectIdAndTicketHistroysDepartment(Long companyId,
			String id, String department);
	
	
	Optional<CompanyHrTickets> findByCompanyIdAndTicketObjectIdAndMonitoringDepartment(Long companyId, String id,String monitoringDepartment);
	
	List<CompanyHrTickets> findByCompanyIdAndMonitoringDepartmentIgnoreCase(Long companyId, String monitoringDepartment);
	
	List<CompanyHrTickets> findByCompanyIdAndMonitoringDepartmentIgnoreCaseAndTicketHistroysStatusIgnoreCaseNot(Long companyId, String monitoringDepartment, String status);

	Optional<CompanyHrTickets> findByCompanyIdAndTicketObjectId(Long companyId, String id);
	List<CompanyHrTickets> findByCompanyIdAndEmployeeId(Long companyId, String employeeId);
	
	List<CompanyHrTickets> findByCompanyIdAndTicketHistroysByAndTicketHistroysStatus(Long companyId, Long by,String status);

}
