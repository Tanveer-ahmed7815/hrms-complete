package com.te.flinko.controller.notification.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.notification.employee.InAppNotificationService;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RequestMapping("/api/v1/notification")
@RestController
public class InAppNotificationController {
	
	@Autowired
	private InAppNotificationService inAppNotificationService;

	@GetMapping("/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> getNotification(@PathVariable Long employeeInfoId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder().error(Boolean.FALSE).message("Notification Fetched Successfully")
				.data(inAppNotificationService.getNotification(employeeInfoId)).build());
	}

	@PutMapping("/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> updateNotification(@PathVariable Long employeeInfoId) {
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(false)
				.message("Updated").data(inAppNotificationService.updateNotification(employeeInfoId)).build());

	}

}
