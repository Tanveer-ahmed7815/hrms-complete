package com.te.flinko.service.hr.mongo;

import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.COMPANY_NOT_FOUND;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.HR;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.HR_APPROVAL_NOT_REQUIRED;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.PENDING;
import static com.te.flinko.common.admin.EmployeeReimbursementInfoConstants.RM;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.ADMIN;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.APPROVED;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.PERSONAL_DETAILS_DOES_NOT_EXIST;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.REJECTED;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.STATUS_DOES_NOT_EXIST;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.THE_GET_EMPLOYEE_TIMESHEET_DETAILS_METHOD_END;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.THE_UPDATE_EMPLOYEE_TIMESHEET_DETAILS_METHOD_END;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_APPROVED_SUCCESSFULLY;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_DOES_NOT_EXIST;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_DOES_NOT_EXIST_WITH_EMPLOYEE_ID;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.TIMESHEET_REJECTED_BY_ADMIN;
import static com.te.flinko.common.admin.mongo.EmployeeTimesheetDetailsConstants.UPDATE_EMPLOYEE_TIMESHEET;
import static com.te.flinko.common.admin.EmployeeLeaveDetailsConstants.LEVEL_OF_APPROVAL_NOT_FOUND;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.dto.admin.mongo.EmployeeTimeSheetDTO;
import com.te.flinko.dto.employee.mongo.EmployeeProjectTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.Timesheet;
import com.te.flinko.dto.employee.mongo.TimesheetDTO;
import com.te.flinko.entity.admin.LevelsOfApproval;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.EmployeeTimesheetDetails;
import com.te.flinko.entity.project.mongo.ProjectTaskDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.admin.LevelsOfApprovalRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeReportingInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeTimesheetDetailsRepository;
import com.te.flinko.repository.project.mongo.ProjectTaskDetailsRepository;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.service.notification.employee.PushNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Validated
@Slf4j
public class HRTimesheetApprovalServiceImpli implements HRTimesheetApprovalService {

	private final LevelsOfApprovalRepository levelsOfApprovalRepository;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final EmployeeTimesheetDetailsRepository employeeTimesheetDetailsRepository;

	private final EmployeeReportingInfoRepository employeeReportingInfoRepository;

	private final ProjectTaskDetailsRepository projectTaskDetailsRepository;

	private final InAppNotificationServiceImpl notificationServiceImpl;
	
	private final PushNotificationService pushNotificationService;
	
	private Optional<String> optional = Optional.of("optional");

	@Override
	public List<EmployeeTimeSheetDTO> getAllEmployeeTimesheetDetails(Long companyId, String status,
			Long employeeInfoId) {
		List<String> timesheetLevels = levelsOfApprovalRepository.findByCompanyInfoCompanyId(companyId)
				.map(LevelsOfApproval::getTimeSheet).orElseThrow(() -> new DataNotFoundException(LEVEL_OF_APPROVAL_NOT_FOUND));

		if (!timesheetLevels.contains(HR)) {
			throw new DataNotFoundException(HR_APPROVAL_NOT_REQUIRED);
		}

		List<String> employeeIdList = employeeReportingInfoRepository.findByReportingHREmployeeInfoId(employeeInfoId)
				.stream().filter(employee -> employee.getEmployeePersonalInfo().getEmployeeOfficialInfo() != null)
				.map(employee -> employee.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId())
				.collect(Collectors.toList());

		Optional<List<EmployeeTimesheetDetails>> timeSheetOptionalList = employeeTimesheetDetailsRepository
				.findByCompanyIdAndEmployeeIdInAndIsSubmitted(companyId, employeeIdList, true);

		List<EmployeeTimeSheetDTO> employeeTimeSheetDTOList = new ArrayList<>();

		if (timeSheetOptionalList.isPresent()) {
			List<EmployeeTimesheetDetails> timeSheetList = timeSheetOptionalList.get();
			timeSheetList = filterTimesheetBasedOnCondition(status, timeSheetList, timesheetLevels);

			for (EmployeeTimesheetDetails employeeTimesheetDetails : timeSheetList) {
				Optional<List<EmployeePersonalInfo>> employeePersonalInfoOptional = employeePersonalInfoRepository
						.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId,
								employeeTimesheetDetails.getEmployeeId());
				if (!employeePersonalInfoOptional.isEmpty()) {
					EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoOptional.get().get(0);
					employeeTimeSheetDTOList
							.add(getTimesheetDetails(employeePersonalInfo, status, employeeTimesheetDetails));
				}
			}
		}
		return employeeTimeSheetDTOList;
	}

	private EmployeeTimeSheetDTO getTimesheetDetails(EmployeePersonalInfo employeePersonalInfo, String status,
			EmployeeTimesheetDetails employeeTimesheetDetails) {
		String pendingAt = null;

		if (status.equalsIgnoreCase(PENDING)) {
			pendingAt = (employeeTimesheetDetails.getApprovedBy().keySet().contains(HR)) ? ADMIN : HR;
		}
		return EmployeeTimeSheetDTO.builder()
				.employeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
				.designation(employeePersonalInfo.getEmployeeOfficialInfo().getDesignation())
				.timesheetObjectId(employeeTimesheetDetails.getTimesheetObjectId())
				.department(employeePersonalInfo.getEmployeeOfficialInfo().getDepartment())
				.employeeId(employeeTimesheetDetails.getEmployeeId())
				.startDate(employeeTimesheetDetails.getTimesheets().get(0).getDate())
				.endDate(employeeTimesheetDetails.getTimesheets()
						.get(employeeTimesheetDetails.getTimesheets().size() - 1).getDate())
				.reason(employeeTimesheetDetails.getRejectionReason())
				.rejectedBy(employeeTimesheetDetails.getRejectionReason()).pendingAt(pendingAt)
				.isActionRequired(status.equalsIgnoreCase(PENDING)
						? !(employeeTimesheetDetails.getApprovedBy().keySet().contains(HR))
						: null)
				.build();
	}

	private List<EmployeeTimesheetDetails> filterTimesheetBasedOnCondition(String status,
			List<EmployeeTimesheetDetails> timeSheetList, List<String> levels) {
		if (status.equalsIgnoreCase(APPROVED)) {
			return timeSheetList.stream()
					.filter(timesheet -> timesheet.getApprovedBy().keySet().contains(levels.get(levels.size() - 1)))
					.collect(Collectors.toList());
		} else if (status.equalsIgnoreCase(PENDING)) {
			return timeSheetList.stream().filter(timesheet -> timesheet.getRejectedBy() == null
					&& !timesheet.getIsApproved() && timesheet.getApprovedBy().keySet().contains(RM))
					.collect(Collectors.toList());
		} else {
			return timeSheetList.stream().filter(timesheet -> timesheet.getRejectedBy() != null)
					.collect(Collectors.toList());
		}
	}

