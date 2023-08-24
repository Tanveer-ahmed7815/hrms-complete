package com.te.flinko.service.sales;

import com.te.flinko.dto.DepartmentProjectDTO;
import com.te.flinko.dto.sales.EventBirthdayOtherDetailsDTO;

public interface CommonDashboardService {
	EventBirthdayOtherDetailsDTO getEventsBirthdayOtherDetails(Long companyId, Long employeeInfoId,DepartmentProjectDTO departmentProjectDTO);

	String sendWishes(Long companyId, Long employeeInfoIdFrom,Long employeeInfoIdTo);
}
