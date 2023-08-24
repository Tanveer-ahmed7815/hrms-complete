package com.te.flinko.service.employee.mongo;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.employee.EmployeeCalanderDetailsDTO;
import com.te.flinko.dto.employee.EmployeeCalenderLeaveInfoDTO;
import com.te.flinko.dto.employee.mongo.EmployeeCalendarDTO;
import com.te.flinko.dto.employee.mongo.EmployeeProjectTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTaskDetailsDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTaskListDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTimesheetConfigurationDTO;
import com.te.flinko.dto.employee.mongo.EmployeeTimesheetDetailsDTO;
import com.te.flinko.dto.employee.mongo.Timesheet;
import com.te.flinko.dto.employee.mongo.TimesheetDTO;
import com.te.flinko.entity.admin.CompanyHolidayDetails;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyWorkWeekRule;
import com.te.flinko.entity.admin.WorkOffDetails;
import com.te.flinko.entity.employee.EmployeeLeaveApplied;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.EmployeeTimesheetDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.project.mongo.ProjectTaskDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyHolidayDetailsRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeLeaveAppliedRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeTimesheetDetailsRepository;
import com.te.flinko.repository.project.mongo.ProjectTaskDetailsRepository;

import io.swagger.v3.oas.models.security.SecurityScheme.In;

@Service
public class EmployeeTimeSheetServiceImpl implements EmployeeTimeSheetService {

	@Autowired
	EmployeeTimesheetDetailsRepository timeSheetRepository;

	@Autowired
	EmployeePersonalInfoRepository personalInfoRepository;

	@Autowired
	ProjectTaskDetailsRepository taskRepository;

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	@Autowired
	private EmployeeLeaveAppliedRepository employeeLeaveAppliedRepository;

	@Autowired
	private CompanyHolidayDetailsRepository companyHolidayDetailsRepository;

	@Override
	public List<EmployeeProjectTaskDetailsDTO> getProjectList(Long employeeInfoId, Long companyId) {

		ArrayList<EmployeeProjectTaskDetailsDTO> projectListDTO = new ArrayList<>();
		Optional<EmployeePersonalInfo> employeePersonalInfo = personalInfoRepository.findById(employeeInfoId);

		if (!employeePersonalInfo.isPresent()) {
			throw new DataNotFoundException("Employee Not Found");
		}

		EmployeePersonalInfo employeeInfo = employeePersonalInfo.get();

		if (employeeInfo.getEmployeeOfficialInfo() == null) {
			throw new DataNotFoundException("Employee OfficialInfo Not Found");
		}

		List<ProjectDetails> allotedProjectList = employeeInfo.getAllocatedProjectList();
		List<Long> projectIdList = allotedProjectList.stream().map(ProjectDetails::getProjectId)
				.collect(Collectors.toList());
		List<ProjectTaskDetails> projectTaskDetailsList = taskRepository
				.findByAssignedEmployeeAndProjectIdInAndCompanyId(
						employeeInfo.getEmployeeOfficialInfo().getEmployeeId(), projectIdList, companyId);
		Map<Long, List<ProjectTaskDetails>> groupedProjectTaskDetailsList = projectTaskDetailsList.stream()
				.collect(Collectors.groupingBy(ProjectTaskDetails::getProjectId));
		for (ProjectDetails projectDetails : allotedProjectList) {
			EmployeeProjectTaskDetailsDTO dto = new EmployeeProjectTaskDetailsDTO();
			dto.setProjectId(projectDetails.getProjectId().toString());
			dto.setProjectName(projectDetails.getProjectName());
			List<ProjectTaskDetails> taskList = groupedProjectTaskDetailsList.get(projectDetails.getProjectId());
			dto.setTaskDetails((taskList == null) ? new ArrayList<>() : taskList.stream().map(task -> {
				return new EmployeeTaskDetailsDTO(task.getId(), task.getTaskName());
			}).collect(Collectors.toList()));
			projectListDTO.add(dto);
		}
		return projectListDTO;

	}

	@Override
	public ProjectTaskDetails saveProjectTaskDetails(ProjectTaskDetails projectTaskDetails) {

		return taskRepository.save(projectTaskDetails);
	}

