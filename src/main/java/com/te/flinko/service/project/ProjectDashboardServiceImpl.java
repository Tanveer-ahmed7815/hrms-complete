package com.te.flinko.service.project;

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

import com.te.flinko.dto.DashboardDTO;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.project.ProjectCompleteDetailsDTO;
import com.te.flinko.dto.project.ProjectDetailsBasicDTO;
import com.te.flinko.dto.project.mongo.ProjectListDTO;
import com.te.flinko.dto.project.mongo.SubMilestone;
import com.te.flinko.entity.admin.CompanyHolidayDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.project.ProjectEstimationDetails;
import com.te.flinko.entity.project.mongo.ProjectMilestoneDeliverables;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyHolidayDetailsRepository;
import com.te.flinko.repository.project.ProjectDetailsRepository;
import com.te.flinko.repository.project.mongo.MilestoneRepository;

@Service
public class ProjectDashboardServiceImpl implements ProjectDashboardService {

	@Autowired
	private ProjectDetailsRepository projectDetailsRepository;

	@Autowired
	private MilestoneRepository milestoneRepository;

	@Autowired
	private CompanyHolidayDetailsRepository companyHolidayDetailsRepository;

	private static final String CARDS = "Cards";

	private static final String GRAPH = "Graph";

	@Override
	public DashboardResponseDTO getProjectDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<ProjectDetails> projectList = projectDetailsRepository.findByCompanyInfoCompanyId(companyId);

		List<ProjectDetails> projectListForGraph = projectList.stream().filter(project -> project.getCreatedDate()
				.toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
				&& project.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getProjectCounts(projectList, CARDS))
				.graphValues(getProjectCounts(projectListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getProjectCounts(List<ProjectDetails> projectList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long notEstimatedCount = 0l;
		Long estimationApprovalPending = 0l;
		Long estimationRejectedCount = 0l;
		Long completedCount = 0l;
		Long onGoingCount = 0l;
		for (ProjectDetails project : projectList) {
			if (project.getProjectEstimationDetails() == null) {
				notEstimatedCount = notEstimatedCount + 1;
			} else if ("Pending".equalsIgnoreCase(project.getProjectEstimationDetails().getStatus())) {
				estimationApprovalPending = estimationApprovalPending + 1;
			} else if ("Rejected".equalsIgnoreCase(project.getProjectEstimationDetails().getStatus())) {
				estimationRejectedCount = estimationRejectedCount + 1;
			} else if (LocalDate.now().isAfter(project.getProjectEstimationDetails().getEndDate())) {
				completedCount = completedCount + 1;
			} else {
				onGoingCount = onGoingCount + 1;
			}
		}
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type("Total").count(Long.valueOf(projectList.size())).build());
		}
		values.add(DashboardDTO.builder().type("Not Estimated").count(notEstimatedCount).build());
		values.add(DashboardDTO.builder().type("Approval Pending").count(estimationApprovalPending).build());
		values.add(DashboardDTO.builder().type("Estimation Rejected").count(estimationRejectedCount).build());
		values.add(DashboardDTO.builder().type("Completed").count(completedCount).build());
		values.add(DashboardDTO.builder().type("On-Going").count(onGoingCount).build());
		return values;
	}

	@Override
	public List<ProjectListDTO> getAllProjectNames(Long companyId) {
		List<ProjectDetails> projectList = projectDetailsRepository.findByCompanyInfoCompanyId(companyId);
		List<ProjectListDTO> projectListDTO = new ArrayList<>();
		for (ProjectDetails project : projectList) {
			projectListDTO.add(new ProjectListDTO(project.getProjectId(), project.getProjectName(), companyId));
		}
		return projectListDTO;
	}

