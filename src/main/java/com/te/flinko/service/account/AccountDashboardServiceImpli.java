package com.te.flinko.service.account;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.DashboardDTO;
import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.account.AccountPaySlipListDTO;
import com.te.flinko.dto.account.PurchasedOrderDisplayDTO;
import com.te.flinko.dto.account.VendorBasicDetailsDTO;
import com.te.flinko.dto.account.mongo.ContactPerson;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;
import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.account.mongo.CompanyVendorInfo;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyPayrollInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAccountTickets;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.account.CompanyVendorInfoRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admindept.CompanyPurchaseOrderRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAccountTicketsRepository;

@Service
public class AccountDashboardServiceImpli implements AccountDashboardService {

	@Autowired
	private CompanyAccountTicketsRepository companyAccountTicketsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	private CompanyPurchaseOrderRepository companyPurchaseOrderRepository;

	private static final String CARDS = "Cards";

	private static final String GRAPH = "Graph";

	private static final String RESOLVED = "Resolved";

	private static final String TOTAL_CAPS = "TOTAL";

	private static final String TYPE_NOT_FOUND = "Type Not Found";

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	@Autowired
	private CompanyVendorInfoRepository companyVendorInfoRepository;

	@Autowired
	private EmployeeSalaryDetailsRepository employeeSalaryDetailsRepository;