	@Override
	public List<EmployeeTaskListDTO> getTaskList(Long employeeInfoId, List<Long> projectIdList, Long companyId) {

		List<EmployeeTaskListDTO> taskListDTO = new ArrayList<>();

		EmployeePersonalInfo personalInfo = personalInfoRepository
				.findByEmployeeInfoIdAndCompanyInfoCompanyId(employeeInfoId, companyId);

		if (personalInfo == null)
			throw new DataNotFoundException("Employee Not Found");

		if (personalInfo.getEmployeeOfficialInfo() == null) {
			throw new DataNotFoundException("Employee OfficialInfo Not Found");
		}
		if (projectIdList.isEmpty())
			throw new DataNotFoundException("No project is selected");

		for (Long projectId : projectIdList) {
			List<ProjectTaskDetails> taskLists = taskRepository.findByAssignedEmployeeAndProjectIdAndCompanyId(
					personalInfo.getEmployeeOfficialInfo().getEmployeeId(), projectId, companyId);

			for (ProjectTaskDetails taskDetail : taskLists) {
				taskListDTO.add(new EmployeeTaskListDTO(taskDetail.getId(), taskDetail.getTaskName()));
			}
		}
		if (taskListDTO.isEmpty())
			throw new DataNotFoundException("Task Not Assigned");

		return taskListDTO;
	}

	@Override
	public EmployeeTimesheetDetailsDTO saveEmployeeTimesheetDetails(
			EmployeeTimesheetDetailsDTO employeeTimesheetDetailsDTO, Long employeeInfoId, Long companyId) {

		EmployeeTimesheetDetailsDTO employeeTimesheetDetailsDTO2 = new EmployeeTimesheetDetailsDTO();

		EmployeeTimesheetDetails employeeTimesheetDetails = new EmployeeTimesheetDetails();

		EmployeePersonalInfo employeePersonalInfo = personalInfoRepository
				.findByEmployeeInfoIdAndCompanyInfoCompanyId(employeeInfoId, companyId);
		if (employeePersonalInfo == null) {
			throw new DataNotFoundException("PersonalInfo Not Found");
		}
		if (employeePersonalInfo.getEmployeeOfficialInfo() == null) {
			throw new DataNotFoundException("OfficialInfo Not Found");
		}
		if (employeeTimesheetDetailsDTO.getId() != null) {

			employeeTimesheetDetails = timeSheetRepository
					.findByTimesheetObjectIdAndCompanyId(employeeTimesheetDetailsDTO.getId(), companyId)
					.orElseThrow(() -> new DataNotFoundException("Timesheet Not Found"));
			if (employeeTimesheetDetails.getIsSubmitted().equals(Boolean.TRUE)) {
				throw new DataNotFoundException("Timesheet Cannot be edited");
			}
		} else {
			employeeTimesheetDetails = new EmployeeTimesheetDetails();
		}
		List<Timesheet> dtoTimesheets = employeeTimesheetDetailsDTO.getTimesheets();
		for (int i = 0; i < dtoTimesheets.size(); i++) {
			Timesheet timesheet = dtoTimesheets.get(i);
			timesheet.setId(Integer.toString(i + 1));
		}
		BeanUtils.copyProperties(employeeTimesheetDetailsDTO, employeeTimesheetDetails);
		employeeTimesheetDetails.setIsApproved(Boolean.FALSE);
		employeeTimesheetDetails.setRejectedBy(null);
		employeeTimesheetDetails.setRejectionReason(null);
		employeeTimesheetDetails.setApprovedBy(new LinkedHashMap<String, String>());
		BeanUtils.copyProperties(timeSheetRepository.save(employeeTimesheetDetails), employeeTimesheetDetailsDTO2);
		employeeTimesheetDetailsDTO2.setId(employeeTimesheetDetails.getTimesheetObjectId());
		employeeTimesheetDetailsDTO2.setTimesheetId(employeeTimesheetDetails.getTimesheetId());
		return employeeTimesheetDetailsDTO2;

	}

