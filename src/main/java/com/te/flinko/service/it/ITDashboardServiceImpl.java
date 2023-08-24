package com.te.flinko.service.it;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.constants.admindept.AdminDeptConstants;
import com.te.flinko.dto.DashboardDTO;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.admindept.CompanyHardwareItemsDTO;
import com.te.flinko.dto.admindept.CompanyPCLaptopDTO;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;
import com.te.flinko.entity.account.PurchaseOrderItems;
import com.te.flinko.entity.account.SalesOrderItems;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAdminDeptTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyItTickets;
import com.te.flinko.entity.it.CompanyHardwareItems;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.admin.CompanyNotExistException;
import com.te.flinko.exception.admindept.PurchaseOrderItemNotFoundException;
import com.te.flinko.exception.admindept.SalesOrderItemNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admindept.CompanyHardwareItemsRepository;
import com.te.flinko.repository.admindept.CompanyItTicketsRepository;
import com.te.flinko.repository.admindept.CompanyPCLaptopRepository;
import com.te.flinko.repository.admindept.PurchaseOrderItemsRepository;
import com.te.flinko.repository.admindept.SalesOrderItemsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.service.admindept.CompanyPCLaptopServiceImpl;

@Service
public class ITDashboardServiceImpl implements ITDashboardService {

	@Autowired
	private CompanyItTicketsRepository companyItTicketsRepository;

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	@Autowired
	private CompanyPCLaptopRepository companyPCLaptopRepository;

	@Autowired
	private CompanyHardwareItemsRepository companyHardwareItemsRepository;

	@Autowired
	private CompanyPCLaptopServiceImpl companyPCLaptopServiceImpl;

	@Autowired
	private SalesOrderItemsRepository salesOrderItemsRepository;

	@Autowired
	private PurchaseOrderItemsRepository purchaseOrderItemsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private static final String RESOLVED = "Resolved";

	private static final String TOTAL_CAPS = "TOTAL";

	private static final String TYPE_NOT_FOUND = "Type Not Found";

	private static final String AVAILABLE = "Available";

	private static final String ALLOCATED = "Allocated";

	private static final String WORKING = "Working";

	private static final String NOT_WORKING = "Not Working";

	private static final String TOTAL = "Total";

	private static final String CARDS = "Cards";

	private static final String GRAPH = "Graph";

