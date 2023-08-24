package com.te.flinko.service.employee;

import com.te.flinko.dto.employee.EmployeeExtraWorkDTO;

public interface EmployeeExtraWorkService {
	
	EmployeeExtraWorkDTO saveExtraWorkDetails(EmployeeExtraWorkDTO employeeExtraWorkDTO, Long employeeInfoId);

}
