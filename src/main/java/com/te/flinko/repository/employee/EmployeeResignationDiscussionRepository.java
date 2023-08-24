package com.te.flinko.repository.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.employee.CompanyEmployeeResignationDetails;
import com.te.flinko.entity.employee.EmployeeResignationDiscussion;
@Repository
public interface EmployeeResignationDiscussionRepository extends JpaRepository<EmployeeResignationDiscussion, Long>{


	Optional<EmployeeResignationDiscussion> findByCompanyEmployeeResignationDetails(CompanyEmployeeResignationDetails companyEmployeeResignationDetails);
	
	List<EmployeeResignationDiscussion> findByCompanyEmployeeResignationDetailsResignationId(Long resignationId);
}
