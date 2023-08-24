package com.te.flinko.service.reportingmanager;

import java.util.List;

import com.te.flinko.dto.reportingmanager.AttendanceApprovalDTO;
import com.te.flinko.dto.reportingmanager.EmployeeAttendanceDetailsDTO;

public interface EmployeeAttendanceService {

	List<EmployeeAttendanceDetailsDTO> getAllAttendanceDetails(Long companyId, Long employeeInfoId);

	EmployeeAttendanceDetailsDTO getAttendanceDetailsById(AttendanceApprovalDTO attendanceApprovalDTO);

	AttendanceApprovalDTO updateStatus(AttendanceApprovalDTO attendanceApprovalDTO);

	List<EmployeeAttendanceDetailsDTO> getMissedPunchIn(Long companyId, Long employeeInfoId);

	List<EmployeeAttendanceDetailsDTO> getMissedPunchOut(Long companyId, Long employeeInfoId);

	AttendanceApprovalDTO updatePunchInOut(AttendanceApprovalDTO attendanceApprovalDTO);

}
