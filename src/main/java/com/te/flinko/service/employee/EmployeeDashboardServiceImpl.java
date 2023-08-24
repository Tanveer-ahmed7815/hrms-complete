package com.te.flinko.service.employee;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joda.time.Days;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.employee.EmployeeProjectDetailsDTO;
import com.te.flinko.dto.employee.EmployeeProjectListDTO;
import com.te.flinko.dto.project.ProjectCompleteDetailsDTO;
import com.te.flinko.dto.project.mongo.SubMilestone;
import com.te.flinko.entity.admin.CompanyHolidayDetails;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.project.ProjectEstimationDetails;
import com.te.flinko.entity.project.mongo.ProjectMilestoneDeliverables;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyHolidayDetailsRepository;
import com.te.flinko.repository.project.ProjectDetailsRepository;
import com.te.flinko.repository.project.mongo.MilestoneRepository;

@Service
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {

	@Autowired
	private ProjectDetailsRepository projectDetailsRepository;

	@Autowired
	private CompanyHolidayDetailsRepository companyHolidayDetailsRepository;

	@Autowired
	private MilestoneRepository milestoneRepository;

	@Override
	public List<EmployeeProjectListDTO> getAllProjectNames(Long employeeInfoId) {
		List<ProjectDetails> projectList = projectDetailsRepository
				.findByEmployeePersonalInfoListEmployeeInfoId(employeeInfoId);
		List<EmployeeProjectListDTO> projectListDTO = new ArrayList<>();
		for (ProjectDetails project : projectList) {
			projectListDTO.add(new EmployeeProjectListDTO(project.getProjectId(), project.getProjectName()));
		}
		return projectListDTO;
	}

	@Override
	public EmployeeProjectDetailsDTO getProjectDetailsById(Long projectId, Long companyId) {
		ProjectDetails projectDetails = projectDetailsRepository.findById(projectId)
				.orElseThrow(() -> new DataNotFoundException("No project present with Id " + projectId));
		List<ProjectMilestoneDeliverables> milestoneDetails = milestoneRepository.findByProjectId(projectId);
		EmployeePersonalInfo projectManager = projectDetails.getProjectManager();
		ProjectEstimationDetails estimationDetails = projectDetails.getProjectEstimationDetails();
		EmployeeProjectDetailsDTO detailsDTO = new EmployeeProjectDetailsDTO();
		BeanUtils.copyProperties(projectDetails, detailsDTO);
		LocalDate nextDeliverable = getNextDeliverable(milestoneDetails);
		detailsDTO.setNextDeliverable(nextDeliverable);
		if (nextDeliverable != null && LocalDate.now().isAfter(nextDeliverable)) {
			detailsDTO.setDelayedBy(Days.daysBetween(org.joda.time.LocalDate.parse(nextDeliverable.toString()),
					org.joda.time.LocalDate.parse(LocalDate.now().toString())).getDays());
		}
		detailsDTO.setProjectManagerName(
				projectManager == null ? null : projectManager.getFirstName() + " " + projectManager.getLastName());
		if (estimationDetails != null) {
			detailsDTO
					.setProjectDuration(Days
							.daysBetween(org.joda.time.LocalDate.parse(estimationDetails.getStartDate().toString()),
									org.joda.time.LocalDate.parse(estimationDetails.getEndDate().toString()))
							.getDays());
			detailsDTO
					.setAvailableDays(estimationDetails.getEndDate().isAfter(LocalDate.now())
							? Days.daysBetween(org.joda.time.LocalDate.parse(LocalDate.now().toString()),
									org.joda.time.LocalDate.parse(estimationDetails.getEndDate().toString())).getDays()
							: 0);
			Optional<List<CompanyHolidayDetails>> holidayDetails = companyHolidayDetailsRepository
					.findByHolidayDateBetweenAndCompanyInfoCompanyId(estimationDetails.getStartDate(),
							estimationDetails.getEndDate(), companyId);

			detailsDTO.setHolidays(holidayDetails.isPresent() ? Long.valueOf(holidayDetails.get().size()) : 0l);
			detailsDTO.setDaysLaps(detailsDTO.getProjectDuration() - detailsDTO.getAvailableDays());
			detailsDTO.setEndDate(estimationDetails.getEndDate());
		}
		return detailsDTO;
	}

	private LocalDate getNextDeliverable(List<ProjectMilestoneDeliverables> milestoneDetails) {
		List<ProjectMilestoneDeliverables> filteredMilestones = milestoneDetails.stream()
				.filter(milestone -> milestone.getDeliveredDate() == null).collect(Collectors.toList());
		List<LocalDate> nextDeliveryDates = new ArrayList<>();
		for (ProjectMilestoneDeliverables projectMilestoneDeliverables : filteredMilestones) {
			if (projectMilestoneDeliverables.getSubMilestones() != null
					&& !projectMilestoneDeliverables.getSubMilestones().isEmpty()) {
				List<SubMilestone> fileredSubMilestones = projectMilestoneDeliverables.getSubMilestones().stream()
						.filter(subMilestone -> subMilestone.getDeliveredDate() == null).collect(Collectors.toList());
				for (SubMilestone subMilestone : fileredSubMilestones) {
					nextDeliveryDates.add(subMilestone.getDueDate());
				}
			}
			nextDeliveryDates.add(projectMilestoneDeliverables.getDueDate());
		}
		Collections.sort(nextDeliveryDates);
		return nextDeliveryDates.isEmpty() ? null : nextDeliveryDates.get(0);
	}

}