	private EmployeeCalendarDTO getMonthDetails(CompanyInfo companyInfo, EmployeeOfficialInfo employeeOfficialInfo,
			Integer month, Integer year, Long employeeInfoId, EmployeeCalendarDTO employeeCalendarDTO) {
		CompanyWorkWeekRule companyWorkWeekRule = employeeOfficialInfo.getCompanyWorkWeekRule();
		List<WorkOffDetails> workOffDetailsList;
		Map<Integer, List<String>> weekOffName;
		if (companyWorkWeekRule == null) {
			List<CompanyWorkWeekRule> companyWorkWeekRuleList = companyInfo.getCompanyWorkWeekRuleList().stream()
					.filter(CompanyWorkWeekRule::getIsDefault).collect(Collectors.toList());
			if (!companyWorkWeekRuleList.isEmpty()) {
				companyWorkWeekRule = companyWorkWeekRuleList.get(0);
			} else {
				throw new DataNotFoundException("Work week details not found");
			}
		}

		workOffDetailsList = companyWorkWeekRule.getWorkOffDetailsList();
		weekOffName = workOffDetailsList.stream()
				.collect(Collectors.toMap(WorkOffDetails::getWeekNumber, WorkOffDetails::getFullDayWorkOff));

		List<LocalDate> weekOffDetails = new ArrayList<>();
		List<LocalDate> leaveDetails = new ArrayList<>();
		LocalDate startDate = LocalDate.of(year, month, 1);
		int maxDay = startDate.getMonth().length(startDate.isLeapYear());
		LocalDate endDate = LocalDate.of(year, month, maxDay);

		List<LocalDate> datesUntil = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
		for (LocalDate localDate : datesUntil) {
			List<String> weekOff = weekOffName.get(Integer.valueOf(localDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH)));
			if (weekOff != null && weekOff.contains(Arrays
					.stream(localDate.getDayOfWeek().toString().substring(0, 3).split("\\s+"))
					.map(name -> name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase())
					.collect(Collectors.joining(" ")))) {
				weekOffDetails.add(localDate);
			}
		}

		List<LocalDate> holidayList = companyHolidayDetailsRepository
				.findByHolidayDateBetweenAndCompanyInfoCompanyId(startDate, endDate, companyInfo.getCompanyId())
				.orElse(new ArrayList<>()).stream().map(CompanyHolidayDetails::getHolidayDate)
				.collect(Collectors.toList());

		List<EmployeeLeaveApplied> employeeLeaveAppliedList = employeeLeaveAppliedRepository
				.findByStatusIgnoreCaseAndEmployeePersonalInfoEmployeeInfoIdAndStartDateBetween("Approved",
						employeeInfoId, startDate, endDate);
		employeeLeaveAppliedList.stream().forEach(leave -> {
			if (leave.getStartDate().equals(leave.getEndDate())) {
				if (!weekOffDetails.contains(leave.getStartDate())) {
					leaveDetails.add(leave.getStartDate());
				}
			} else if (leave.getStartDate().isBefore(leave.getEndDate())) {
				leave.getStartDate().datesUntil(leave.getEndDate().plusDays(1)).forEach(leaveDate -> {
					if (!weekOffDetails.contains(leaveDate) && !holidayList.contains(leaveDate)) {
						leaveDetails.add(leaveDate);
					}
				});
			}
		});

