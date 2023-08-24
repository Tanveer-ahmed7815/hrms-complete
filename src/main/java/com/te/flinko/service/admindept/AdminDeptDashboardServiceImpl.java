package com.te.flinko.service.admindept;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.DashboardDTO;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.admindept.GetOtherStockGroupItemDto;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;
import com.te.flinko.entity.admindept.CompanyStockGroupItems;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAdminDeptTickets;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.admindept.CompanyStockGroupItemsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAdminDeptTicketsRepo;

@Service
public class AdminDeptDashboardServiceImpl implements AdminDeptDashboardService {

	@Autowired
	private CompanyAdminDeptTicketsRepo companyAdminDeptTicketsRepo;

	@Autowired
	private CompanyStockGroupItemsRepository companyStockGroupItemsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private static final String CARDS = "Cards";

	private static final String GRAPH = "Graph";

	private static final String RESOLVED = "Resolved";

	private static final String TOTAL_CAPS = "TOTAL";

	private static final String TYPE_NOT_FOUND = "Type Not Found";

	@Override
	public DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyAdminDeptTickets> ticketList = companyAdminDeptTicketsRepo.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(dashboardRequestDTO.getFilterValue())) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyAdminDeptTickets> ticketListForCards = ticketList.stream()
				.filter(ticket -> (ticket.getCreatedDate().toLocalDate().isAfter(startOfTheWeek.minusDays(1)))
						&& (ticket.getCreatedDate().toLocalDate().isBefore(endOfTheWeek.plusDays(1)))
						&& (ticket.getTicketHistroys() != null) && (!ticket.getTicketHistroys().isEmpty()))
				.collect(Collectors.toList());

		List<CompanyAdminDeptTickets> ticketListForGraph = ticketList.stream().filter(
				ticket -> ticket.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& ticket.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getTicketCounts(ticketListForCards, CARDS))
				.graphValues(getTicketCounts(ticketListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getTicketCounts(List<CompanyAdminDeptTickets> ticketList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long solvedCount = ticketList.stream().filter(ticket -> RESOLVED
				.equalsIgnoreCase(ticket.getTicketHistroys().get(ticket.getTicketHistroys().size() - 1).getStatus()))
				.count();
		Long totalCount = Long.valueOf(ticketList.size());
		values.add(DashboardDTO.builder().type("Pending").count(Math.subtractExact(totalCount, solvedCount)).build());
		values.add(DashboardDTO.builder().type("Solved").count(solvedCount).build());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type("Total").count(totalCount).build());
		}
		return values;
	}

	@Override
	public DashboardResponseDTO getStockGroupDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyStockGroupItems> stockGroupItemList = companyStockGroupItemsRepository
				.findByCompanyInfoCompanyId(companyId);

		List<CompanyStockGroupItems> stockGroupItemListForGraph = stockGroupItemList.stream().filter(
				ticket -> ticket.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& ticket.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getStockGroupCounts(stockGroupItemList, CARDS))
				.graphValues(getStockGroupCounts(stockGroupItemListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getStockGroupCounts(List<CompanyStockGroupItems> stockGroupItemList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long freeCount = 0l;
		Long inUseCount = 0l;
		Long workingCount = 0l;
		Long notWorkingCount = 0l;
		Long total = 0l;
		for (CompanyStockGroupItems stockGroupItem : stockGroupItemList) {
			freeCount = freeCount + stockGroupItem.getFree();
			inUseCount = inUseCount + stockGroupItem.getInUse();
			workingCount = workingCount + stockGroupItem.getWorking();
			Long itemTotal = Long.sum(stockGroupItem.getFree(), stockGroupItem.getInUse());
			notWorkingCount = notWorkingCount + Math.subtractExact(itemTotal, stockGroupItem.getWorking());
			total = total + itemTotal;
		}
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type("Total").count(total).build());
		}
		values.add(DashboardDTO.builder().type("Available").count(freeCount).build());
		values.add(DashboardDTO.builder().type("Allocated").count(inUseCount).build());
		values.add(DashboardDTO.builder().type("Working").count(workingCount).build());
		values.add(DashboardDTO.builder().type("Not working").count(notWorkingCount).build());
		return values;
	}

	@Override
	public List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue) {
		List<CompanyAdminDeptTickets> ticketList = companyAdminDeptTicketsRepo.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(filterValue)) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyAdminDeptTickets> ticketListForCards = ticketList.stream()
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

	public List<HRTicketsBasicDTO> getTicketDTO(List<CompanyAdminDeptTickets> ticketList, Long companyId) {
		List<EmployeePersonalInfo> employeeDetails = employeePersonalInfoRepository
				.findByCompanyInfoCompanyId(companyId);
		return ticketList.stream().map(ticket -> {
			HRTicketsBasicDTO ticketDTO = new HRTicketsBasicDTO();
			ticketDTO.setTicketObjectId(ticket.getObjectTicketId());
			ticketDTO.setCategory(ticket.getCategory());
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
	public List<GetOtherStockGroupItemDto> getStockItemDetailsByStatus(Long companyId, String type) {
		List<CompanyStockGroupItems> stockGroupItemList = companyStockGroupItemsRepository
				.findByCompanyInfoCompanyId(companyId);

		switch (type.toUpperCase()) {
		case TOTAL_CAPS:
			return getStockItemDTO(stockGroupItemList);
		case "AVAILABLE":
			return getStockItemDTO(
					stockGroupItemList.stream().filter(stock -> stock.getFree() > 0).collect(Collectors.toList()));
		case "ALLOCATED":
			return getStockItemDTO(
					stockGroupItemList.stream().filter(stock -> stock.getInUse() > 0).collect(Collectors.toList()));
		case "WORKING":
			return getStockItemDTO(
					stockGroupItemList.stream().filter(stock -> stock.getWorking() > 0).collect(Collectors.toList()));
		case "NOT WORKING":
			return getStockItemDTO(stockGroupItemList.stream()
					.filter(stock -> stock.getQuantity() != stock.getWorking()).collect(Collectors.toList()));

		default:
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}
	}

	public List<GetOtherStockGroupItemDto> getStockItemDTO(List<CompanyStockGroupItems> stockGroupItemList) {
		return stockGroupItemList.stream().map(stock -> {
			GetOtherStockGroupItemDto stockItemDTO = new GetOtherStockGroupItemDto();
			BeanUtils.copyProperties(stock, stockItemDTO);
			stockItemDTO.setStockGroupName(
					stock.getCompanyStockGroup() != null ? stock.getCompanyStockGroup().getStockGroupName() : null);
			return stockItemDTO;
		}).collect(Collectors.toList());

	}

}
