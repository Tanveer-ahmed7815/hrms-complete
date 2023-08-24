package com.te.flinko.service.notification.employee;

import com.te.flinko.dto.employee.EmployeeExpoTokenDTO;

public interface PushNotificationService {
	
	void pushMessage(String title, String message, String recipient);
	
	Boolean updateExpoToken(EmployeeExpoTokenDTO employeeExpoTokenDTO);

}