	@Override
	public DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyAccountTickets> ticketList = companyAccountTicketsRepository.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(dashboardRequestDTO.getFilterValue())) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyAccountTickets> ticketListForCards = ticketList.stream()
				.filter(ticket -> (ticket.getCreatedDate().toLocalDate().isAfter(startOfTheWeek.minusDays(1)))
						&& (ticket.getCreatedDate().toLocalDate().isBefore(endOfTheWeek.plusDays(1)))
						&& (ticket.getTicketHistroys() != null) && (!ticket.getTicketHistroys().isEmpty()))
				.collect(Collectors.toList());

		List<CompanyAccountTickets> ticketListForGraph = ticketList.stream().filter(
				ticket -> ticket.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& ticket.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getTicketCounts(ticketListForCards, CARDS))
				.graphValues(getTicketCounts(ticketListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getTicketCounts(List<CompanyAccountTickets> ticketList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		Long solvedCount = ticketList.stream().filter(ticket -> "Resolved"
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
	public DashboardResponseDTO getSalesAndPurchaseDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		CompanyInfo companyInfo = companyInfoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company Not Found"));
		List<DashboardDTO> valuesForCards = new ArrayList<>();
		valuesForCards.add(DashboardDTO.builder().type("Work Order")
				.count(Long.valueOf(companyInfo.getCompanyWorkOrderList().size())).build());
		valuesForCards.add(DashboardDTO.builder().type("Sales Order")
				.count(Long.valueOf(companyInfo.getCompanySalesOrderList().size())).build());
		valuesForCards.add(DashboardDTO.builder().type("Purchase Order")
				.count(Long.valueOf(companyInfo.getCompanyPurchaseOrderList().size())).build());
		valuesForCards.add(DashboardDTO.builder().type("Sales Invoice")
				.count(Long.valueOf(companyInfo.getCompanySalesInvoiceList().size())).build());
		valuesForCards.add(DashboardDTO.builder().type("Purchase Invoice")
				.count(Long.valueOf(companyInfo.getCompanyPurchaseInvoiceList().size())).build());

		List<DashboardDTO> valuesForGraph = new ArrayList<>();
		valuesForGraph.add(DashboardDTO.builder().type("Work Order")
				.count(companyInfo.getCompanyWorkOrderList().stream().filter(work -> work.getCreatedDate().toLocalDate()
						.isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& work.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
						.count())
				.build());
		valuesForGraph.add(DashboardDTO.builder().type("Sales Order")
				.count(companyInfo.getCompanySalesOrderList().stream().filter(sales -> sales.getCreatedDate()
						.toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& sales.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
						.count())
				.build());
		valuesForGraph.add(DashboardDTO.builder().type("Purchase Order")
				.count(companyInfo.getCompanyPurchaseOrderList().stream()
						.filter(purchase -> purchase.getCreatedDate().toLocalDate()
								.isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
								&& purchase.getCreatedDate().toLocalDate()
										.isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
						.count())
				.build());
		valuesForGraph.add(DashboardDTO.builder().type("Sales Invoice")
				.count(companyInfo.getCompanySalesInvoiceList().stream()
						.filter(salesInvoice -> salesInvoice.getCreatedDate().toLocalDate()
								.isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
								&& salesInvoice.getCreatedDate().toLocalDate()
										.isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
						.count())
				.build());
		valuesForGraph.add(DashboardDTO.builder().type("Purchase Invoice")
				.count(companyInfo.getCompanyPurchaseInvoiceList().stream()
						.filter(purchaseInvoice -> purchaseInvoice.getCreatedDate().toLocalDate()
								.isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
								&& purchaseInvoice.getCreatedDate().toLocalDate()
										.isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
						.count())
				.build());

		return DashboardResponseDTO.builder().cardValues(valuesForCards).graphValues(valuesForGraph).build();
	}

	@Override
	public DashboardResponseDTO getVendorDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<CompanyVendorInfo> vendorList = companyVendorInfoRepository.findByCompanyId(companyId);

		List<CompanyVendorInfo> vendorListForGraph = vendorList.stream().filter(
				vendor -> vendor.getCreatedDate().toLocalDate().isAfter(dashboardRequestDTO.getStartDate().minusDays(1))
						&& vendor.getCreatedDate().toLocalDate().isBefore(dashboardRequestDTO.getEndDate().plusDays(1)))
				.collect(Collectors.toList());

		return DashboardResponseDTO.builder().cardValues(getVendorCounts(vendorList, CARDS))
				.graphValues(getVendorCounts(vendorListForGraph, GRAPH)).build();
	}

	private List<DashboardDTO> getVendorCounts(List<CompanyVendorInfo> vendorList, String type) {
		List<DashboardDTO> values = new ArrayList<>();
		BigDecimal amountToBePaid = new BigDecimal(0);
		BigDecimal amountPaid = new BigDecimal(0);
		for (CompanyVendorInfo vendor : vendorList) {
			if (vendor.getAmountPaid() != null && vendor.getAmountToBePaid() != null) {
				amountToBePaid = amountToBePaid.add(vendor.getAmountToBePaid());
				amountPaid = amountPaid.add(vendor.getAmountPaid());
			}
		}
		if (type.equalsIgnoreCase(CARDS)) {
			values.add(DashboardDTO.builder().type("Total").count(Long.valueOf(vendorList.size())).build());
		}
		values.add(DashboardDTO.builder().type("Pending Amount").count(amountToBePaid.subtract(amountPaid)).build());
		values.add(DashboardDTO.builder().type("Paid Amount").count(amountPaid).build());
		return values;
	}

	@Override
	public DashboardResponseDTO getSalaryDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId) {
		List<String> finalizingDates = new ArrayList<>();
		List<String> paymentDates = new ArrayList<>();
		List<String> paySlipGenerationDates = new ArrayList<>();
		CompanyInfo companyInfo = companyInfoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company Not Found"));
		LocalDate now = LocalDate.now();
		List<CompanyPayrollInfo> companyPayrollInfoList = companyInfo.getCompanyPayrollInfoList();
		for (CompanyPayrollInfo companyPayrollInfo : companyPayrollInfoList) {
			finalizingDates.add(companyPayrollInfo.getPayrollName() + " # " + companyPayrollInfo.getSalaryApprovalDate()
					+ " " + now.getMonth());
			paymentDates.add(companyPayrollInfo.getPayrollName() + " # " + companyPayrollInfo.getPaymentDate() + " "
					+ ((companyPayrollInfo.getSalaryApprovalDate() > companyPayrollInfo.getPaymentDate())
							? now.getMonth().plus(1)
							: now.getMonth()));
			paySlipGenerationDates.add(
					companyPayrollInfo.getPayrollName() + " # " + companyPayrollInfo.getPaySlipGenerationDate() + " "
							+ ((companyPayrollInfo.getPaymentDate() > companyPayrollInfo.getPaySlipGenerationDate())
									? now.getMonth().plus(1)
									: now.getMonth()));
		}
		List<DashboardDTO> valuesForCards = new ArrayList<>();
		valuesForCards.add(DashboardDTO.builder().type("Finalizing Date").count(finalizingDates).build());
		valuesForCards.add(DashboardDTO.builder().type("Payment Date").count(paymentDates).build());
		valuesForCards
				.add(DashboardDTO.builder().type("Payslip Generation Date").count(paySlipGenerationDates).build());

		List<EmployeeSalaryDetails> salaryDetailsForGraph = employeeSalaryDetailsRepository
				.findByCompanyInfoCompanyIdAndMonthAndYear(companyId, dashboardRequestDTO.getMonth(),
						dashboardRequestDTO.getYear());
		List<DashboardDTO> valuesForGraph = new ArrayList<>();
		valuesForGraph.add(DashboardDTO.builder().type("Finalized")
				.count(salaryDetailsForGraph.stream().filter(EmployeeSalaryDetails::getIsFinalized).count()).build());
		valuesForGraph.add(DashboardDTO.builder().type("Paid")
				.count(salaryDetailsForGraph.stream().filter(EmployeeSalaryDetails::getIsPaid).count()).build());
		valuesForGraph.add(DashboardDTO.builder().type("Payslip Generated")
				.count(salaryDetailsForGraph.stream().filter(EmployeeSalaryDetails::getIsPayslipGenerated).count())
				.build());

		return DashboardResponseDTO.builder().cardValues(valuesForCards).graphValues(valuesForGraph).build();
	}

	public List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue) {
		List<CompanyAccountTickets> ticketList = companyAccountTicketsRepository.findByCompanyId(companyId);
		LocalDate date = LocalDate.now();
		if ("previous".equalsIgnoreCase(filterValue)) {
			date = date.minusDays(7);
		}
		LocalDate startOfTheWeek = date.with(DayOfWeek.MONDAY);
		LocalDate endOfTheWeek = startOfTheWeek.plusDays(7);

		List<CompanyAccountTickets> ticketListForCards = ticketList.stream()
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

	public List<HRTicketsBasicDTO> getTicketDTO(List<CompanyAccountTickets> ticketList, Long companyId) {
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
	public List<VendorBasicDetailsDTO> getVendorDetailsByStatus(Long companyId, String type) {
		List<CompanyVendorInfo> vendorList = companyVendorInfoRepository.findByCompanyId(companyId);
		switch (type.toUpperCase()) {
		case TOTAL_CAPS:
			return getVendorDTO(vendorList);
		case "PENDING AMOUNT":
			return getVendorDTO(vendorList.stream().filter(vendor -> vendor.getAmountPaid() != null
					&& vendor.getAmountToBePaid() != null
					&& (vendor.getAmountToBePaid().subtract(vendor.getAmountPaid())).compareTo(new BigDecimal(0)) > 0)
					.collect(Collectors.toList()));

		case "PAID AMOUNT":
			return getVendorDTO(vendorList.stream().filter(
					vendor -> vendor.getAmountPaid() != null && vendor.getAmountPaid().compareTo(new BigDecimal(0)) > 0)
					.collect(Collectors.toList()));
		default:
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}
	}

	public List<VendorBasicDetailsDTO> getVendorDTO(List<CompanyVendorInfo> vendorList) {
		return vendorList.stream().map(companyVendorInfo -> {
			VendorBasicDetailsDTO vendorBasicDetailsDTO = new VendorBasicDetailsDTO();
			BeanUtils.copyProperties(companyVendorInfo, vendorBasicDetailsDTO);
			List<ContactPerson> contactPersons = companyVendorInfo.getContactPersons();
			if (contactPersons != null && !contactPersons.isEmpty()) {
				vendorBasicDetailsDTO.setContactPersonName(contactPersons.get(0).getContactPersonName());
				vendorBasicDetailsDTO.setEmailId(contactPersons.get(0).getEmailId());
				vendorBasicDetailsDTO.setMobileNumber(contactPersons.get(0).getMobileNumber());
			}
			return vendorBasicDetailsDTO;
		}).collect(Collectors.toList());

	}

	@Override
	public List<AccountPaySlipListDTO> getSalaryDetailsByStatus(Long companyId,
			DashboardRequestDTO dashboardRequestDTO) {
		List<EmployeeSalaryDetails> salaryDetails = employeeSalaryDetailsRepository
				.findByCompanyInfoCompanyIdAndMonthAndYear(companyId, dashboardRequestDTO.getMonth(),
						dashboardRequestDTO.getYear());
		if (dashboardRequestDTO.getType().equalsIgnoreCase("FINALIZING DATE")
				|| dashboardRequestDTO.getType().equalsIgnoreCase("FINALIZED")) {
			return getSalaryDTO(
					salaryDetails.stream().filter(EmployeeSalaryDetails::getIsFinalized).collect(Collectors.toList()));
		} else if (dashboardRequestDTO.getType().equalsIgnoreCase("PAYMENT DATE")
				|| dashboardRequestDTO.getType().equalsIgnoreCase("PAID")) {
			return getSalaryDTO(
					salaryDetails.stream().filter(EmployeeSalaryDetails::getIsPaid).collect(Collectors.toList()));
		} else if (dashboardRequestDTO.getType().equalsIgnoreCase("PAYSLIP GENERATION DATE")
				|| dashboardRequestDTO.getType().equalsIgnoreCase("PAYSLIP GENERATED")) {
			return getSalaryDTO(salaryDetails.stream().filter(EmployeeSalaryDetails::getIsPayslipGenerated)
					.collect(Collectors.toList()));
		} else {
			throw new DataNotFoundException(TYPE_NOT_FOUND);
		}
	}

	public List<AccountPaySlipListDTO> getSalaryDTO(List<EmployeeSalaryDetails> salaryDetails) {
		return salaryDetails.stream().map(salary -> {
			AccountPaySlipListDTO accountPaySlipListDTO = new AccountPaySlipListDTO();
			BeanUtils.copyProperties(salary, accountPaySlipListDTO);
			EmployeePersonalInfo employeePersonalInfo = salary.getEmployeePersonalInfo();
			if (employeePersonalInfo != null) {
				EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
				accountPaySlipListDTO
						.setFullname(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
				if (employeeOfficialInfo != null) {
					accountPaySlipListDTO.setEmployeeId(employeeOfficialInfo.getEmployeeId());
				}
			}
			return accountPaySlipListDTO;
		}).collect(Collectors.toList());

	}

	@Override
	public List<PurchasedOrderDisplayDTO> getAllPurchaseOrder(Long companyId) {
		List<CompanyPurchaseOrder> companyPurchaseOrderList = companyPurchaseOrderRepository
				.findByCompanyInfoCompanyId(companyId);
		List<PurchasedOrderDisplayDTO> companyPurchaseOrderDTOList = new ArrayList<>();
		companyPurchaseOrderList.forEach((i) -> {
			PurchasedOrderDisplayDTO purchasedOrderDisplayDTO = new PurchasedOrderDisplayDTO();
			BeanUtils.copyProperties(i, purchasedOrderDisplayDTO);
			if (i.getVendorId() != null) {
				Optional<CompanyVendorInfo> vendorInfoOptional = companyVendorInfoRepository.findById(i.getVendorId());
				purchasedOrderDisplayDTO.setVendorName(
						vendorInfoOptional.isPresent() ? vendorInfoOptional.get().getVendorName() : null);
			}
			companyPurchaseOrderDTOList.add(purchasedOrderDisplayDTO);
		});
		Collections.reverse(companyPurchaseOrderDTOList);
		return companyPurchaseOrderDTOList;
	}

}
