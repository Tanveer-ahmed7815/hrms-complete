package com.te.flinko.repository.employee.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.te.flinko.entity.employee.mongo.EmployeeAttendanceDetails;

public interface EmployeeAttendanceDetailsRepository extends MongoRepository<EmployeeAttendanceDetails, String> {

	Optional<EmployeeAttendanceDetails> findByEmployeeInfoIdAndMonthNoAndYear(Long employeeInfoId, Integer monthNo,
			Integer year);

	Optional<EmployeeAttendanceDetails> findByEmployeeInfoIdAndMonthNoAndYearAndAttendanceDetailsPunchInBetween(
			Long employeeInfoId, Integer monthNo, Integer year, LocalDateTime punchInStart, LocalDateTime punchInEnd);

	Optional<EmployeeAttendanceDetails> findByAttendanceObjectId(String attendanceObjectId);

	List<EmployeeAttendanceDetails> findByCompanyIdAndEmployeeInfoIdInAndAttendanceDetailsIsInsideLocationAndAttendanceDetailsPunchOutIsNotNullAndAttendanceDetailsPunchInIsNotNull(
			Long companyId, List<Long> employeeIdList, Boolean isInsideLocation);

	List<EmployeeAttendanceDetails> findByCompanyIdAndEmployeeInfoIdInAndAttendanceDetailsPunchOutIsNull(Long companyId,
			List<Long> employeeIdList);

	List<EmployeeAttendanceDetails> findByCompanyIdAndEmployeeInfoIdInAndAttendanceDetailsPunchInIsNull(Long companyId,
			List<Long> employeeIdList);

	List<EmployeeAttendanceDetails> findByCompanyIdAndMonthNoAndYear(Long companyId, Integer monthNo, Integer year);
}
