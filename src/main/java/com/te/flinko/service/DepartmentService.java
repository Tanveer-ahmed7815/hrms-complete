package com.te.flinko.service;

import java.util.List;

import com.te.flinko.dto.hr.EventManagementDepartmentNameDTO;

public interface DepartmentService {

	List<EventManagementDepartmentNameDTO> fetchDepartmentFromPlan(Long companyId);
	
}
