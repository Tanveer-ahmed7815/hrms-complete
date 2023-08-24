package com.te.flinko.service.reportingmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.dto.employee.mongo.EmployeeProjectTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.Timesheet;
import com.te.flinko.dto.reportingmanager.ExtraWorkDTO;
import com.te.flinko.entity.employee.EmployeeExtraWorkDetails;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.EmployeeTimesheetDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.project.mongo.ProjectTaskDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.employee.EmployeeExtraWorkDetailsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeTimesheetDetailsRepository;
import com.te.flinko.repository.project.ProjectDetailsRepository;
import com.te.flinko.repository.project.mongo.ProjectTaskDetailsRepository;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.service.notification.employee.PushNotificationService;

@Service
public class ExtraWorkApprovalServiceImpl implements ExtraWorkApprovalService {

	@Autowired
	private EmployeeExtraWorkDetailsRepository employeeExtraWorkDetailsRepository;

	@Autowired
	private ProjectTaskDetailsRepository projectTaskDetailsRepository;

	@Autowired
	private ProjectDetailsRepository projectDetailsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	private EmployeeTimesheetDetailsRepository employeeTimesheetDetailsRepository;

	@Autowired
	private InAppNotificationServiceImpl notificationServiceImpl;

	@Autowired
	private PushNotificationService pushNotificationService;

	@Override
	public List<ExtraWorkDTO> getAllExtraWorkDetails(Long employeeInfoId, String status) {
		EmployeePersonalInfo loggedInUser = employeePersonalInfoRepository.findById(employeeInfoId)
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		List<Long> employeeInfoIdList = loggedInUser.getEmployeeReportingInfoList().stream()
				.map(reportingDetails -> reportingDetails.getEmployeePersonalInfo().getEmployeeInfoId())
				.collect(Collectors.toList());
		List<ExtraWorkDTO> extraWorkDTOList = new ArrayList<>();
		List<EmployeeExtraWorkDetails> extraWorkList = employeeExtraWorkDetailsRepository
				.findByEmployeePersonalInfoEmployeeInfoIdInAndStatusIgnoreCase(employeeInfoIdList, status);
		extraWorkList.stream().forEach(extraWork -> {
			EmployeePersonalInfo employeePersonalInfo = extraWork.getEmployeePersonalInfo();
			EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
			List<EmployeeTimesheetDetails> employeeTimesheetDetails = employeeTimesheetDetailsRepository
					.findByTimesheetsDateAndEmployeeIdAndIsSubmitted(extraWork.getDate(),
							employeeOfficialInfo.getEmployeeId(), true);
			if (!employeeTimesheetDetails.isEmpty()) {
				ExtraWorkDTO extraWorkDTO = new ExtraWorkDTO();

				extraWorkDTO.setEmployeeId(employeeOfficialInfo.getEmployeeId());
				extraWorkDTO.setBranch(employeeOfficialInfo.getCompanyBranchInfo() == null ? null
						: employeeOfficialInfo.getCompanyBranchInfo().getBranchName());
				extraWorkDTO.setDepartment(employeeOfficialInfo.getDepartment());
				extraWorkDTO.setDesignation(employeeOfficialInfo.getDesignation());
				extraWorkDTO
						.setFullName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
				BeanUtils.copyProperties(extraWork, extraWorkDTO);
				extraWorkDTOList.add(extraWorkDTO);

			}
		});
		return extraWorkDTOList;
	}