	@Override
	public DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyItTickets> ticketList = companyItTicketsRepository.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(dashboardRequestDTO.getFilterValue())) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyItTickets> ticketListForCards = ticketList.stream()
				.filter(ticket -> (ticket.getCreatedDate().toLocalDate().isAfter(startOfTheWeek.minusDays(1)))
						&& (ticket.getCreatedDate().toLocalDate().isBefore(endOfTheWeek.plusDays(1)))
						&& (ticket.getTicketHistroys() != null) && (!ticket.getTicketHistroys().isEmpty()))
				.collect(Collectors.toList());

		List<CompanyItTickets> ticketListForGraph = ticketList.stream().filter(
				ticket -> ticket.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& ticket.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getTicketCounts(ticketListForCards, CARDS))
				.graphValues(getTicketCounts(ticketListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getTicketCounts(List<CompanyItTickets> ticketList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long solvedCount = ticketList.stream().filter(ticket -> "Resolved"
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
	public List<CompanyPCLaptopDTO> typeBasedPCLaptopDetails(Long companyId, String type) {

		List<CompanyPcLaptopDetails> companyPcLaptopDetailsList;
		List<CompanyPcLaptopDetails> companyPcLaptopDetailsList1 = companyPCLaptopRepository
				.findByCompanyInfoCompanyId(companyId);

		if (type.equalsIgnoreCase(TOTAL)) {
			companyPcLaptopDetailsList = companyPCLaptopRepository.findByCompanyInfoCompanyId(companyId);

		} else if (type.equalsIgnoreCase(ALLOCATED)) {
			companyPcLaptopDetailsList = companyPcLaptopDetailsList1.stream()
					.filter(x -> x.getEmployeePersonalInfo() != null).collect(Collectors.toList());

		} else if (type.equalsIgnoreCase(AVAILABLE)) {
			companyPcLaptopDetailsList = companyPcLaptopDetailsList1.stream()
					.filter(x -> x.getEmployeePersonalInfo() == null).collect(Collectors.toList());

		} else {

			boolean y = false;

			if (type.equalsIgnoreCase(WORKING)) {
				y = true;
			}
			companyPcLaptopDetailsList = companyPCLaptopRepository.findByCpldIsWorkingAndCompanyInfoCompanyId(y,
					companyId);

			if (companyPcLaptopDetailsList.isEmpty()) {
				return Collections.emptyList();
			}
		}

		return elseData(companyPcLaptopDetailsList);
	}

	public List<CompanyPCLaptopDTO> elseData(List<CompanyPcLaptopDetails> pcLaptopDetailsList) {

		return pcLaptopDetailsList.stream().map(x -> {
			CompanyPCLaptopDTO companyPCLaptopDTO = new CompanyPCLaptopDTO();
			BeanUtils.copyProperties(x, companyPCLaptopDTO);
			if (x.getCompanyPurchaseOrder() != null || x.getCompanySalesOrder() != null) {
				companyPCLaptopServiceImpl.dto(x, companyPCLaptopDTO);
			} else {
				companyPCLaptopDTO.setProductName(x.getProductName());
			}
			companyPCLaptopDTO.setStatus(Boolean.TRUE.equals(x.getCpldIsWorking()) ? WORKING : NOT_WORKING);
			companyPCLaptopDTO.setAvailability(x.getEmployeePersonalInfo() == null ? AVAILABLE : ALLOCATED);
			return companyPCLaptopDTO;
		}).collect(Collectors.toList());

	}

	@Override
	public DashboardResponseDTO getPCLaptopDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyPcLaptopDetails> laptopList = companyPCLaptopRepository.findByCompanyInfoCompanyId(companyId);
		List<CompanyPcLaptopDetails> laptopListForGraph = laptopList.stream().filter(
				laptop -> laptop.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& laptop.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());
		return DashboardResponseDTO.builder().cardValues(getPCLaptopCounts(laptopList, CARDS))
				.graphValues(getPCLaptopCounts(laptopListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getPCLaptopCounts(List<CompanyPcLaptopDetails> laptopList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long availableCount = laptopList.stream().filter(laptop -> laptop.getEmployeePersonalInfo() == null).count();
		Long workingCount = laptopList.stream()
				.filter(laptop -> laptop.getCpldIsWorking() != null && laptop.getCpldIsWorking()).count();
		Long totalCount = Long.valueOf(laptopList.size());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type(TOTAL).count(totalCount).build());
		}
		values.add(DashboardDTO.builder().type(AVAILABLE).count(availableCount).build());
		values.add(DashboardDTO.builder().type(WORKING).count(workingCount).build());
		values.add(
				DashboardDTO.builder().type(ALLOCATED).count(Math.subtractExact(totalCount, availableCount)).build());
		values.add(
				DashboardDTO.builder().type(NOT_WORKING).count(Math.subtractExact(totalCount, workingCount)).build());
		return values;
	}

	@Override
	public DashboardResponseDTO getHardwareItemDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyHardwareItems> hardwareItemList = companyHardwareItemsRepository
				.findByCompanyInfoCompanyId(companyId);
		List<CompanyHardwareItems> hardwareItemListGraph = hardwareItemList.stream().filter(
				laptop -> laptop.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& laptop.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());
		return DashboardResponseDTO.builder().cardValues(getHardwareItemCounts(hardwareItemList, CARDS))
				.graphValues(getHardwareItemCounts(hardwareItemListGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getHardwareItemCounts(List<CompanyHardwareItems> hardwareItemList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long workingCount = hardwareItemList.stream()
				.filter(laptop -> laptop.getIsWorking() != null && laptop.getIsWorking()).count();
		Long allocatedCount = hardwareItemList.stream()
				.filter(laptop -> laptop.getEmployeePersonalInfo()!=null).count();
		Long totalCount = Long.valueOf(hardwareItemList.size());
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type(TOTAL).count(totalCount).build());
		}
		values.add(DashboardDTO.builder().type(WORKING).count(workingCount).build());
		values.add(
				DashboardDTO.builder().type(NOT_WORKING).count(Math.subtractExact(totalCount, workingCount)).build());
		values.add(
				DashboardDTO.builder().type(ALLOCATED).count(allocatedCount).build());
		values.add(
				DashboardDTO.builder().type(AVAILABLE).count(Math.subtractExact(totalCount, allocatedCount)).build());
		return values;
	}

	@Override
	public List<CompanyHardwareItemsDTO> typeBasedHardwareItemDetails(Long companyId, String type) {

		Optional<CompanyInfo> companyInfo = companyInfoRepository.findById(companyId);
		

		if (companyInfo.isEmpty()) {
			throw new CompanyNotExistException("Company not found");
		}

		List<CompanyHardwareItems> companyHardwareItemsList;
		List<CompanyHardwareItems> companyHardwareItemsList1;
		
		companyHardwareItemsList1 = companyHardwareItemsRepository.findByCompanyInfoCompanyId(companyId);

		if (type.equalsIgnoreCase(TOTAL)) {
			companyHardwareItemsList = companyHardwareItemsRepository.findByCompanyInfoCompanyId(companyId);

		} else if (type.equalsIgnoreCase(ALLOCATED)) {
			companyHardwareItemsList = companyHardwareItemsList1.stream()
					.filter(x -> x.getEmployeePersonalInfo() != null).collect(Collectors.toList());

		} else if (type.equalsIgnoreCase(AVAILABLE)) {
			companyHardwareItemsList = companyHardwareItemsList1.stream()
					.filter(x -> x.getEmployeePersonalInfo() == null).collect(Collectors.toList());

		}  else {
			boolean y = false;

			if (type.equalsIgnoreCase(WORKING)) {

				y = true;

			}
			companyHardwareItemsList = companyHardwareItemsRepository.findByIsWorkingAndCompanyInfoCompanyId(y,
					companyId);

			if (companyHardwareItemsList.isEmpty()) {
				return Collections.emptyList();
			}
		}

		return getAllDetailsOfHardware(companyHardwareItemsList);

	}

	List<CompanyHardwareItemsDTO> getAllDetailsOfHardware(List<CompanyHardwareItems> companyHardwareItemsList) {
		return companyHardwareItemsList.stream().map(x -> {

			CompanyHardwareItemsDTO companyHardwareItemsDTO = new CompanyHardwareItemsDTO();

			BeanUtils.copyProperties(x, companyHardwareItemsDTO);

			if (x.getCompanyPurchaseOrder() != null || x.getCompanySalesOrder() != null) {

				if (x.getInOut().equalsIgnoreCase("IN")) {

					getPurchaseDetailsOfHardware(companyHardwareItemsDTO, x);

				} else if (x.getInOut().equalsIgnoreCase("OUT")) {
					getSalesDetailsOfHardware(companyHardwareItemsDTO, x);
				}
			} else {
				companyHardwareItemsDTO.setProductName(x.getProductName());
			}
			companyHardwareItemsDTO.setStatus(Boolean.TRUE.equals(x.getIsWorking()) ? WORKING : NOT_WORKING);
			return companyHardwareItemsDTO;
		}).collect(Collectors.toList());
	}

	CompanyHardwareItemsDTO getSalesDetailsOfHardware(CompanyHardwareItemsDTO companyHardwareItemsDTO,
			CompanyHardwareItems x) {
		companyHardwareItemsDTO.setSalesOrderId(x.getCompanySalesOrder().getSalesOrderId());
		companyHardwareItemsDTO.setSubject(x.getCompanySalesOrder().getSubject());
		companyHardwareItemsDTO.setStatus(Boolean.TRUE.equals(x.getIsWorking()) ? WORKING : NOT_WORKING);
		List<SalesOrderItems> findByCompanySalesOrderSalesOrderId = salesOrderItemsRepository
				.findByCompanySalesOrderSalesOrderId(x.getCompanySalesOrder().getSalesOrderId());
		if (findByCompanySalesOrderSalesOrderId == null) {
			throw new SalesOrderItemNotFoundException(AdminDeptConstants.SALES_ORDER_ITEM_NOT_FOUND);
		}
		SalesOrderItems findByProductNameAndCompanySalesOrderSalesOrderId = salesOrderItemsRepository
				.findByProductNameAndCompanySalesOrderSalesOrderId(x.getProductName(),
						x.getCompanySalesOrder().getSalesOrderId());
		if (findByProductNameAndCompanySalesOrderSalesOrderId == null) {
			throw new SalesOrderItemNotFoundException(AdminDeptConstants.SALES_ORDER_ITEM_NOT_FOUND);
		}
		companyHardwareItemsDTO.setProductId(findByProductNameAndCompanySalesOrderSalesOrderId.getSaleItemId());

		return companyHardwareItemsDTO;
	}

	CompanyHardwareItemsDTO getPurchaseDetailsOfHardware(CompanyHardwareItemsDTO companyHardwareItemsDTO,
			CompanyHardwareItems x) {
		companyHardwareItemsDTO.setPurchaseOrderId(x.getCompanyPurchaseOrder().getPurchaseOrderId());

		companyHardwareItemsDTO.setSubject(x.getCompanyPurchaseOrder().getSubject());

		companyHardwareItemsDTO.setStatus(Boolean.TRUE.equals(x.getIsWorking()) ? WORKING : NOT_WORKING);

		List<PurchaseOrderItems> findByCompanyPurchaseOrderPurchaseOrderId = purchaseOrderItemsRepository

				.findByCompanyPurchaseOrderPurchaseOrderId(

						x.getCompanyPurchaseOrder().getPurchaseOrderId());

		if (findByCompanyPurchaseOrderPurchaseOrderId == null) {

			throw new PurchaseOrderItemNotFoundException(AdminDeptConstants.PURCHASE_ORDER_ITEM_NOT_FOUND);

		}
		companyHardwareItemsDTO

				.setProductId(

						purchaseOrderItemsRepository

								.findByProductNameAndCompanyPurchaseOrderPurchaseOrderId(x.getProductName(),

										x.getCompanyPurchaseOrder().getPurchaseOrderId())

								.getPurchaseItemId());

		return companyHardwareItemsDTO;
	}

	@Override
	public List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue) {
		List<CompanyItTickets> ticketList = companyItTicketsRepository.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(filterValue)) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyItTickets> ticketListForCards = ticketList.stream()
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

	public List<HRTicketsBasicDTO> getTicketDTO(List<CompanyItTickets> ticketList, Long companyId) {
		List<EmployeePersonalInfo> employeeDetails = employeePersonalInfoRepository
				.findByCompanyInfoCompanyId(companyId);
		return ticketList.stream().map(ticket -> {
			HRTicketsBasicDTO ticketDTO = new HRTicketsBasicDTO();
			ticketDTO.setTicketObjectId(ticket.getId());
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
}
