package com.te.flinko.service.notification.employee;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.te.flinko.dto.employee.EmployeeNotificationDTO;
import com.te.flinko.dto.superadmin.CompanyNotificationDTO;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeNotification;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.superadmin.CompanyNotification;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeNotificationRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.superadmin.CompanyNotificationRepository;

@Service
public class InAppNotificationServiceImpl implements InAppNotificationService{

	@Autowired
	private CompanyInfoRepository companyInfoRepository;
	
	@Autowired
	private CompanyNotificationRepository companyNotificationRepository;
	
	@Autowired
	private EmployeeNotificationRepository employeeNotificationRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Override
	public List<EmployeeNotificationDTO> getNotification(Long employeeInfoId) {
		List<EmployeeNotification> employeeNotificationList = employeeNotificationRepository
				.findByReceiverEmployeePersonalInfoEmployeeInfoId(employeeInfoId);
		Collections.reverse(employeeNotificationList);
		return employeeNotificationList.stream()
				.map(notification -> EmployeeNotificationDTO.builder().description(notification.getDescription())
						.employeeNotificationId(notification.getEmployeeNotificationId())
						.isSeen(notification.getIsSeen()).build())
				.collect(Collectors.toList());
	}

	@Transactional
	public EmployeeNotificationDTO saveNotification(String message, Long employeeInfoId) {
		EmployeePersonalInfo employee = employeePersonalInfoRepository.findById(employeeInfoId)
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		EmployeeNotification employeeNotification = new EmployeeNotification();
		employeeNotification.setDescription(message);
		employeeNotification.setReceiverEmployeePersonalInfo(employee);
		employeeNotification.setIsSeen(Boolean.FALSE);
		employeeNotification = employeeNotificationRepository.save(employeeNotification);
		return EmployeeNotificationDTO.builder().description(employeeNotification.getDescription())
				.employeeNotificationId(employeeNotification.getEmployeeNotificationId()).build();
	}
	
	@Transactional
	public CompanyNotificationDTO saveCompanyNotification(String message, Long companyId) {
		CompanyInfo companyInfo = companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		CompanyNotification notification = new CompanyNotification();
		notification.setDescription(message);
		notification.setIsSeen(Boolean.FALSE);
		notification.setCompanyInfo(companyInfo);
		notification = companyNotificationRepository.save(notification);
		return CompanyNotificationDTO.builder().description(notification.getDescription())
				.companyNotificationId(notification.getCompanyNotificationId()).build();
	}

	@Override
	@Transactional
	public Boolean updateNotification(Long employeeInfoId) {
		employeeNotificationRepository.findByReceiverEmployeePersonalInfoEmployeeInfoIdAndIsSeenFalse(employeeInfoId)
				.stream().forEach(notification -> notification.setIsSeen(Boolean.TRUE));
		return Boolean.TRUE;
	}

}