	@Override
	@Transactional
	public Boolean updateStatus(Long extraWorkId, AdminApprovedRejectDto adminApprovedRejectDTO) {
		EmployeeExtraWorkDetails extraWorkDetails = employeeExtraWorkDetailsRepository
				.findByExtraWorkIdAndStatusIgnoreCase(extraWorkId, "Pending")
				.orElseThrow(() -> new DataNotFoundException("Extra Work Details is Not PENDING"));
		EmployeePersonalInfo employeePersonalInfo = extraWorkDetails.getEmployeePersonalInfo();
		if (employeePersonalInfo == null) {
			throw new DataNotFoundException("Employee Not Found");
		}
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		if (employeeOfficialInfo == null) {
			throw new DataNotFoundException("Employee Official Details Not Found");
		}
		extraWorkDetails.setStatus(adminApprovedRejectDTO.getStatus());
		if (adminApprovedRejectDTO.getStatus().equalsIgnoreCase("Rejected")) {
			extraWorkDetails.setRejectionReason(adminApprovedRejectDTO.getReason());

			notificationServiceImpl.saveNotification(
					"Extra Work request for " + extraWorkDetails.getDate() + " is Rejected",
					employeePersonalInfo.getEmployeeInfoId());

			if (employeePersonalInfo.getExpoToken() != null) {
				pushNotificationService.pushMessage("Flinko",
						"Extra Work request for " + extraWorkDetails.getDate() + " is Rejected",
						employeePersonalInfo.getExpoToken());
			}
		} else {
			List<EmployeeTimesheetDetails> employeeTimesheetDetails = employeeTimesheetDetailsRepository
					.findByTimesheetsDateAndEmployeeIdAndIsSubmitted(extraWorkDetails.getDate(),
							employeeOfficialInfo.getEmployeeId(), true);
			if (!employeeTimesheetDetails.isEmpty() && employeeTimesheetDetails.get(0).getTimesheets() != null) {
				for (Timesheet timesheet : employeeTimesheetDetails.get(0).getTimesheets()) {
					if (timesheet.getDate().equals(extraWorkDetails.getDate())) {
						timesheet.setBreakDuration(extraWorkDetails.getBreakDuration());
						timesheet.setLoginTime(extraWorkDetails.getLoginTime());
						timesheet.setLogoutTime(extraWorkDetails.getLogoutTime());
						timesheet.setProjectTaskDetails(extraWorkDetails.getProjectTaskDetails());
					}
				}
			}
			employeeTimesheetDetailsRepository.saveAll(employeeTimesheetDetails);
			notificationServiceImpl.saveNotification(
					"Extra Work request for " + extraWorkDetails.getDate() + " is Approved",
					employeePersonalInfo.getEmployeeInfoId());

			if (employeePersonalInfo.getExpoToken() != null) {
				pushNotificationService.pushMessage("Flinko", "Extra Work request for " + extraWorkDetails.getDate() + " is Approved",
						employeePersonalInfo.getExpoToken());
			}
		}
		return true;
	}

	@Override
	public ExtraWorkDTO getExtraWorkById(Long extraWorkId) {
		ExtraWorkDTO extraWorkDTO = new ExtraWorkDTO();
		EmployeeExtraWorkDetails employeeExtraWorkDetails = employeeExtraWorkDetailsRepository.findById(extraWorkId)
				.orElseThrow(() -> new DataNotFoundException("Extra Work Details Not Found"));
		EmployeePersonalInfo employeePersonalInfo = employeeExtraWorkDetails.getEmployeePersonalInfo();
		if (employeePersonalInfo == null) {
			throw new DataNotFoundException("Employee Not Found");
		}
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		if (employeeOfficialInfo == null) {
			throw new DataNotFoundException("Employee Official Details Not Found");
		}
		extraWorkDTO.setEmployeeId(employeeOfficialInfo.getEmployeeId());
		extraWorkDTO.setBranch(employeeOfficialInfo.getCompanyBranchInfo() == null ? null
				: employeeOfficialInfo.getCompanyBranchInfo().getBranchName());
		extraWorkDTO.setDepartment(employeeOfficialInfo.getDepartment());
		extraWorkDTO.setDesignation(employeeOfficialInfo.getDesignation());
		extraWorkDTO.setFullName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		Set<String> keySet = employeeExtraWorkDetails.getProjectTaskDetails().keySet();
		List<Long> projectIds = keySet.stream().map(Long::parseLong).collect(Collectors.toList());
		List<ProjectDetails> projectDetails = projectDetailsRepository.findAllById(projectIds);
		List<EmployeeProjectTaskDetailsDTO> employeeProjectTaskDetailsDTOList = new ArrayList<>();
		for (Entry<String, List<String>> projectTaskDetails : employeeExtraWorkDetails.getProjectTaskDetails()
				.entrySet()) {

			ProjectDetails projectInfo = projectDetails.stream()
					.filter(project -> project.getProjectId() == Long.parseLong(projectTaskDetails.getKey()))
					.collect(Collectors.toList()).get(0);
			List<ProjectTaskDetails> taskList = (List<ProjectTaskDetails>) projectTaskDetailsRepository
					.findAllById(projectTaskDetails.getValue());
			List<EmployeeTaskDetailsDTO> employeeTaskDetailsDTOList = taskList.stream().map(task -> {
				return new EmployeeTaskDetailsDTO(task.getId(), task.getTaskName());
			}).collect(Collectors.toList());
			employeeProjectTaskDetailsDTOList.add(new EmployeeProjectTaskDetailsDTO(
					projectInfo.getProjectId().toString(), projectInfo.getProjectName(), employeeTaskDetailsDTOList));
		}

		extraWorkDTO.setProjectTaskDetails(employeeProjectTaskDetailsDTOList);
		BeanUtils.copyProperties(employeeExtraWorkDetails, extraWorkDTO);
		return extraWorkDTO;
	}

}
