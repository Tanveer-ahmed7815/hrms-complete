package com.te.flinko.service.reportingmanager.mongo;

import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.RM;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.RM_APPROVAL_NOT_REQUIRED;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.COMPANY_NOT_FOUND;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.HR;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.PENDING;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.ADMIN;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.APPROVED;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.PERSONAL_DETAILS_DOES_NOT_EXIST;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.REJECTED;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.STATUS_DOES_NOT_EXIST;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_APPROVED_SUCCESSFULLY;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_DOES_NOT_EXIST;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_REJECTED_BY_RM;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.dto.admin.mongo.EmployeeTimeSheetDTO;
import com.te.flinko.dto.employee.mongo.EmployeeProjectTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.Timesheet;
import com.te.flinko.dto.employee.mongo.TimesheetDTO;
import com.te.flinko.dto.reportingmanager.EmployeeTimesheetDetailsApprovalDTO;
import com.te.flinko.entity.admin.LevelsOfApproval;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReportingInfo;
import com.te.flinko.entity.employee.mongo.EmployeeTimesheetDetails;
import com.te.flinko.entity.project.mongo.ProjectTaskDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.admin.LevelsOfApprovalRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeReportingInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeTimesheetDetailsRepository;
import com.te.flinko.repository.project.mongo.ProjectTaskDetailsRepository;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.service.notification.employee.PushNotificationService;

@Service
public class ReportingManagerTimesheetApprovalServiceImpl implements ReportingManagerTimesheetApprovalService {

	@Autowired
	LevelsOfApprovalRepository levelsOfApproval;

	@Autowired
	EmployeeReportingInfoRepository reportingRepository;

	@Autowired
	EmployeeTimesheetDetailsRepository timesheetRepository;

	@Autowired
	EmployeePersonalInfoRepository personalRepository;

	@Autowired
	ProjectTaskDetailsRepository taskRepository;
	
	@Autowired
	InAppNotificationServiceImpl notificationServiceImpl;
	
	@Autowired
	PushNotificationService pushNotificationService;

	private Optional<String> optional = Optional.of("optional");

	private List<EmployeeTimesheetDetails> filterTimesheetBasedOnCondition(String status,
			List<EmployeeTimesheetDetails> timeSheetList, List<String> levels) {
		if (status.equalsIgnoreCase(APPROVED)) {

			return timeSheetList.stream().filter(timesheet -> timesheet.getIsApproved().equals(Boolean.TRUE))
					.collect(Collectors.toList());
		} else if (status.equalsIgnoreCase(PENDING)) {

			return timeSheetList.stream()
					.filter(timesheet -> timesheet.getRejectedBy() == null && !timesheet.getIsApproved())
					.collect(Collectors.toList());
		} else {
			return timeSheetList.stream().filter(timesheet -> timesheet.getRejectedBy() != null)
					.collect(Collectors.toList());
		}
	}

