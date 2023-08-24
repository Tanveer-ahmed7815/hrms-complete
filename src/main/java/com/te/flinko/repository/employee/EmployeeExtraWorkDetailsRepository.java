package com.te.flinko.repository.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.employee.EmployeeExtraWorkDetails;

@Repository
public interface EmployeeExtraWorkDetailsRepository extends JpaRepository<EmployeeExtraWorkDetails, Long> {
	
	List<EmployeeExtraWorkDetails> findByEmployeePersonalInfoEmployeeInfoIdInAndStatusIgnoreCase(List<Long> employeeInfoIdList, String status);
	
	Optional<EmployeeExtraWorkDetails> findByExtraWorkIdAndStatusIgnoreCase(Long extraWorkId, String status);

}
