package com.te.flinko.service.hr;

import static com.te.flinko.common.hr.HrConstants.OVERALL_FEEDBACK;
import static com.te.flinko.common.hr.HrConstants.REJECTED;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.DashboardDTO;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.hr.CandidateListDTO;
import com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO;
import com.te.flinko.dto.hr.EventManagementDisplayEventDTO;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.helpandsupport.mongo.CompanyHrTickets;
import com.te.flinko.entity.hr.CandidateInfo;
import com.te.flinko.entity.hr.CandidateInterviewInfo;
import com.te.flinko.entity.hr.CompanyEventDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyHrTicketsRepository;
import com.te.flinko.repository.hr.CandidateInfoRepository;
import com.te.flinko.repository.hr.CompanyEventDetailsRepository;

@Service
public class HRDashboardServiceImpl implements HRDashboardService {

	@Autowired
	private CompanyHrTicketsRepository companyHrTicketsRepository;

	@Autowired
	private CandidateInfoRepository candidateInfoRepository;

	@Autowired
	private CompanyEventDetailsRepository companyEventDetailsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private static final String TOTAL = "Total";

	private static final String CARDS = "Cards";

	private static final String GRAPH = "Graph";

	private static final String TYPE_NOT_FOUND = "Type Not Found";

	private static final String TERMINATED = "Terminated";

	private static final String ABSCONDED = "Absconded";

	private static final String RESIGNED = "Resigned";

	private static final String RESOLVED = "Resolved";

	private static final String TOTAL_CAPS = "TOTAL";