		employeeCalendarDTO.setHolidayDetails(holidayList);
		employeeCalendarDTO.setWeekOffDetails(weekOffDetails);
		employeeCalendarDTO.setLeaveDetails(leaveDetails);
		return employeeCalendarDTO;
	}

	@Override
	public EmployeeCalendarDTO getTimesheetDetail(EmployeeTimesheetConfigurationDTO employeeTimesheetConfigurationDTO) {

		EmployeeCalendarDTO employeeCalendarDTO = new EmployeeCalendarDTO();

		CompanyInfo companyInfo = companyInfoRepository.findById(employeeTimesheetConfigurationDTO.getCompanyId())
				.orElseThrow(() -> new DataNotFoundException("Company Not Found"));

		EmployeePersonalInfo employeePersonalInfo = personalInfoRepository.findByEmployeeInfoIdAndCompanyInfoCompanyId(
				employeeTimesheetConfigurationDTO.getEmployeeInfoId(),
				employeeTimesheetConfigurationDTO.getCompanyId());

		if (employeePersonalInfo == null) {
			throw new DataNotFoundException("PersonalInfo Not found");
		}

		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();

		if (employeeOfficialInfo == null) {
			throw new DataNotFoundException("Official Info Not Found");
		}

		getMonthDetails(companyInfo, employeeOfficialInfo, employeeTimesheetConfigurationDTO.getMonth(),
				employeeTimesheetConfigurationDTO.getYear(), employeeTimesheetConfigurationDTO.getEmployeeInfoId(),
				employeeCalendarDTO);

		List<EmployeeTimesheetDetails> employeeTimesheetDetails;

		if (Boolean.FALSE.equals(employeeTimesheetConfigurationDTO.getIsMonthlyTimesheet())) {
			employeeTimesheetDetails = timeSheetRepository.findByCompanyIdAndEmployeeIdAndYearAndMonth(
					employeeTimesheetConfigurationDTO.getCompanyId(), employeeOfficialInfo.getEmployeeId(),
					employeeTimesheetConfigurationDTO.getYear(), employeeTimesheetConfigurationDTO.getMonth());
		} else {
			employeeTimesheetDetails = timeSheetRepository.findByCompanyIdAndEmployeeIdAndYear(
					employeeTimesheetConfigurationDTO.getCompanyId(), employeeOfficialInfo.getEmployeeId(),
					employeeTimesheetConfigurationDTO.getYear());
		}

		List<EmployeeTimesheetDetailsDTO> employeeTimesheetDetailsDTOList = new ArrayList<>();

		for (EmployeeTimesheetDetails employeeTimesheet : employeeTimesheetDetails) {
			EmployeeTimesheetDetailsDTO employeeTimesheetDetailsDTO = new EmployeeTimesheetDetailsDTO();
			List<ProjectDetails> allocatedProjectList = employeePersonalInfo.getAllocatedProjectList();

			List<TimesheetDTO> newDTOList = new ArrayList<>();

			List<Timesheet> timesheets = employeeTimesheet.getTimesheets();

			for (Timesheet timesheet : timesheets) {

				List<EmployeeProjectTaskDetailsDTO> res = new ArrayList<>();

				TimesheetDTO newDTO = new TimesheetDTO();

				List<EmployeeProjectTaskDetailsDTO> projectTaskList = new ArrayList();

				Set<String> keySet = timesheet.getProjectTaskDetails().keySet();
				if (!timesheet.getProjectTaskDetails().isEmpty()) {

					keySet.stream().map(k -> {
						EmployeeProjectTaskDetailsDTO employeeProjectTaskDetailsDTO = new EmployeeProjectTaskDetailsDTO();

						List<ProjectTaskDetails> employeeTasks = taskRepository
								.findByAssignedEmployeeAndProjectIdAndCompanyId(employeeOfficialInfo.getEmployeeId(),
										Long.parseLong(k), employeeTimesheetConfigurationDTO.getCompanyId());

						List<EmployeeProjectTaskDetailsDTO> collect2 = new ArrayList<>();

						collect2 = allocatedProjectList.stream().filter(p -> p.getProjectId().toString().equals(k))
								.map(p -> {
									employeeProjectTaskDetailsDTO.setProjectId(k);
									employeeProjectTaskDetailsDTO.setProjectName(p.getProjectName());
									List<String> dayTaskDetails = timesheet.getProjectTaskDetails().get(k);

									List<EmployeeTaskDetailsDTO> dailyTaskDetails = new ArrayList();
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
						return projectTaskList;
					}).collect(Collectors.toList());
				}
				BeanUtils.copyProperties(timesheet, newDTO);
				newDTO.setProjectTaskDetails(res);
				newDTOList.add(newDTO);
			}
			BeanUtils.copyProperties(employeeTimesheet, employeeTimesheetDetailsDTO);
			employeeTimesheetDetailsDTO.setId(employeeTimesheet.getTimesheetObjectId());
			employeeTimesheetDetailsDTO.setEmployeeId(employeeTimesheet.getEmployeeId());
			employeeTimesheetDetailsDTO.setTimesheets(null);
			employeeTimesheetDetailsDTO.setTimesheetDTO(newDTOList);
			employeeTimesheetDetailsDTOList.add(employeeTimesheetDetailsDTO);
		}
		employeeCalendarDTO.setTimesheetDetails(employeeTimesheetDetailsDTOList);
		return employeeCalendarDTO;
	}

	@Override
	public String deleteEmployeeTimeSheet(Long employeeInfoId, String timesheetObjectId, Long companyId) {

		EmployeePersonalInfo employeePersonalInfo = personalInfoRepository
				.findByEmployeeInfoIdAndCompanyInfoCompanyId(employeeInfoId, companyId);

		if (employeePersonalInfo == null) {
			throw new DataNotFoundException("PersonalInfo No Found");
		}
		EmployeeTimesheetDetails employeeTimesheetDetails = timeSheetRepository
				.findByTimesheetObjectIdAndCompanyId(timesheetObjectId, companyId)
				.orElseThrow(() -> new DataNotFoundException("Timesheet Not Found"));
		if (employeeTimesheetDetails == null) {
			throw new DataNotFoundException("Employee Timesheet Details Not Found");
		}

		timeSheetRepository.deleteById(timesheetObjectId);

		return "Data Deleted Successfully";
	}

}
