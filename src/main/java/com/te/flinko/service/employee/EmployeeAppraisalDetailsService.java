package com.te.flinko.service.employee;

import com.te.flinko.dto.employee.AppraisalMeetingDTO;

public interface EmployeeAppraisalDetailsService {
	
	AppraisalMeetingDTO getEmployeeApprisalDetails(Long employeeInfoId);

}