	@Override
	public DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyHrTickets> ticketList = companyHrTicketsRepository.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(dashboardRequestDTO.getFilterValue())) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyHrTickets> ticketListForCards = ticketList.stream()
				.filter(ticket -> (ticket.getCreatedDate().toLocalDate().isAfter(startOfTheWeek.minusDays(1)))
						&& (ticket.getCreatedDate().toLocalDate().isBefore(endOfTheWeek.plusDays(1)))
						&& (ticket.getTicketHistroys() != null) && (!ticket.getTicketHistroys().isEmpty()))
				.collect(Collectors.toList());

		List<CompanyHrTickets> ticketListForGraph = ticketList.stream().filter(
				ticket -> ticket.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& ticket.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getTicketCounts(ticketListForCards, CARDS))
				.graphValues(getTicketCounts(ticketListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getTicketCounts(List<CompanyHrTickets> ticketList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long solvedCount = ticketList.stream().filter(ticket -> RESOLVED
				.equalsIgnoreCase(ticket.getTicketHistroys().get(ticket.getTicketHistroys().size() - 1).getStatus()))
				.count();
		Long totalCount = Long.valueOf(ticketList.size());
		values.add(DashboardDTO.builder().type("Pending").count(Math.subtractExact(totalCount, solvedCount)).build());
		values.add(DashboardDTO.builder().type("Solved").count(solvedCount).build());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type(TOTAL).count(totalCount).build());
		}
		return values;
	}

	@Override
	public List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue) {
		List<CompanyHrTickets> ticketList = companyHrTicketsRepository.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(filterValue)) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyHrTickets> ticketListForCards = ticketList.stream()
				.filter(ticket -> (ticket.getCreatedDate().toLocalDate().isAfter(startOfTheWeek.minusDays(1)))
						&& (ticket.getCreatedDate().toLocalDate().isBefore(endOfTheWeek.plusDays(1)))
						&& (ticket.getTicketHistroys() != null) && (!ticket.getTicketHistroys().isEmpty()))
				.collect(Collectors.toList());
		switch (type.toUpperCase()) {
		case TOTAL_CAPS:
			return getTicketDTO(ticketListForCards, companyId);
		case "SOLVED":
			return getTicketDTO(ticketListForCards.stream()
					.filter(ticket -> RESOLVED.equalsIgnoreCase(
							ticket.getTicketHistroys().get(ticket.getTicketHistroys().size() - 1).getStatus()))
					.collect(Collectors.toList()), companyId);
		case "PENDING":
			return getTicketDTO(ticketListForCards.stream()
					.filter(ticket -> !(RESOLVED.equalsIgnoreCase(
							ticket.getTicketHistroys().get(ticket.getTicketHistroys().size() - 1).getStatus())))
					.collect(Collectors.toList()), companyId);

		default:
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}
	}

	public List<HRTicketsBasicDTO> getTicketDTO(List<CompanyHrTickets> ticketList, Long companyId) {
		List<EmployeePersonalInfo> employeeDetails = employeePersonalInfoRepository
				.findByCompanyInfoCompanyId(companyId);
		return ticketList.stream().map(ticket -> {
			HRTicketsBasicDTO ticketDTO = new HRTicketsBasicDTO();
			BeanUtils.copyProperties(ticket, ticketDTO);
			ticketDTO.setHrTicketId(ticket.getTicketId());
			ticketDTO.setRaisedDate(ticket.getCreatedDate().toLocalDate());
			List<TicketHistroy> ticketHistroys = ticket.getTicketHistroys();
			ticketDTO.setStatus((ticketHistroys != null && !ticketHistroys.isEmpty())
					? ticketHistroys.get(ticketHistroys.size() - 1).getStatus()
					: null);
			List<EmployeePersonalInfo> owner = employeeDetails.stream()
					.filter(employee -> employee.getEmployeeInfoId().equals(ticket.getCreatedBy()))
					.collect(Collectors.toList());
			if (!owner.isEmpty()) {
				ticketDTO.setTicketOwner(owner.get(0).getFirstName() + " " + owner.get(0).getLastName());
			}
			return ticketDTO;
		}).collect(Collectors.toList());

	}

	@Override
	public DashboardResponseDTO getCandidateDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CandidateInfo> candidateList = candidateInfoRepository.findByCompanyInfoCompanyId(companyId);
		List<CandidateInfo> candidateListForGraph = candidateList.stream().filter(candidate -> candidate
				.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
				&& candidate.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getCandidateCounts(candidateList, CARDS))
				.graphValues(getCandidateCounts(candidateListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getCandidateCounts(List<CandidateInfo> candidateList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long selectedCount = candidateList.stream().filter(candidate -> candidate.getIsSelected()
				&& (candidate.getIsDocumentVerified() != null && candidate.getIsDocumentVerified())).count();
		Long rejectedCount = candidateList.stream().filter(candidate -> {
			List<CandidateInterviewInfo> candidateInterviewInfoList = candidate.getCandidateInterviewInfoList();
			if (candidate.getIsDocumentVerified() != null && !candidate.getIsDocumentVerified()) {
				return true;
			} else if (!candidateInterviewInfoList.isEmpty()) {
				Map<String, String> feedback = candidateInterviewInfoList.get(candidateInterviewInfoList.size() - 1)
						.getFeedback();
				if (feedback != null && REJECTED.equalsIgnoreCase(feedback.get(OVERALL_FEEDBACK))) {
					return true;
				}
			}
			return false;
		}).count();
		Long totalCount = Long.valueOf(candidateList.size());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type(TOTAL).count(totalCount).build());
		}
		values.add(DashboardDTO.builder().type("In Progress")
				.count(Math.subtractExact(totalCount, (Long.sum(selectedCount, rejectedCount)))).build());
		values.add(DashboardDTO.builder().type("Selected").count(selectedCount).build());
		values.add(DashboardDTO.builder().type("Rejected").count(rejectedCount).build());
		return values;
	}

	@Override
	public DashboardResponseDTO getEventDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyEventDetails> eventList = companyEventDetailsRepository.findByCompanyInfoCompanyId(companyId);

		List<CompanyEventDetails> eventListForGraph = eventList.stream()
				.filter(event -> event.getEventDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& event.getEventDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getEventCounts(eventList, CARDS))
				.graphValues(getEventCounts(eventListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getEventCounts(List<CompanyEventDetails> eventList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long completedCount = eventList.stream().filter(
				event -> LocalDateTime.of(event.getEventDate(), event.getEndTime()).isBefore(LocalDateTime.now()))
				.count();
		Long totalCount = Long.valueOf(eventList.size());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type(TOTAL).count(totalCount).build());
		}
		values.add(
				DashboardDTO.builder().type("Pending").count(Math.subtractExact(totalCount, completedCount)).build());
		values.add(DashboardDTO.builder().type("Completed").count(completedCount).build());
		return values;
	}

	@Override
	public DashboardResponseDTO getEmployeeDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<EmployeePersonalInfo> employeeList = employeePersonalInfoRepository.findByCompanyInfoCompanyId(companyId);

		employeeList = employeeList.stream()
				.filter(employee -> employee.getStatus() == null || employee.getStatus().get("rejectedBy") == null)
				.collect(Collectors.toList());

		List<EmployeePersonalInfo> employeeListForGraph = employeeList.stream().filter(employee -> employee
				.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
				&& employee.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getEmployeeCounts(employeeList, CARDS))
				.graphValues(getEmployeeCounts(employeeListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getEmployeeCounts(List<EmployeePersonalInfo> employeeList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long totalCount = Long.valueOf(employeeList.size());

		employeeList = employeeList.stream()
				.filter(employee -> employee.getStatus() != null && employee.getStatus().get("approvedBy") != null)
				.collect(Collectors.toList());

		Long activeCount = employeeList.stream()
				.filter(employee -> employee.getIsActive() != null && employee.getIsActive()).count();
		Long terminatedCount = employeeList.stream()
				.filter(employee -> employee.getIsActive() != null && !employee.getIsActive()
						&& employee.getStatus() != null && employee.getStatus().get(TERMINATED) != null)
				.count();
		Long abscondedCount = employeeList.stream()
				.filter(employee -> employee.getIsActive() != null && !employee.getIsActive()
						&& employee.getStatus() != null && employee.getStatus().get(ABSCONDED) != null)
				.count();
		Long resignedCount = employeeList.stream()
				.filter(employee -> employee.getIsActive() != null && !employee.getIsActive()
						&& employee.getStatus() != null && employee.getStatus().get(RESIGNED) != null)
				.count();
		Long currentCount = Long.valueOf(employeeList.size());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type(TOTAL).count(totalCount).build());
		}
		values.add(DashboardDTO.builder().type("Active").count(activeCount).build());
		values.add(
				DashboardDTO.builder().type("In Progress").count(Math.subtractExact(totalCount, currentCount)).build());
		values.add(DashboardDTO.builder().type(TERMINATED).count(terminatedCount).build());
		values.add(DashboardDTO.builder().type(ABSCONDED).count(abscondedCount).build());
		values.add(DashboardDTO.builder().type(RESIGNED).count(resignedCount).build());
		return values;
	}

	@Override
	public List<CandidateListDTO> getCandidateDetailsByStatus(Long companyId, String type) {
		List<CandidateInfo> candidateList = candidateInfoRepository.findByCompanyInfoCompanyId(companyId);
		switch (type.toUpperCase()) {
		case TOTAL_CAPS:
			return getCandidateDTO(candidateList);
		case "SELECTED":
			return getCandidateDTO(getSelectedCandidates(candidateList));
		case "REJECTED":
			return getCandidateDTO(getRejectedCandidates(candidateList));
		case "IN PROGRESS":
			candidateList.removeAll(getSelectedCandidates(candidateList));
			candidateList.removeAll(getRejectedCandidates(candidateList));
			return getCandidateDTO(candidateList);
		default:
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}
	}

	List<CandidateInfo> getSelectedCandidates(List<CandidateInfo> candidateList) {
		return candidateList.stream()
				.filter(candidate -> candidate.getIsSelected()
						&& (candidate.getIsDocumentVerified() != null && candidate.getIsDocumentVerified()))
				.collect(Collectors.toList());
	}

	List<CandidateInfo> getRejectedCandidates(List<CandidateInfo> candidateList) {
		return candidateList.stream().filter(candidate -> {
			List<CandidateInterviewInfo> candidateInterviewInfoList = candidate.getCandidateInterviewInfoList();
			if (candidate.getIsDocumentVerified() != null && !candidate.getIsDocumentVerified()) {
				return true;
			} else if (!candidateInterviewInfoList.isEmpty()) {
				Map<String, String> feedback = candidateInterviewInfoList.get(candidateInterviewInfoList.size() - 1)
						.getFeedback();
				if (feedback != null && REJECTED.equalsIgnoreCase(feedback.get(OVERALL_FEEDBACK))) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
	}

	public List<CandidateListDTO> getCandidateDTO(List<CandidateInfo> candidateInfoList) {
		return candidateInfoList.stream().map(candidate -> {
			CandidateListDTO candidateDetailsDTO = new CandidateListDTO();
			BeanUtils.copyProperties(candidate, candidateDetailsDTO);
			candidateDetailsDTO.setFullName(candidate.getFirstName() + " " + candidate.getLastName());
			return candidateDetailsDTO;
		}).collect(Collectors.toList());

	}

	@Override
	public List<EventManagementDisplayEventDTO> getEventDetailsByStatus(Long companyId, String type) {
		List<CompanyEventDetails> eventList = companyEventDetailsRepository.findByCompanyInfoCompanyId(companyId);
		switch (type.toUpperCase()) {
		case TOTAL_CAPS:
			return getEventDTO(eventList);
		case "COMPLETED":
			return getEventDTO(eventList.stream().filter(
					event -> LocalDateTime.of(event.getEventDate(), event.getEndTime()).isBefore(LocalDateTime.now()))
					.collect(Collectors.toList()));
		case "PENDING":
			return getEventDTO(
					eventList.stream().filter(event -> !(LocalDateTime.of(event.getEventDate(), event.getEndTime())
							.isBefore(LocalDateTime.now()))).collect(Collectors.toList()));
		default:
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}
	}

	public List<EventManagementDisplayEventDTO> getEventDTO(List<CompanyEventDetails> eventList) {
		return eventList.stream().map(event -> {
			EventManagementDisplayEventDTO eventDTO = new EventManagementDisplayEventDTO();
			BeanUtils.copyProperties(event, eventDTO);
			return eventDTO;
		}).collect(Collectors.toList());

	}

	@Override
	public List<EmployeeDisplayDetailsDTO> getEmployeeDetailsByStatus(Long companyId, String type) {
		List<EmployeeDisplayDetailsDTO> employeeList = employeePersonalInfoRepository.getEmployeeDetails(companyId);

		employeeList = employeeList.stream()
				.filter(employee -> employee.getStatus() == null || employee.getStatus().get("rejectedBy") == null)
				.collect(Collectors.toList());

		List<EmployeeDisplayDetailsDTO> currentEmployees = employeeList.stream()
				.filter(employee -> employee.getStatus() != null && employee.getStatus().get("approvedBy") != null)
				.collect(Collectors.toList());

		switch (type.toUpperCase()) {

		case TOTAL_CAPS:
			return employeeList;

		case "IN PROGRESS":
			employeeList.removeAll(currentEmployees);
			return employeeList;

		case "ACTIVE":
			return currentEmployees.stream()
					.filter(employee -> employee.getIsActive() != null && employee.getIsActive())
					.collect(Collectors.toList());

		case "TERMINATED":
			return currentEmployees.stream()
					.filter(employee -> employee.getIsActive() != null && !employee.getIsActive()
							&& employee.getStatus() != null && employee.getStatus().get(TERMINATED) != null)
					.collect(Collectors.toList());

		case "ABSCONDED":
			return currentEmployees.stream()
					.filter(employee -> employee.getIsActive() != null && !employee.getIsActive()
							&& employee.getStatus() != null && employee.getStatus().get(ABSCONDED) != null)
					.collect(Collectors.toList());

		case "RESIGNED":
			return currentEmployees.stream()
					.filter(employee -> employee.getIsActive() != null && !employee.getIsActive()
							&& employee.getStatus() != null && employee.getStatus().get(RESIGNED) != null)
					.collect(Collectors.toList());

		default:
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}

	}

}