	@Override
	public List<EmployeeTimeSheetDTO> getEmployeeTimesheetList(Long companyId, String status, Long employeeInfoId) {

		List<String> approvalLevel = levelsOfApproval.findByCompanyInfoCompanyId(companyId)
				.map(LevelsOfApproval::getTimeSheet).orElseThrow(() -> new DataNotFoundException(COMPANY_NOT_FOUND));

		if (!approvalLevel.contains(RM)) {
			throw new DataNotFoundException(RM_APPROVAL_NOT_REQUIRED);
		}

		List<String> employeeIdList = reportingRepository.findByReportingManagerEmployeeInfoId(employeeInfoId).stream()
				.filter(employee -> employee.getEmployeePersonalInfo().getEmployeeOfficialInfo() != null)
				.map(employee -> employee.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId())
				.collect(Collectors.toList());
		Optional<List<EmployeeTimesheetDetails>> optional = timesheetRepository
				.findByCompanyIdAndEmployeeIdInAndIsSubmitted(companyId, employeeIdList,true);

		List<EmployeeTimeSheetDTO> employeeTimeSheetDTOList = new ArrayList<>();

		if (optional.isPresent()) {

			List<EmployeeTimesheetDetails> timesheetList = optional.get();
			timesheetList = filterTimesheetBasedOnCondition(status, timesheetList, approvalLevel);
			for (EmployeeTimesheetDetails employeeTimesheetDetails : timesheetList) {

				Optional<List<EmployeePersonalInfo>> employeePersonalInfoOptional = personalRepository
						.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId,
								employeeTimesheetDetails.getEmployeeId());

				if (!employeePersonalInfoOptional.isEmpty()) {
					EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoOptional.get().get(0);
					employeeTimeSheetDTOList.add(
							getTimesheetDetails(employeePersonalInfo, status, employeeTimesheetDetails, approvalLevel));
				}
			}
		}
		return employeeTimeSheetDTOList;
	}

	private EmployeeTimeSheetDTO getTimesheetDetails(EmployeePersonalInfo employeePersonalInfo, String status,
			EmployeeTimesheetDetails employeeTimesheetDetails, List<String> approvalLevel) {

		String pendingAt = null;

		if (approvalLevel.size() == 1 || employeeTimesheetDetails.getApprovedBy() == null) {
			pendingAt = RM;
		} else {
			pendingAt = (employeeTimesheetDetails.getApprovedBy().keySet().contains(RM)
					&& employeeTimesheetDetails.getApprovedBy().keySet().contains(HR)) ? ADMIN
							: (employeeTimesheetDetails.getApprovedBy().keySet().contains(RM) ? approvalLevel.get(1)
									: RM);
		}
		List<EmployeeReportingInfo> employeeInfoList = employeePersonalInfo.getEmployeeInfoList();
		EmployeePersonalInfo reportingManagerDetails = null;
		if (!employeeInfoList.isEmpty()) {
			reportingManagerDetails = employeeInfoList.get(employeeInfoList.size() - 1).getReportingManager();
		}
		String reportingManager = (reportingManagerDetails == null) ? null
				: reportingManagerDetails.getFirstName() + " " + reportingManagerDetails.getLastName();

		Boolean isActionRequired = Boolean.FALSE;
		if (employeeTimesheetDetails.getApprovedBy() == null) {
			isActionRequired = Boolean.TRUE;
		} else {
			isActionRequired = status.equalsIgnoreCase(PENDING)
					? !(employeeTimesheetDetails.getApprovedBy().keySet().contains(RM))
					: null;
		}

		return EmployeeTimeSheetDTO.builder().timesheetObjectId(employeeTimesheetDetails.getTimesheetObjectId())
				.employeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
				.designation(employeePersonalInfo.getEmployeeOfficialInfo().getDesignation())
				.department(employeePersonalInfo.getEmployeeOfficialInfo().getDepartment())
				.employeeId(employeePersonalInfo.getEmployeeOfficialInfo().getEmployeeId())
				.startDate(employeeTimesheetDetails.getTimesheets().get(0).getDate())
				.endDate(employeeTimesheetDetails.getTimesheets()
						.get(employeeTimesheetDetails.getTimesheets().size() - 1).getDate())
				.reason(employeeTimesheetDetails.getRejectionReason())
				.rejectedBy(employeeTimesheetDetails.getRejectedBy()).pendingAt(pendingAt)
				.isActionRequired(isActionRequired).reportingManagerName(reportingManager).build();

	}

