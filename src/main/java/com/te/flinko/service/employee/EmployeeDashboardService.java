package com.te.flinko.service.employee;

import java.util.List;

import com.te.flinko.dto.employee.EmployeeProjectDetailsDTO;
import com.te.flinko.dto.employee.EmployeeProjectListDTO;

public interface EmployeeDashboardService {
	
	List<EmployeeProjectListDTO> getAllProjectNames(Long employeeInfoId);
	
	EmployeeProjectDetailsDTO getProjectDetailsById(Long projectId, Long companyId);

}