	@Override
	public ProjectCompleteDetailsDTO getProjectDetailsById(Long projectId, Long companyId) {
		ProjectDetails projectDetails = projectDetailsRepository.findById(projectId)
				.orElseThrow(() -> new DataNotFoundException("No project present with Id " + projectId));
		List<ProjectMilestoneDeliverables> milestoneDetails = milestoneRepository.findByProjectId(projectId);
		ProjectEstimationDetails estimationDetails = projectDetails.getProjectEstimationDetails();
		ProjectCompleteDetailsDTO detailsDTO = new ProjectCompleteDetailsDTO();
		BeanUtils.copyProperties(projectDetails, detailsDTO);
		detailsDTO.setClientName(projectDetails.getCompanyClientInfo().getClientName());
		LocalDate nextDeliverable = getNextDeliverable(milestoneDetails);
		detailsDTO.setNextDeliverable(nextDeliverable);
		double amountPaid = milestoneDetails.stream().filter(deliverable -> deliverable.getAmountReceived() != null)
				.mapToDouble(ProjectMilestoneDeliverables::getAmountReceived).sum();
		detailsDTO.setAmountReceived(amountPaid);
		detailsDTO.setNoOfEmpWorking(Long.valueOf(projectDetails.getEmployeePersonalInfoList().size()));
		if (nextDeliverable != null && LocalDate.now().isAfter(nextDeliverable)) {
			detailsDTO.setStatus("Slippage");
			detailsDTO.setDelayedBy(Days.daysBetween(org.joda.time.LocalDate.parse(nextDeliverable.toString()),
					org.joda.time.LocalDate.parse(LocalDate.now().toString())).getDays());
		} else {
			detailsDTO.setStatus("As per schedule");
		}
		if (estimationDetails != null) {
			detailsDTO.setAmountPending(estimationDetails.getTotalAmountToBeReceived().doubleValue() - amountPaid);
			Period between = Period.between(estimationDetails.getStartDate(), estimationDetails.getEndDate());
			int days = between.getDays();
			int months = between.getMonths();
			int years = between.getYears();
			Integer workingDays = days + (months * 30) + (365 * years);
			detailsDTO.setWorkingDays(workingDays);
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

	@Override
	public List<ProjectDetailsBasicDTO> getProjectDetailsByStatus(Long companyId, String type) {
		List<ProjectDetails> projectList = projectDetailsRepository.findByCompanyInfoCompanyId(companyId);
		switch (type.toUpperCase()) {
		case "TOTAL":
			return getProjectDTO(projectList);
		case "NOT ESTIMATED":
			return getProjectDTO(projectList.stream().filter(project -> project.getProjectEstimationDetails() == null)
					.collect(Collectors.toList()));
		case "APPROVAL PENDING":
			return getProjectDTO(projectList.stream()
					.filter(project -> project.getProjectEstimationDetails() != null
							&& "Pending".equalsIgnoreCase(project.getProjectEstimationDetails().getStatus()))
					.collect(Collectors.toList()));
		case "ESTIMATION REJECTED":
			return getProjectDTO(projectList.stream()
					.filter(project -> project.getProjectEstimationDetails() != null
							&& "Rejected".equalsIgnoreCase(project.getProjectEstimationDetails().getStatus()))
					.collect(Collectors.toList()));
		case "COMPLETED":
			return getProjectDTO(projectList.stream()
					.filter(project -> project.getProjectEstimationDetails() != null
							&& "Approved".equalsIgnoreCase(project.getProjectEstimationDetails().getStatus())
							&& LocalDate.now().isAfter(project.getProjectEstimationDetails().getEndDate()))
					.collect(Collectors.toList()));
		case "ON-GOING":
			return getProjectDTO(projectList.stream()
					.filter(project -> project.getProjectEstimationDetails() != null
							&& "Approved".equalsIgnoreCase(project.getProjectEstimationDetails().getStatus())
							&& !(LocalDate.now().isAfter(project.getProjectEstimationDetails().getEndDate())))
					.collect(Collectors.toList()));
		default:
			throw new DataNotFoundException("Type Not Found");
		}
	}

	public List<ProjectDetailsBasicDTO> getProjectDTO(List<ProjectDetails> projectList) {
		return projectList.stream().map(project -> {
			ProjectDetailsBasicDTO projectDTO = new ProjectDetailsBasicDTO();
			BeanUtils.copyProperties(project, projectDTO);
			projectDTO.setClientName(
					project.getCompanyClientInfo() != null ? project.getCompanyClientInfo().getClientName() : null);
			projectDTO.setProjectManager(project.getProjectManager() != null
					? project.getProjectManager().getFirstName() + " " + project.getProjectManager().getLastName()
					: null);
			projectDTO.setReportingManager(project.getReportingManager() != null
					? project.getReportingManager().getFirstName() + " " + project.getReportingManager().getLastName()
					: null);
			return projectDTO;
		}).collect(Collectors.toList());

	}

}
