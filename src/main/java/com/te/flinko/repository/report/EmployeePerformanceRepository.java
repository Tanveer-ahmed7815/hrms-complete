package com.te.flinko.repository.report;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.report.mongo.EmployeePerformance;

public interface EmployeePerformanceRepository extends MongoRepository<EmployeePerformance, String> {
	Optional<EmployeePerformance> findByCompanyIdAndEmployeeInfoIdAndYear(Long companyId, Long employeeInfoId,
			Long year);

	List<EmployeePerformance> findByCompanyIdAndYear(Long companyId, Long year);
}
