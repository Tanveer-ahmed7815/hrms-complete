package com.te.flinko.service.employee;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.employee.EmployeeExtraWorkDTO;
import com.te.flinko.entity.employee.EmployeeExtraWorkDetails;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.employee.EmployeeExtraWorkDetailsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;

@Service
public class EmployeeExtraWorkServiceImpl implements EmployeeExtraWorkService {

	@Autowired
	private EmployeeExtraWorkDetailsRepository employeeExtraWorkDetailsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Override
	public EmployeeExtraWorkDTO saveExtraWorkDetails(EmployeeExtraWorkDTO employeeExtraWorkDTO, Long employeeInfoId) {
		EmployeePersonalInfo personalInfo = employeePersonalInfoRepository.findById(employeeInfoId)
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		List<EmployeeExtraWorkDetails> existingExtraWorks = personalInfo.getEmployeeExtraWorkDetailsList().stream()
				.filter(extraWork -> extraWork.getDate().equals(employeeExtraWorkDTO.getDate()))
				.collect(Collectors.toList());
		if (!existingExtraWorks.isEmpty()) {
			throw new DataNotFoundException("Already Submitted");
		}
		EmployeeExtraWorkDetails employeeExtraWorkDetails = new EmployeeExtraWorkDetails();
		BeanUtils.copyProperties(employeeExtraWorkDTO, employeeExtraWorkDetails);
		employeeExtraWorkDetails.setStatus("PENDING");
		employeeExtraWorkDetails.setEmployeePersonalInfo(personalInfo);
		employeeExtraWorkDetails.setProjectTaskDetails(employeeExtraWorkDTO.getProjectTaskDetails());
		employeeExtraWorkDetailsRepository.save(employeeExtraWorkDetails);
		return employeeExtraWorkDTO;
	}

}
