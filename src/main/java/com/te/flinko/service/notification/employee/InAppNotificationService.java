package com.te.flinko.service.notification.employee;

import java.util.List;

import com.te.flinko.dto.employee.EmployeeNotificationDTO;

public interface InAppNotificationService {
	
	List<EmployeeNotificationDTO> getNotification(Long employeeInfoId);
	
	Boolean updateNotification(Long employeeInfoId);
	

}
