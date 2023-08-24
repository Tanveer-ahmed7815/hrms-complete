package com.te.flinko.service.sales;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.te.flinko.dto.DepartmentProjectDTO;
import com.te.flinko.dto.employee.MailDto;
import com.te.flinko.dto.sales.EmployeeBirthdayDTO;
import com.te.flinko.dto.sales.EmployeePerformanceDTO;
import com.te.flinko.dto.sales.EventBirthdayOtherDetailsDTO;
import com.te.flinko.dto.sales.EventDTO;
import com.te.flinko.entity.employee.EmployeeAnnualSalary;
import com.te.flinko.entity.employee.EmployeeLeaveAllocated;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.entity.report.mongo.EmployeePerformance;
import com.te.flinko.entity.report.mongo.MonthlyPerformance;
import com.te.flinko.entity.report.mongo.ProjectTargetPerformance;
import com.te.flinko.repository.employee.EmployeeAnnualSalaryRepository;
import com.te.flinko.repository.employee.EmployeeLeaveAllocatedRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.hr.CompanyEventDetailsRepository;
import com.te.flinko.repository.report.EmployeePerformanceRepository;
import com.te.flinko.service.mail.employee.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonDashboardServiceImpl implements CommonDashboardService {

	private final EmployeePerformanceRepository employeePerformanceRepository;

	private final CompanyEventDetailsRepository companyEventDetailsRepository;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final EmployeeLeaveAllocatedRepository employeeLeaveAllocatedRepository;

	private final EmployeeAnnualSalaryRepository employeeAnnualSalaryRepository;

	private final EmailService emailService;

	private static List<LocalDate> dates = new ArrayList<>();

	private MonthlyPerformance monthlyPerformance;

	@Override
	public EventBirthdayOtherDetailsDTO getEventsBirthdayOtherDetails(Long companyId, Long employeeInfoId,
			DepartmentProjectDTO departmentProjectDTO) {
		List<EventDTO> events = companyEventDetailsRepository.findByCompanyInfoCompanyId(companyId).stream()
				.filter(e -> e.getEventDate().isAfter(LocalDate.now()) || e.getEventDate().isEqual(LocalDate.now()))
				.map(event -> EventDTO.builder().eventDate(event.getEventDate()).eventName(event.getEventTitle())
						.photoUrl(event.getPhotoUrl()).eventEndTime(event.getEndTime())
						.eventStartTime(event.getStartTime()).build())
				.collect(Collectors.toList());
		List<EmployeeAnnualSalary> employeeAnnualSalary = employeeAnnualSalaryRepository
				.findByEmployeePersonalInfoEmployeeInfoId(employeeInfoId);

		List<EmployeeBirthdayDTO> employeeBirthday = employeePersonalInfoRepository
				.findByIsActiveTrueAndCompanyInfoCompanyIdAndEmployeeOfficialInfoNotNullAndDobIn(companyId, dates)
				.stream().map(emp -> {
					EmployeeOfficialInfo employeeOfficialInfo = emp.getEmployeeOfficialInfo();
					return EmployeeBirthdayDTO.builder().employeeInfoId(emp.getEmployeeInfoId())
							.employeeName(emp.getFirstName() + " " + emp.getLastName())
							.department(employeeOfficialInfo.getDepartment())
							.designation(employeeOfficialInfo.getDesignation()).build();
				}).collect(Collectors.toList());
		LocalDate localDate = LocalDate.now();

		List<EmployeePerformance> employeePerformance = employeePerformanceRepository.findByCompanyIdAndYear(companyId,
				(long) LocalDate.now().getYear());
		List<EmployeePerformanceDTO> collect = employeePerformance.stream().map(per -> {
			monthlyPerformance = Optional
					.ofNullable(per.getMonthlyPerformance().get(localDate.minusMonths(1).getMonth().toString()))
					.orElse(MonthlyPerformance.builder().build());
			if (departmentProjectDTO.getProjectId() != null && departmentProjectDTO.getDepartmentName() == null) {
				return EmployeePerformanceDTO.builder().departmentName(per.getDepartmentName())
						.companyId(per.getCompanyId()).employeeId(per.getEmployeeId())
						.employeeName(per.getEmployeeName())
						.employeeRating(BigDecimal
								.valueOf(Optional.ofNullable(departmentProjectDTO)
										.filter(v -> v.getProjectId() != null && v.getProjectId() != 0)
										.map(m -> monthlyPerformance.getProjectDetails().stream()
												.filter(p -> Objects.equals(p.getProjectId(),
														departmentProjectDTO.getProjectId()))
												.map(ProjectTargetPerformance::getTargetPerProject)
												.collect(Collectors.averagingDouble(x -> x)))
										.orElseGet(() -> 0.0))
								.setScale(2, RoundingMode.HALF_DOWN).doubleValue())
						.build();
			} else if (departmentProjectDTO.getProjectId() == null && departmentProjectDTO.getDepartmentName() != null
					&& departmentProjectDTO.getDepartmentName().equalsIgnoreCase(per.getDepartmentName())) {
				return EmployeePerformanceDTO.builder().departmentName(per.getDepartmentName())
						.employeeId(per.getEmployeeId()).employeeName(
								per.getEmployeeName())
						.companyId(per.getCompanyId())
						.employeeRating(BigDecimal
								.valueOf(Optional.ofNullable(departmentProjectDTO)
										.filter(v -> v.getDepartmentName() != null && v.getDepartmentName()
												.equalsIgnoreCase(departmentProjectDTO.getDepartmentName()))
										.map(b -> monthlyPerformance.getActivities().doubleValue()
												+ monthlyPerformance.getPunctual().doubleValue()
												+ monthlyPerformance.getTargetAchived().doubleValue()
												+ monthlyPerformance.getLeaves().doubleValue())
										.orElse(0.0) / 4)
								.setScale(2, RoundingMode.HALF_DOWN).doubleValue())
						.build();
			} else if (departmentProjectDTO.getProjectId() == null
					&& departmentProjectDTO.getDepartmentName() == null) {
				return EmployeePerformanceDTO.builder().departmentName(per.getDepartmentName())
						.employeeId(
								per.getEmployeeId())
						.employeeName(per.getEmployeeName())
						.employeeRating(
								BigDecimal
										.valueOf(Optional
												.ofNullable(departmentProjectDTO.getProjectId() == null
														&& departmentProjectDTO.getDepartmentName() == null)
												.filter(b -> b)
												.map(v -> monthlyPerformance.getActivities().doubleValue()
														+ monthlyPerformance.getPunctual().doubleValue()
														+ monthlyPerformance.getTargetAchived().doubleValue()
														+ monthlyPerformance.getLeaves().doubleValue())
												.orElse(0.0) / 4)
										.setScale(2, RoundingMode.HALF_DOWN).doubleValue())
						.build();
			} else if (departmentProjectDTO.getProjectId() != null && departmentProjectDTO.getDepartmentName() != null
					&& departmentProjectDTO.getDepartmentName().equalsIgnoreCase(per.getDepartmentName())) {
				return EmployeePerformanceDTO.builder().departmentName(per.getDepartmentName())
						.companyId(per.getCompanyId()).employeeId(per.getEmployeeId())
						.employeeName(per.getEmployeeName())
						.employeeRating(BigDecimal
								.valueOf(Optional.ofNullable(departmentProjectDTO)
										.filter(v -> v.getProjectId() != null && v.getDepartmentName() != null
												&& v.getDepartmentName()
														.equalsIgnoreCase(departmentProjectDTO.getDepartmentName()))
										.map(m -> monthlyPerformance.getProjectDetails().stream()
												.filter(p -> Objects.equals(p.getProjectId(),
														departmentProjectDTO.getProjectId()))
												.map(ProjectTargetPerformance::getTargetPerProject)
												.collect(Collectors.averagingDouble(x -> x)))
										.orElseGet(() -> 0.0))
								.setScale(2, RoundingMode.HALF_DOWN).doubleValue())
						.build();
			}
			return null;
		}).filter(Objects::nonNull).filter(e -> e.getEmployeeName() != null)
				.sorted(Comparator.comparingDouble(EmployeePerformanceDTO::getEmployeeRating).reversed()).limit(5)
				.collect(Collectors.toList());

		EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
				.findByEmployeeInfoIdAndIsActiveAndCompanyInfoCompanyId(employeeInfoId, Boolean.TRUE, companyId);

		EmployeeLeaveAllocated leave = employeeLeaveAllocatedRepository
				.findByEmployeePersonalInfoEmployeeInfoId(employeeInfoId).orElse(null);

		List<EmployeeSalaryDetails> employeeSalaryDetailsList = employeePersonalInfo.getEmployeeSalaryDetailsList();

		Double departmentRating = BigDecimal.valueOf(employeePerformance.stream()
				.filter(d -> d.getDepartmentName() != null && d.getMonthlyPerformance() != null
						&& d.getMonthlyPerformance().get(localDate.minusMonths(1).getMonth().toString()) != null
						&& d.getDepartmentName().equalsIgnoreCase(departmentProjectDTO.getDashboardDepartmentName()))
				.map(dept -> {
					MonthlyPerformance deptMonthlyPerformance = dept.getMonthlyPerformance()
							.get(localDate.minusMonths(1).getMonth().toString());
					return (deptMonthlyPerformance.getActivities().doubleValue()
							+ deptMonthlyPerformance.getPunctual().doubleValue()
							+ deptMonthlyPerformance.getTargetAchived().doubleValue()
							+ deptMonthlyPerformance.getLeaves().doubleValue()) / 4;
				}).collect(Collectors.averagingDouble(a -> a))).setScale(2, RoundingMode.HALF_DOWN).doubleValue();

		if (!departmentProjectDTO.getDesignationName().equalsIgnoreCase("ADMIN"))
			departmentRating = null;

		return EventBirthdayOtherDetailsDTO.builder().employeePerformanceDTOs(collect)
				.approvedLeaves(employeePersonalInfo.getEmployeeLeaveAppliedList().stream()
						.filter(x -> x.getStatus() != null && x.getStatus().equalsIgnoreCase("APPROVED")).map(x -> {
							double between = ChronoUnit.DAYS.between(x.getStartDate().minusDays(1), x.getEndDate());
							return ChronoUnit.HALF_DAYS.between(x.getStartTime(), x.getEndTime()) > 4.5 ? between + 0.5
									: between;
						}).reduce(0.0, (x, y) -> x + y))
				.monthlySelary(employeeSalaryDetailsList.isEmpty() ? BigDecimal.ZERO
						: employeeSalaryDetailsList.get(employeeSalaryDetailsList.size() - 1).getNetPay())
				.allottedLeaves(leave != null && !leave.getLeavesDetails().isEmpty() ? leave.getLeavesDetails().values()
						.stream().map(Double::parseDouble).reduce(0.0, (x, y) -> x + y) : 0.0)
				.departmentRating(departmentRating).employeeBirthdayDTOs(employeeBirthday).eventDTOs(events)
				.annualSelary(employeeAnnualSalary.isEmpty() ? BigDecimal.ZERO
						: employeeAnnualSalary.get(0).getAnnualSalary())
				.build();
	}

	@Override
	public String sendWishes(Long companyId, Long employeeInfoIdFrom, Long employeeInfoIdTo) {
		return employeePersonalInfoRepository.findByEmployeeInfoIdAndCompanyInfoIsActiveTrue(employeeInfoIdTo)
				.map(emp -> {
					EmployeePersonalInfo employeePersonalInfoFrom = employeePersonalInfoRepository
							.findByEmployeeInfoIdAndCompanyInfoIsActiveTrue(employeeInfoIdFrom)
							.orElseGet(EmployeePersonalInfo::new);
					MailDto mailDto = MailDto.builder()
							.subject("Happy Birthday " + emp.getFirstName() + " " + emp.getLastName())
							.body("Hi " + emp.getFirstName() + " " + emp.getLastName() + " ," + "\r\n" + "\r\n"
									+ "       Sending your way birthday wishes for a beautiful year ahead. May your lucky stars continue to shine"
									+ " and make all your dreams come true. Enjoy your day with all the pleasures it has in store"
									+ "\r\n" + "\r\n" + "Happy birthday once again." + "\r\n" + "\r\n" + "From: "
									+ "\r\n" + employeePersonalInfoFrom.getFirstName() + " "
									+ employeePersonalInfoFrom.getLastName())
							.to(emp.getEmployeeOfficialInfo().getOfficialEmailId()).build();
					emailService.sendMail(mailDto);
					return "Send Wish Successfully";
				}).orElse("Employee Not Found");
	}

	static {
		LocalDate localDate = LocalDate.now();
		for (int i = localDate.getYear() - 110; i < localDate.getYear() - 10; i++) {
			dates.add(LocalDate.of(i, localDate.getMonthValue(), localDate.getDayOfMonth()));
		}
	}

}