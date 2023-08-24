package com.te.flinko.repository.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.te.flinko.entity.employee.EmployeeNotification;

@Repository
public interface EmployeeNotificationRepository extends JpaRepository<EmployeeNotification, Long> {
	
	List<EmployeeNotification> findByReceiverEmployeePersonalInfoEmployeeInfoId(Long employeeInfoId);
	
	List<EmployeeNotification> findByReceiverEmployeePersonalInfoEmployeeInfoIdAndIsSeenFalse(Long employeeInfoId);
	
	List<EmployeeNotification> findByIsSeenTrue();

}