//New
	@Override
	public EmployeeTimesheetDetailsApprovalDTO getEmployeetimesheetDetails(String timesheetObjectId, Long companyId) {

		List<String> approvalLevel = levelsOfApproval.findByCompanyInfoCompanyId(companyId)
				.map(LevelsOfApproval::getTimeSheet).orElseThrow(() -> new DataNotFoundException("Company Not Found"));

		Optional<EmployeeTimesheetDetails> optionalEmployeeTimesheetDetail = timesheetRepository
				.findByTimesheetObjectIdAndCompanyId(timesheetObjectId, companyId);

		if (optionalEmployeeTimesheetDetail.isEmpty()) {
			throw new DataNotFoundException("Data Not Found");
		}

		List<EmployeePersonalInfo> list = personalRepository
				.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId,
						optionalEmployeeTimesheetDetail.get().getEmployeeId())
				.get();
		if(list.isEmpty()) {
			throw new DataNotFoundException("Personal Info Not Found");
		}
		EmployeePersonalInfo employeePersonalInfo = list.get(0);

		EmployeeTimesheetDetails timesheetDetails = optionalEmployeeTimesheetDetail.get();
		EmployeeTimesheetDetailsApprovalDTO employeeTimesheetDetailsDTO = new EmployeeTimesheetDetailsApprovalDTO();

		String pendingAt = null;
		if (approvalLevel.size() == 1 || timesheetDetails.getApprovedBy() == null) {
			pendingAt = RM;
		} else {
			pendingAt = (timesheetDetails.getApprovedBy().keySet().contains(RM)
					&& timesheetDetails.getApprovedBy().keySet().contains(HR)) ? ADMIN
							: (timesheetDetails.getApprovedBy().keySet().contains(RM) ? approvalLevel.get(1) : RM);
		}
		List<EmployeeReportingInfo> employeeInfoList = employeePersonalInfo.getEmployeeInfoList();
		EmployeePersonalInfo reportingManagerDetails = null;
		if (!employeeInfoList.isEmpty()) {
			reportingManagerDetails = employeeInfoList.get(employeeInfoList.size() - 1).getReportingManager();
		}
		String reportingManager = (reportingManagerDetails == null) ? null
				: reportingManagerDetails.getFirstName() + " " + reportingManagerDetails.getLastName();

		List<Timesheet> timesheets = timesheetDetails.getTimesheets();

		List<TimesheetDTO> newDTOList = new ArrayList<>();

		for (Timesheet timesheet : timesheets) {

			List<EmployeeProjectTaskDetailsDTO> res = new ArrayList<>();			
			TimesheetDTO newDTO = new TimesheetDTO();
			List<EmployeeProjectTaskDetailsDTO> projectTaskList = new ArrayList();

			Set<String> keySet = timesheet.getProjectTaskDetails().keySet();

			List<String> projectList = new ArrayList<>(keySet);

			if (!timesheet.getProjectTaskDetails().isEmpty()) {
				projectList.stream().map(k -> {
					EmployeeProjectTaskDetailsDTO employeeProjectTaskDetailsDTO = new EmployeeProjectTaskDetailsDTO();

					List<ProjectTaskDetails> employeeTasks = taskRepository
							.findByAssignedEmployeeAndProjectIdAndCompanyId(
									employeePersonalInfo.getEmployeeOfficialInfo().getEmployeeId(), Long.parseLong(k),
									companyId);

					List<EmployeeProjectTaskDetailsDTO> collect2 = employeePersonalInfo.getAllocatedProjectList()
							.stream().filter(p -> p.getProjectId().toString().equals(k)).map(p -> {
								employeeProjectTaskDetailsDTO.setProjectId(k);
								employeeProjectTaskDetailsDTO.setProjectName(p.getProjectName());
								List<String> dayTaskDetails = timesheet.getProjectTaskDetails().get(k);

								List<EmployeeTaskDetailsDTO> dailyTaskDetails = new ArrayList<>();
								if (!dayTaskDetails.isEmpty()) {
									dailyTaskDetails = dayTaskDetails.stream().map(d -> {
										EmployeeTaskDetailsDTO employeeTaskDetailsDTO = new EmployeeTaskDetailsDTO();
										employeeTaskDetailsDTO.setTaskId(d);

										List<String> nameList = employeeTasks.stream().filter(t -> t.getId().equals(d))
												.map(t -> {
													return t.getTaskName();
												}).collect(Collectors.toList());
										employeeTaskDetailsDTO.setTaskName(nameList.get(0));
										return employeeTaskDetailsDTO;
									}).collect(Collectors.toList());

									employeeProjectTaskDetailsDTO.setTaskDetails(dailyTaskDetails);
								} else {
									employeeProjectTaskDetailsDTO.setTaskDetails(dailyTaskDetails);
								}
								return employeeProjectTaskDetailsDTO;
							}).collect(Collectors.toList());
					res.addAll(collect2);
					projectTaskList.add(employeeProjectTaskDetailsDTO);
					return projectTaskList;
				}).collect(Collectors.toList());
			}
			BeanUtils.copyProperties(timesheet, newDTO);
			newDTO.setProjectTaskDetails(res);
			newDTOList.add(newDTO);
		}
		employeeTimesheetDetailsDTO.setId(timesheetDetails.getTimesheetObjectId());
		employeeTimesheetDetailsDTO.setTimesheetId(timesheetDetails.getTimesheetId());
		employeeTimesheetDetailsDTO.setEmployeeId(timesheetDetails.getEmployeeId());
		employeeTimesheetDetailsDTO
				.setBranch(employeePersonalInfo.getEmployeeOfficialInfo().getCompanyBranchInfo().getBranchName());
		employeeTimesheetDetailsDTO.setDepartment(employeePersonalInfo.getEmployeeOfficialInfo().getDepartment());
		employeeTimesheetDetailsDTO.setDesignation(employeePersonalInfo.getEmployeeOfficialInfo().getDesignation());
		employeeTimesheetDetailsDTO
				.setFullName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		employeeTimesheetDetailsDTO.setStartDate(timesheetDetails.getTimesheets().get(0).getDate());
		int size = timesheetDetails.getTimesheets().size();
		employeeTimesheetDetailsDTO.setEndDate(timesheetDetails.getTimesheets().get(size - 1).getDate());

		if (timesheetDetails.getApprovedBy() != null) {
			employeeTimesheetDetailsDTO.setIsActionRequired(timesheetDetails.getIsApproved().equals(Boolean.FALSE)
					? !(timesheetDetails.getApprovedBy().keySet().contains(RM))
					: null);
		} else {
			employeeTimesheetDetailsDTO.setIsActionRequired(Boolean.TRUE);
		}
		employeeTimesheetDetailsDTO.setTimesheetDTO(newDTOList);
		employeeTimesheetDetailsDTO.setIsApproved(timesheetDetails.getIsApproved());
		employeeTimesheetDetailsDTO.setPendingAt(pendingAt);
		employeeTimesheetDetailsDTO.setRejectedBy(timesheetDetails.getRejectedBy());
		employeeTimesheetDetailsDTO.setRejectionReason(timesheetDetails.getRejectionReason());
		employeeTimesheetDetailsDTO.setReportingManager(reportingManager);

		return employeeTimesheetDetailsDTO;

	}

	@Override
	public String addResponseToTimesheet(String timesheetObjectId, Long companyId, String employeeId,
			AdminApprovedRejectDto adminApprovedRejectDto) {

		List<String> timesheetApprovalLevel = levelsOfApproval.findByCompanyInfoCompanyId(companyId)
				.map(LevelsOfApproval::getTimeSheet).orElseThrow(() -> new DataNotFoundException(COMPANY_NOT_FOUND));

		if (!(timesheetApprovalLevel.contains(RM))) {
			throw new DataNotFoundException(RM_APPROVAL_NOT_REQUIRED);
		}

		String updateStatus = timesheetRepository.findByTimesheetObjectIdAndCompanyId(timesheetObjectId, companyId)
				.filter(y -> !y.getIsApproved())
				.map(x -> personalRepository
						.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId, x.getEmployeeId())
						.filter(z -> x.getRejectedBy() == null).map(y -> {
							EmployeeTimesheetDetails employeeTimesheetDetails = optional
									.filter(r -> adminApprovedRejectDto.getStatus().equalsIgnoreCase(REJECTED))
									.map(a -> {
										x.setRejectedBy(RM);
										x.setRejectionReason(adminApprovedRejectDto.getReason());
										x.setIsApproved(false);
										List<Timesheet> timesheets = x.getTimesheets();
										if(timesheets!=null && !timesheets.isEmpty()) {
										notificationServiceImpl.saveNotification("Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Rejected by Reporting Manager",x.getCreatedBy()
												);
										
										Optional<EmployeePersonalInfo> findById = personalRepository.findById(x.getCreatedBy());
										
										if (findById.get().getExpoToken() != null) {
											pushNotificationService.pushMessage("Flinko", "Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Rejected by Reporting Manager",
													findById.get().getExpoToken());
											}
										
										}
										return x;
									})
									.orElseGet(() -> optional
											.filter(a -> adminApprovedRejectDto.getStatus().equalsIgnoreCase(APPROVED))
											.map(b -> {
												Map<String, String> approvedBy = new LinkedHashMap<>();
												approvedBy.put(RM, employeeId);
												if (timesheetApprovalLevel.size() == 1
														&& timesheetApprovalLevel.contains(RM)) {
													x.setIsApproved(true);
												}
												x.setApprovedBy(approvedBy);
												List<Timesheet> timesheets = x.getTimesheets();
												if(timesheets!=null && !timesheets.isEmpty()) {
													notificationServiceImpl.saveNotification("Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Approved by Reporting Manager",x.getCreatedBy()
															);
													
													Optional<EmployeePersonalInfo> findById = personalRepository.findById(x.getCreatedBy());
													
													if (findById.get().getExpoToken() != null) {
														pushNotificationService.pushMessage("Flinko", "Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Approved by Reporting Manager",
																findById.get().getExpoToken());
														}
													
													}
												return x;
											}).orElseThrow(() -> new DataNotFoundException(STATUS_DOES_NOT_EXIST)));
							timesheetRepository.save(employeeTimesheetDetails);
							return x.getIsApproved().booleanValue() ? TIMESHEET_APPROVED_SUCCESSFULLY
									: TIMESHEET_REJECTED_BY_RM;
						}).orElseThrow(() -> {
							return new DataNotFoundException(PERSONAL_DETAILS_DOES_NOT_EXIST);
						})

				).orElseThrow(() -> {
					return new DataNotFoundException(TIMESHEET_DOES_NOT_EXIST);
				});
		return updateStatus;
	}

}