//new	
	@Override
	public EmployeeTimeSheetDTO getEmployeeTimesheetDetail(String timesheetObjectId, Long companyId) {

		EmployeeTimeSheetDTO timeSheetDto = employeeTimesheetDetailsRepository
				.findByTimesheetObjectIdAndCompanyId(timesheetObjectId, companyId)
				.map(x -> employeePersonalInfoRepository
						.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId, x.getEmployeeId())
						.map(yy -> {
							EmployeePersonalInfo y = yy.get(0);
							String pendingAt = null;

							if (!x.getIsApproved().booleanValue() && x.getRejectedBy() == null) {
								pendingAt = (x.getApprovedBy().keySet().contains(HR)) ? ADMIN : HR;
							}
							EmployeeOfficialInfo employeeOfficialInfo = y.getEmployeeOfficialInfo();
//							
							List<Timesheet> timesheets = x.getTimesheets();
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

										List<ProjectTaskDetails> employeeTasks = projectTaskDetailsRepository
												.findByAssignedEmployeeAndProjectIdAndCompanyId(
														y.getEmployeeOfficialInfo().getEmployeeId(), Long.parseLong(k),
														companyId);

										List<EmployeeProjectTaskDetailsDTO> collect2 = y.getAllocatedProjectList()
												.stream().filter(p -> p.getProjectId().toString().equals(k)).map(p -> {
													employeeProjectTaskDetailsDTO.setProjectId(k);
													employeeProjectTaskDetailsDTO.setProjectName(p.getProjectName());
													List<String> dayTaskDetails = timesheet.getProjectTaskDetails()
															.get(k);
													List<EmployeeTaskDetailsDTO> dailyTaskDetails = new ArrayList<>();
													if (!dayTaskDetails.isEmpty()) {
														dailyTaskDetails = dayTaskDetails.stream().map(d -> {
															EmployeeTaskDetailsDTO employeeTaskDetailsDTO = new EmployeeTaskDetailsDTO();
															employeeTaskDetailsDTO.setTaskId(d);

															List<String> nameList = employeeTasks.stream()
																	.filter(t -> t.getId().equals(d)).map(t -> {
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
							return EmployeeTimeSheetDTO.builder().timesheetId(x.getTimesheetId())
									.employeeId(x.getEmployeeId()).timesheetObjectId(x.getTimesheetObjectId())
									.department(employeeOfficialInfo.getDepartment())
									.designation(employeeOfficialInfo.getDesignation())
									.branch(employeeOfficialInfo.getCompanyBranchInfo().getBranchName())
									.employeeName(y.getFirstName() + " " + y.getLastName())
									.startDate(x.getTimesheets().get(0).getDate())
									.endDate(x.getTimesheets().get(x.getTimesheets().size() - 1).getDate())
//									.timesheets(x.getTimesheets())
									.timesheetDTO(newDTOList).reason(x.getRejectionReason())
									.rejectedBy(x.getRejectionReason())
									.isActionRequired((!x.getIsApproved().booleanValue() && x.getRejectedBy() == null)
											? !(x.getApprovedBy().keySet().contains(HR))
											: null)
									.pendingAt(pendingAt).build();
						}).orElseThrow(() -> {
							log.error(TIMESHEET_DOES_NOT_EXIST_WITH_EMPLOYEE_ID + x.getEmployeeId());
							return new DataNotFoundException(
									TIMESHEET_DOES_NOT_EXIST_WITH_EMPLOYEE_ID + x.getEmployeeId());
						}))
				.orElseThrow(() -> {
					log.error(TIMESHEET_DOES_NOT_EXIST);
					return new DataNotFoundException(TIMESHEET_DOES_NOT_EXIST);
				});
		log.info(THE_GET_EMPLOYEE_TIMESHEET_DETAILS_METHOD_END);
		return timeSheetDto;
	}

	@Override
	public String updateEmployeeTimesheetDetails(Long companyId, String timesheetObjectId, String employeeId,
			AdminApprovedRejectDto adminApprovedRejectDto) {

		List<String> timesheetLevels = levelsOfApprovalRepository.findByCompanyInfoCompanyId(companyId)
				.map(LevelsOfApproval::getTimeSheet).orElseThrow(() -> new DataNotFoundException(LEVEL_OF_APPROVAL_NOT_FOUND));

		String updateStatus = employeeTimesheetDetailsRepository
				.findByTimesheetObjectIdAndCompanyId(timesheetObjectId, companyId).filter(y -> !y.getIsApproved())
				.map(x -> employeePersonalInfoRepository
						.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId, x.getEmployeeId())
						.filter(z -> x.getRejectedBy() == null).map(y -> {
							EmployeeTimesheetDetails timesheetDetails = optional
									.filter(o -> adminApprovedRejectDto.getStatus().equals(REJECTED)).map(a -> {
										x.setRejectedBy(ADMIN);
										x.setRejectionReason(adminApprovedRejectDto.getReason());
										x.setIsApproved(false);
										List<Timesheet> timesheets = x.getTimesheets();
										if(timesheets!=null && !timesheets.isEmpty()) {
										notificationServiceImpl.saveNotification("Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Rejected by HR",x.getCreatedBy()
												);
										
										Optional<EmployeePersonalInfo> findById = employeePersonalInfoRepository.findById(x.getCreatedBy());
										
										if (findById.get().getExpoToken() != null) {
											pushNotificationService.pushMessage("Flinko", "Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Rejected by HR",
													findById.get().getExpoToken());
											}
										
										}
										return x;
									}).orElseGet(() -> optional
											.filter(d -> adminApprovedRejectDto.getStatus().equals(APPROVED)).map(p -> {
												Map<String, String> previousAprovedBy = (x.getApprovedBy().isEmpty())
														? new LinkedHashMap<>()
														: x.getApprovedBy();
												previousAprovedBy.put(HR, employeeId);
												x.setApprovedBy(previousAprovedBy);
												if (timesheetLevels.get(timesheetLevels.size() - 1)
														.equalsIgnoreCase(HR)) {
													x.setIsApproved(true);
												}
												List<Timesheet> timesheets = x.getTimesheets();
												if(timesheets!=null && !timesheets.isEmpty()) {
													notificationServiceImpl.saveNotification("Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Approved by HR",x.getCreatedBy()
															);
													Optional<EmployeePersonalInfo> findById = employeePersonalInfoRepository.findById(x.getCreatedBy());
													
													if (findById.get().getExpoToken() != null) {
														pushNotificationService.pushMessage("Flinko", "Timesheet request from " + timesheets.get(0).getDate() + " to "+ timesheets.get(timesheets.size()-1).getDate() + " is Approved by HR",
																findById.get().getExpoToken());
														}
													
													}
												return x;
											}).orElseThrow(() -> new DataNotFoundException(STATUS_DOES_NOT_EXIST)));
						 employeeTimesheetDetailsRepository.save(timesheetDetails);
							log.info(UPDATE_EMPLOYEE_TIMESHEET);
							return x.getIsApproved().booleanValue() ? TIMESHEET_APPROVED_SUCCESSFULLY
									: TIMESHEET_REJECTED_BY_ADMIN;
						}).orElseThrow(() -> {
							log.error(PERSONAL_DETAILS_DOES_NOT_EXIST);
							return new DataNotFoundException(PERSONAL_DETAILS_DOES_NOT_EXIST);
						}))
				.orElseThrow(() -> {
					log.error(TIMESHEET_DOES_NOT_EXIST);
					return new DataNotFoundException(TIMESHEET_DOES_NOT_EXIST);
				});
		log.info(THE_UPDATE_EMPLOYEE_TIMESHEET_DETAILS_METHOD_END + updateStatus);
		return updateStatus;
	}

}