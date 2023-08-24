package com.te.flinko.schedule.employee;

import static com.te.flinko.common.hr.HrConstants.FIXED;
import static com.te.flinko.common.hr.HrConstants.ONE_HUNDRED;
import static com.te.flinko.common.hr.HrConstants.PERCENTAGE;
import static com.te.flinko.common.hr.HrConstants.REIMBURSEMENT;
import static com.te.flinko.common.hr.HrConstants.REWARDS;
import static com.te.flinko.common.hr.HrConstants.STATUS_APPROVED;
import static com.te.flinko.common.hr.HrConstants.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.te.flinko.dto.account.ConversionRateDTO;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.report.EmployeePerformanceDTO;
import com.te.flinko.dto.report.EmployeePunctualPerformanceDTO;
import com.te.flinko.entity.account.mongo.CurrencyConvert;
import com.te.flinko.entity.account.mongo.CurrencyDetails;
import com.te.flinko.entity.admin.CompanyHolidayDetails;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyPayrollDeduction;
import com.te.flinko.entity.admin.CompanyPayrollEarning;
import com.te.flinko.entity.admin.CompanyPayrollInfo;
import com.te.flinko.entity.admin.CompanyShiftInfo;
import com.te.flinko.entity.admin.CompanyWorkWeekRule;
import com.te.flinko.entity.admin.WorkOffDetails;
import com.te.flinko.entity.employee.CompanyEmployeeResignationDetails;
import com.te.flinko.entity.employee.EmployeeAnnualSalary;
import com.te.flinko.entity.employee.EmployeeBonus;
import com.te.flinko.entity.employee.EmployeeLeaveApplied;
import com.te.flinko.entity.employee.EmployeeNotification;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePerformanceRule;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReferenceInfo;
import com.te.flinko.entity.employee.EmployeeReimbursementInfo;
import com.te.flinko.entity.employee.EmployeeReviseSalary;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.entity.employee.EmployeeVariablePay;
import com.te.flinko.entity.employee.mongo.AttendanceDetails;
import com.te.flinko.entity.employee.mongo.EmployeeAttendanceDetails;
import com.te.flinko.entity.project.mongo.ProjectTaskDetails;
import com.te.flinko.entity.report.mongo.EmployeePerformance;
import com.te.flinko.entity.report.mongo.MonthlyPerformance;
import com.te.flinko.entity.report.mongo.ProjectTargetPerformance;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.repository.account.mongo.CurrencyConvertRepository;
import com.te.flinko.repository.admin.CompanyHolidayDetailsRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admin.CompanyPayRollInfoRepository;
import com.te.flinko.repository.admin.CompanyRuleRepository;
import com.te.flinko.repository.admindept.CompanyItTicketsRepository;
import com.te.flinko.repository.employee.EmployeeAnnualSalaryRepository;
import com.te.flinko.repository.employee.EmployeeLeaveAppliedRepository;
import com.te.flinko.repository.employee.EmployeeNotificationRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeResignationRequestRepository;
import com.te.flinko.repository.employee.EmployeeReviseSalaryRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;
import com.te.flinko.repository.employee.mongo.EmployeeAttendanceDetailsRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAccountTicketsRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAdminDeptTicketsRepo;
import com.te.flinko.repository.helpandsupport.mongo.CompanyHrTicketsRepository;
import com.te.flinko.repository.hr.CompanyEventDetailsRepository;
import com.te.flinko.repository.project.mongo.ProjectTaskDetailsRepository;
import com.te.flinko.repository.report.EmployeePerformanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTask {

	private static final String RESOLVED = "Resolved";

	private static final String ABSCONDED = "Absconded";

	private static final String TERMINATED = "Terminated";

	private static final String RESIGNED = "Resigned";

	private final EmployeePerformanceRepository employeePerformanceRepository;

	private final CurrencyConvertRepository currencyConvertRepository;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final CompanyHolidayDetailsRepository companyHolidayDetailsRepository;

	private final EmployeeAttendanceDetailsRepository employeeAttendanceDetailsRepository;

	private final ProjectTaskDetailsRepository projectTaskDetailsRepository;

	private final EmployeeLeaveAppliedRepository employeeLeaveAppliedRepository;

	private final CompanyRuleRepository companyRuleRepository;

	private final CompanyEventDetailsRepository companyEventDetailsRepository;

	private final EmployeeResignationRequestRepository employeeResignationRequestRepository;

	private final EmployeeNotificationRepository employeeNotificationRepository;

	private final EmployeeAnnualSalaryRepository employeeAnnualSalaryRepo;

	private final EmployeeSalaryDetailsRepository employeeSalaryDetailsRepo;

	private final CompanyPayRollInfoRepository companyPayRollInfoRepository;

	private final CompanyInfoRepository companyInfoRepository;

	private final EmployeeReviseSalaryRepository employeeReviseSalaryRepository;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final CompanyHrTicketsRepository companyHrTicketsRepository;

	private final CompanyAccountTicketsRepository companyAccountTicketsRepository;

	private final CompanyAdminDeptTicketsRepo companyAdminDeptTicketsRepo;

	private final CompanyItTicketsRepository companyItTicketsRepository;

	private Long userId;

	private List<String> countryCodes = List.of("INR", "USD", "GBP"); // British Pound =GBP

	@Transactional
	@Scheduled(cron = "@monthly", zone = "Asia/Kolkata") // once a month (0 0 0 1 * *) or @monthly
	public void employeePerformance() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		LocalDate localDate = LocalDate.now().minusMonths(1);
		LocalDateTime startDate = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(),
				localDate.lengthOfMonth(), 12, 0);

		int hrTicketsLength = companyHrTicketsRepository.findByCreatedDateBetween(startDate, endDate).size();
		int accountTicketsLength = companyAccountTicketsRepository.findByCreatedDateBetween(startDate, endDate).size();
		int adminDeptTicketsLength = companyAdminDeptTicketsRepo.findByCreatedDateBetween(startDate, endDate).size();
		int itTicketsLength = companyItTicketsRepository.findByCreatedDateBetween(startDate, endDate).size();
		List<EmployeePerformance> employeePerformance = employeePersonalInfoRepository
				.findByIsActiveTrueAndCompanyInfoNotNullAndCompanyInfoIsActiveTrueAndCompanyInfoIsSubmitedTrueAndEmployeeOfficialInfoNotNull()
				.filter(x -> !x.isEmpty()).map(empList -> empList.stream().map(emp -> {
					EmployeePerformanceDTO leavePerformance = leavePerformance(emp.getCompanyInfo().getCompanyId(),
							localDate, emp.getEmployeeInfoId(), hrTicketsLength, accountTicketsLength,
							adminDeptTicketsLength, itTicketsLength);

					EmployeePerformance performance = employeePerformanceRepository
							.findByCompanyIdAndEmployeeInfoIdAndYear(emp.getCompanyInfo().getCompanyId(),
									emp.getEmployeeInfoId(), (long) localDate.getYear())
							.orElseGet(
									() -> EmployeePerformance.builder().companyId(emp.getCompanyInfo().getCompanyId())
											.employeeInfoId(emp.getEmployeeInfoId())
											.employeeName(emp.getFirstName() + " " + emp.getLastName())
											.employeeId(emp.getEmployeeOfficialInfo().getEmployeeId())
											.departmentName(emp.getEmployeeOfficialInfo().getDepartment())
											.year((long) LocalDate.now().getYear()).build());

					Map<String, MonthlyPerformance> monthlyPerformance = performance.getMonthlyPerformance() == null
							? new HashMap<>()
							: performance.getMonthlyPerformance();

					monthlyPerformance.put(localDate.getMonth().toString(), MonthlyPerformance.builder()
							.leaves(leavePerformance.getLeaves()).activities(leavePerformance.getActivities())
							.punctual(leavePerformance.getPunctual()).targetAchived(leavePerformance.getTargetAchived())
							.tickets(leavePerformance.getTicketSolveRating())
							.projectDetails(leavePerformance.getProjectTargetPerformances()).build());
					performance.setMonthlyPerformance(monthlyPerformance);
					return performance;

				}).collect(Collectors.toList())).orElseThrow(() -> new DataNotFoundException("No Employee Found"));
		employeePerformanceRepository.saveAll(employeePerformance);
	}

	private EmployeePerformanceDTO leavePerformance(Long companyId, LocalDate date, Long employeeInfoId,
			int hrTicketsLength, int accountTicketsLength, int adminDeptTicketsLength, int itTicketsLength) {
		return companyRuleRepository.findByCompanyInfoCompanyId(companyId).filter(x -> !x.isEmpty()).map(rule -> {
			int totMonth = date.lengthOfMonth();
			EmployeePerformanceDTO stars = rule.stream().filter(r -> r.getEmployeePerformanceRule() != null)
					.map(performanceRule -> {
						EmployeePerformanceRule employeePerformanceRule = performanceRule.getEmployeePerformanceRule();
						return EmployeePerformanceDTO.builder()
								.noOfLeaveInMonth(employeePerformanceRule.getNoOfLeaveInMonth())
								.unapprovedLeave(employeePerformanceRule.getUnapprovedLeave())
								.lateLogin(employeePerformanceRule.getLateLogin())
								.achievedMoreThen100Per(employeePerformanceRule.getAchievedMoreThen100Per())
								.achievedMoreThen50Per(employeePerformanceRule.getAchievedMoreThen50Per())
								.targetAchived(employeePerformanceRule.getTargetAchived()).build();
					}).findFirst().orElseGet(EmployeePerformanceDTO::new);

			EmployeePunctualPerformanceDTO monthDetails = getMonthDetails(employeeInfoId, companyId, date);
			EmployeePersonalInfo employeePersonalInfo = getEmployeePersonalInfo(employeeInfoId, companyId);
			EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
			CompanyShiftInfo companyShiftInfo = employeeOfficialInfo.getCompanyShiftInfo();

			// punctual performance
			Optional<EmployeeAttendanceDetails> attendance = employeeAttendanceDetailsRepository
					.findByEmployeeInfoIdAndMonthNoAndYear(employeeInfoId, date.getMonthValue(), date.getYear());

			EmployeePunctualPerformanceDTO punctual = attendance.map(x -> {
				Integer reduce = x.getAttendanceDetails().stream()
						.filter(abs -> !monthDetails.getHolidays().contains(abs.getPunchIn().toLocalDate()))
						.map(att -> {
							LocalTime punchIn = att.getPunchIn().toLocalTime();
							LocalTime punchOut = att.getPunchOut().toLocalTime();
							LocalTime localTime = punchOut.minusHours(punchIn.getHour())
									.minusMinutes(punchIn.getMinute());
							LocalTime logoutTime = companyShiftInfo == null ? LocalTime.of(0, 0, 0)
									: companyShiftInfo.getLogoutTime();
							return Optional
									.ofNullable(localTime.compareTo(logoutTime.minusHours(logoutTime.getHour())
											.minusMinutes(logoutTime.getMinute())) < 0)
									.filter(com -> com).map(log -> 1).orElse(0);
						}).reduce(0, (sum, y) -> sum + y);
				return EmployeePunctualPerformanceDTO.builder().noOfAbs(reduce).build();
			}).orElseGet(EmployeePunctualPerformanceDTO::new);
			if (monthDetails.getHolidays() == null) {
				monthDetails.setHolidays(List.of());
			}
			double aviableDays = (date.lengthOfMonth() - monthDetails.getHolidays().size()
					- (stars.getLateLogin() == null ? 0 : stars.getLateLogin().intValue()));

			BigDecimal pucntualStar = BigDecimal
					.valueOf(stars.getUnapprovedLeave().doubleValue()
							* ((aviableDays - punctual.getNoOfAbs()) / aviableDays))
					.setScale(2, RoundingMode.HALF_DOWN);
			System.err.println("pucntual " + pucntualStar);

			// leave
			List<EmployeeLeaveApplied> leaves = employeeLeaveAppliedRepository
					.findByStatusIgnoreCaseAndEmployeePersonalInfoEmployeeInfoIdAndStartDateBetween("APPROVED",
							employeeInfoId, LocalDate.of(date.getYear(), date.getMonthValue(), 1),
							LocalDate.of(date.getYear(), date.getMonthValue(), totMonth));

			Double leavez = leaves.stream().filter(x -> x.getStartDate() != null && x.getEndDate() != null)
					.map(leave -> {
						double dateBtn = ChronoUnit.DAYS.between(leave.getStartDate().minusDays(1), leave.getEndDate());
						if (leave.getStartTime() != null && leave.getEndTime() != null) {
							long hrBtn = ChronoUnit.HOURS.between(leave.getStartTime(), leave.getEndTime());
							if (hrBtn > 4)
								dateBtn += 0.5;
						}
						return dateBtn;
					}).reduce(0.0, (sum, y) -> sum + y);

			List<AttendanceDetails> attendanceDetails = attendance.orElseGet(EmployeeAttendanceDetails::new)
					.getAttendanceDetails();
			double noOfLeave = stars.getNoOfLeaveInMonth().multiply(BigDecimal.valueOf(totMonth)).doubleValue();
			double absDays = (date.lengthOfMonth() - monthDetails.getHolidays().size()
					- (attendanceDetails == null ? 0 : attendanceDetails.size()));

			double tot = stars.getNoOfLeaveInMonth().doubleValue() * (noOfLeave
					- stars.getNoOfLeaveInMonth().multiply(BigDecimal.valueOf(leavez)).doubleValue()
					- stars.getUnapprovedLeave().multiply(BigDecimal.valueOf(absDays < 0 ? 0 : absDays)).doubleValue())
					/ (noOfLeave);
			if (((Double) tot).toString().equalsIgnoreCase("NaN"))
				tot = 0;

			BigDecimal calStar = BigDecimal.valueOf(tot).setScale(2, RoundingMode.HALF_DOWN);
			System.err.println("leave " + calStar);

			// target performance
			List<ProjectTaskDetails> projectTaskDetails = projectTaskDetailsRepository
					.findByAssignedEmployeeAndCompanyIdAndAssignedDateBetween(employeeOfficialInfo.getEmployeeId(),
							companyId, LocalDate.of(date.getYear(), date.getMonthValue(), 1),
							LocalDate.of(date.getYear(), date.getMonthValue(), date.lengthOfMonth()));
			int size = projectTaskDetails.size();
			List<ProjectTargetPerformance> targetPerformance = new ArrayList<>();

			BigDecimal targetAchived = projectTaskDetails.stream().map(task -> Optional.ofNullable(task)
					.filter(t -> t.getStatus().equalsIgnoreCase("completed")).map(v -> {

						LocalDate assignedDate = v.getAssignedDate();
						double between = ChronoUnit.DAYS.between(assignedDate, v.getEndDate().toLocalDate());
						long per = (long) (((between - ChronoUnit.DAYS.between(assignedDate,
								v.getCompletedDate() == null ? assignedDate : v.getCompletedDate())) / between) * 100);

						Double proPerformance = Optional.ofNullable(per).filter(a -> a >= 100)
								.map(b -> stars.getAchievedMoreThen100Per().doubleValue() / size)
								.orElseGet(() -> per >= 50 ? stars.getAchievedMoreThen50Per().doubleValue() / size
										: stars.getTargetAchived().doubleValue() / size);
						BigDecimal target = BigDecimal.valueOf(proPerformance).setScale(2, RoundingMode.HALF_DOWN);
						targetPerformance.add(ProjectTargetPerformance.builder().projectId(v.getProjectId())
								.targetPerProject((double) ((long) (proPerformance * 100)) / 100 * size).build());
						return target;
					}).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, (v, sum) -> sum.add(v));

			System.err.println("targetAchive " + targetAchived);

			// activities performance
			Double activity = companyEventDetailsRepository
					.findByCompanyInfoCompanyIdAndEventDateBetween(companyId,
							LocalDate.of(date.getYear(), date.getMonthValue(), 1),
							LocalDate.of(date.getYear(), date.getMonthValue(), date.lengthOfMonth()))
					.stream().filter(emp -> emp.getEmployees().contains(employeeOfficialInfo.getEmployeeId()))
					.map(x -> Optional.ofNullable(x.getEmployees())
							.filter(e -> !x.getWinners().isEmpty() && x.getWinners() != null
									&& x.getWinners().contains(employeeOfficialInfo.getEmployeeId()))
							.map(emp -> 5.0 / emp.size()).orElse(0.0))
					.reduce(0.0, (sum, y) -> sum + y);
			BigDecimal activities = BigDecimal.valueOf(activity).setScale(2, RoundingMode.HALF_DOWN);
			System.err.println("activities " + activities);

			// ticket rating
			BigDecimal ticketSolvedRating = ticketSolvedRating(companyId, date, employeeInfoId, hrTicketsLength,
					accountTicketsLength, adminDeptTicketsLength, itTicketsLength);
			System.err.println(employeeInfoId + " ticketSolvedRating " + ticketSolvedRating);

			return EmployeePerformanceDTO.builder().leaves(calStar).targetAchived(targetAchived)
					.ticketSolveRating(ticketSolvedRating).projectTargetPerformances(targetPerformance)
					.punctual(pucntualStar).activities(activities).build();
		}).orElseGet(EmployeePerformanceDTO::new);
	}

	private EmployeePersonalInfo getEmployeePersonalInfo(Long employeeInfoId, Long companyId) {
		return Optional
				.of(employeePersonalInfoRepository.findByCompanyInfoCompanyIdAndEmployeeInfoId(companyId,
						employeeInfoId))
				.filter(x -> !x.isEmpty()).map(y -> y.get(0)).orElseGet(EmployeePersonalInfo::new);
	}

	private EmployeePunctualPerformanceDTO getMonthDetails(Long employeeInfoId, Long companyId, LocalDate date) {
		Integer month = date.getMonthValue();
		Integer year = date.getYear();
		EmployeePersonalInfo employeePersonalInfo = getEmployeePersonalInfo(employeeInfoId, companyId);

		CompanyInfo companyInfo = employeePersonalInfo.getCompanyInfo();
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();

		List<WorkOffDetails> workOffDetailsList;
		Map<Integer, List<String>> weekOffName;

		CompanyWorkWeekRule companyWorkWeekRule = Optional
				.ofNullable(employeeOfficialInfo.getCompanyWorkWeekRule() == null).filter(rule -> rule)
				.map(x -> Optional
						.ofNullable(companyInfo.getCompanyWorkWeekRuleList().stream()
								.filter(CompanyWorkWeekRule::getIsDefault).collect(Collectors.toList()))
						.filter(f -> !f.isEmpty()).map(y -> y.get(0)).orElseGet(CompanyWorkWeekRule::new))
				.orElse(employeeOfficialInfo.getCompanyWorkWeekRule());

		if (companyWorkWeekRule.getWorkOffDetailsList() == null) {
			return EmployeePunctualPerformanceDTO.builder().build();
		}

		workOffDetailsList = companyWorkWeekRule.getWorkOffDetailsList();
		weekOffName = workOffDetailsList.stream().collect(
				Collectors.toMap(WorkOffDetails::getWeekNumber, WorkOffDetails::getFullDayWorkOff, (k1, k2) -> k1));

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
		holidayList.addAll(weekOffDetails);
		holidayList.addAll(leaveDetails);
		return EmployeePunctualPerformanceDTO.builder().holidays(holidayList).leaveDetails(leaveDetails).build();
	}

	private BigDecimal ticketSolvedRating(Long companyId, LocalDate date, Long employeeInfoId, int hrTicketsLength,
			int accountTicketsLength, int adminDeptTicketsLength, int itTicketsLength) {
		EmployeePersonalInfo employeePersonalInfo = getEmployeePersonalInfo(employeeInfoId, companyId);
		if (employeePersonalInfo == null || employeePersonalInfo.getEmployeeInfoId() == null
				|| employeePersonalInfo.getEmployeeOfficialInfo().getDepartment() == null)
			return null;

		if (employeePersonalInfo.getEmployeeOfficialInfo().getDepartment().equalsIgnoreCase("IT")) {
			Integer companyItTicketsCount = companyItTicketsRepository
					.findByCompanyIdAndTicketHistroysByAndTicketHistroysStatus(companyId,
							employeePersonalInfo.getEmployeeInfoId(), RESOLVED)
					.stream()
					.filter(i -> i != null && i.getTicketHistroys() != null && !i.getTicketHistroys().isEmpty())
					.map(it -> {
						List<TicketHistroy> ticketHistroys = it.getTicketHistroys();
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						return Optional
								.ofNullable(ticketHistroy.getDate().getYear() == date.getYear()
										&& ticketHistroy.getDate().getMonthValue() == date.getMonthValue()
										&& ticketHistroy.getStatus().equalsIgnoreCase(RESOLVED))
								.filter(t -> t).map(d -> 1).orElse(0);
					}).reduce(0, (x, y) -> x + y);
			return BigDecimal.valueOf((double) companyItTicketsCount * 5 / itTicketsLength).setScale(2,
					RoundingMode.HALF_DOWN);
		} else if (employeePersonalInfo.getEmployeeOfficialInfo().getDepartment().equalsIgnoreCase("HR")) {
			Integer companyHrTicketsCount = companyHrTicketsRepository
					.findByCompanyIdAndTicketHistroysByAndTicketHistroysStatus(companyId,
							employeePersonalInfo.getEmployeeInfoId(), RESOLVED)
					.stream()
					.filter(i -> i != null && i.getTicketHistroys() != null && !i.getTicketHistroys().isEmpty())
					.map(it -> {
						List<TicketHistroy> ticketHistroys = it.getTicketHistroys();
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						return Optional
								.ofNullable(ticketHistroy.getDate().getYear() == date.getYear()
										&& ticketHistroy.getDate().getMonthValue() == date.getMonthValue()
										&& ticketHistroy.getStatus().equalsIgnoreCase(RESOLVED))
								.filter(t -> t).map(d -> 1).orElse(0);
					}).reduce(0, (x, y) -> x + y);
			return BigDecimal.valueOf((double) companyHrTicketsCount * 5 / hrTicketsLength).setScale(2,
					RoundingMode.HALF_DOWN);
		} else if (employeePersonalInfo.getEmployeeOfficialInfo().getDepartment().equalsIgnoreCase("ACCOUNTS")) {
			Integer companyAccountTicketsCount = companyAccountTicketsRepository
					.findByCompanyIdAndTicketHistroysByAndTicketHistroysStatus(companyId,
							employeePersonalInfo.getEmployeeInfoId(), RESOLVED)
					.stream()
					.filter(i -> i != null && i.getTicketHistroys() != null && !i.getTicketHistroys().isEmpty())
					.map(it -> {
						List<TicketHistroy> ticketHistroys = it.getTicketHistroys();
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						return Optional
								.ofNullable(ticketHistroy.getDate().getYear() == date.getYear()
										&& ticketHistroy.getDate().getMonthValue() == date.getMonthValue()
										&& ticketHistroy.getStatus().equalsIgnoreCase(RESOLVED))
								.filter(t -> t).map(d -> 1).orElse(0);
					}).reduce(0, (x, y) -> x + y);
			return BigDecimal.valueOf((double) companyAccountTicketsCount * 5 / accountTicketsLength).setScale(2,
					RoundingMode.HALF_DOWN);
		} else if (employeePersonalInfo.getEmployeeOfficialInfo().getDepartment()
				.equalsIgnoreCase("ADMIN DEPARTMENT")) {
			Integer companyAdminDeptTicketsCount = companyAdminDeptTicketsRepo
					.findByCompanyIdAndTicketHistroysByAndTicketHistroysStatus(companyId,
							employeePersonalInfo.getEmployeeInfoId(), RESOLVED)
					.stream()
					.filter(i -> i != null && i.getTicketHistroys() != null && !i.getTicketHistroys().isEmpty())
					.map(it -> {
						List<TicketHistroy> ticketHistroys = it.getTicketHistroys();
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						return Optional
								.ofNullable(ticketHistroy.getDate().getYear() == date.getYear()
										&& ticketHistroy.getDate().getMonthValue() == date.getMonthValue()
										&& ticketHistroy.getStatus().equalsIgnoreCase(RESOLVED))
								.filter(t -> t).map(d -> 1).orElse(0);
					}).reduce(0, (x, y) -> x + y);
			return BigDecimal.valueOf((double) companyAdminDeptTicketsCount * 5 / adminDeptTicketsLength).setScale(2,
					RoundingMode.HALF_DOWN);
		} else {
			return null;
		}
	}

	private final String inr = "INR";

	public ConversionRateDTO moneyRates() {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");

		Map<String, String> params = new HashMap<>();
		params.put("apikey", "05745ee937ca49abacae60ba11cc1c72");

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

		String url = "https://api.currencyfreaks.com/latest?apikey=05745ee937ca49abacae60ba11cc1c72";

		ResponseEntity<ConversionRateDTO> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity,
				ConversionRateDTO.class);
		return exchange.getBody();
	}

	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Kolkata")
	public void moneyConversion() {
		log.info("Scheduler started with method name money conversion");
		ConversionRateDTO moneyRates = moneyRates();
		if (moneyRates == null) {
			return;
		}
		Map<String, BigDecimal> rates = moneyRates.getRates();

		rates.put(moneyRates.getBase(), BigDecimal.ONE);
		moneyRates.setBase(this.inr);

//		List<String> currencyCode = Currency.getAvailableCurrencies().stream().filter(cc->countryCodes.contains(cc.getCurrencyCode())).map(Currency::getCurrencyCode)
//				.collect(Collectors.toList());

		List<CurrencyDetails> collect = rates.entrySet().stream().filter(cc -> countryCodes.contains(cc.getKey()))
				.map(r -> {
					Currency curr = Currency.getInstance(r.getKey());
					String symbol = curr.getSymbol();
					symbol.substring(symbol.length() - 1);
					return CurrencyDetails.builder()
							.amountDifference(
									BigDecimal.valueOf(r.getValue().doubleValue() / rates.get(this.inr).doubleValue()))
							.displayName(curr.getDisplayName()).currencyCode(r.getKey())
							.currencySymbol(symbol.substring(symbol.length() - 1)).build();
				}).collect(Collectors.toList());

		currencyConvertRepository.deleteAll();

		currencyConvertRepository
				.save(CurrencyConvert.builder().currency(Map.of(moneyRates.getBase(), collect)).build());
	}

	@Scheduled(cron = "0 55 23 * * *", zone = "Asia/Kolkata")
	@Transactional
	public void updateEmployeeStatus() {
		List<CompanyEmployeeResignationDetails> employeeList = employeeResignationRequestRepository.findAll();
		for (CompanyEmployeeResignationDetails employee : employeeList) {
			if (employee.getNoticePeriodStartDate() != null && employee.getNoticePeriodDuration() != null) {
				Double duration = employee.getNoticePeriodDuration().doubleValue();

				String string = duration.toString();
				String[] split = string.split("\\.");
				Integer months = Integer.parseInt(split[0]);

				LocalDate localDate = employee.getNoticePeriodStartDate();
				LocalDate plusMonths = localDate.plusMonths(months);
				int days = plusMonths.getMonth().length(plusMonths.isLeapYear()) * Integer.parseInt(split[1]) / 10;
				LocalDate exitDate = plusMonths.plusDays(days);
				if (LocalDate.now().equals(exitDate)) {
					EmployeePersonalInfo employeePersonalInfo = employee.getEmployeePersonalInfo();
					this.userId = employeePersonalInfo.getEmployeeInfoId();
					Map<String, String> status = employeePersonalInfo.getStatus() == null
							|| employeePersonalInfo.getStatus().isEmpty() ? new LinkedHashMap<>()
									: employeePersonalInfo.getStatus();
					status.remove(ABSCONDED);
					status.remove(TERMINATED);
					String resignationReason = employee.getResignationReason().replace(',', '#');
					status.put(RESIGNED, resignationReason);
					employeePersonalInfo.setIsActive(false);
					employeePersonalInfo.setStatus(status);
				}
			}
		}
	}

	@Scheduled(cron = "0 55 23 * * *", zone = "Asia/Kolkata")
	@Transactional
	public void deleteEmployeeNotification() {
		LocalDate now = LocalDate.now();
		List<EmployeeNotification> notificationList = employeeNotificationRepository.findByIsSeenTrue().stream()
				.filter(notification -> now.isAfter(notification.getCreatedDate().toLocalDate().plusDays(7)))
				.collect(Collectors.toList());
		if (!notificationList.isEmpty()) {
			employeeNotificationRepository.deleteAll(notificationList);
		}
	}

	@Scheduled(cron = "0 5 0 * * *", zone = "Asia/Kolkata")
	@Transactional
	public Boolean calculateAllEmployeeSalary() {

		LocalDate now = LocalDate.now();

		List<CompanyPayrollInfo> payrollList = companyPayRollInfoRepository
				.findBySalaryApprovalDate(now.getDayOfMonth());
		
		List<Long> companyIds = payrollList.stream().map(x -> x.getCompanyInfo().getCompanyId())
				.collect(Collectors.toList());

		List<EmployeeSalaryDetails> listOfEmployeeSalaryDetails = employeeSalaryDetailsRepo
				.findByMonthAndYearAndCompanyInfoCompanyIdIn(now.getMonthValue(), now.getYear(), companyIds);

		List<Long> employeeInfoIds = listOfEmployeeSalaryDetails.stream()
				.map(x -> x.getEmployeePersonalInfo().getEmployeeInfoId()).collect(Collectors.toList());


		List<EmployeeAnnualSalary> employeeAnnualSalaryList = employeeAnnualSalaryRepo
				.findByCompanyPayrollInfoInAndEmployeePersonalInfoEmployeeInfoIdNotIn(payrollList, employeeInfoIds);

		Month currMonth = now.getMonth();
		int currMonthValue = now.getMonthValue();
		int currYear = now.getYear();

		List<EmployeeSalaryDetails> employeeSalaryDetailsList = new ArrayList<>();

		// Calculate salary for all employee one by one
		employeeAnnualSalaryList.forEach(empAnnualSalary -> {

			EmployeePersonalInfo employeelInfo = empAnnualSalary.getEmployeePersonalInfo();

			userId = employeelInfo.getEmployeeInfoId();

			// Check if salary disabled
			if (Boolean.TRUE.equals(empAnnualSalary.getIsSalaryStopped())
					&& empAnnualSalary.getSalaryStoppedFrom().getMonth() == currMonth) {
				return;
			}

			EmployeeSalaryDetails employeeSalaryDetails = new EmployeeSalaryDetails();

			BigDecimal yearlyCTC = empAnnualSalary.getAnnualSalary();
			Map<String, String> deductions = new HashMap<>();
			Map<String, String> earnings = new HashMap<>();
			Map<String, String> additional = new HashMap<>();
			// check for variable pay
			BigDecimal totalSalary = ZERO;
			BigDecimal netPay = totalSalary;
			// Variable Pay
			if (Boolean.TRUE.equals(empAnnualSalary.getIsPayEnabled())) {
				EmployeeVariablePay variablePay = empAnnualSalary.getEmployeeVariablePay();
				yearlyCTC = yearlyCTC.subtract(variablePay.getAmount());
				List<String> variablePayMonthList = variablePay.getEffectiveMonth();

				for (String monthValue : variablePayMonthList) {
					if (currMonthValue == Integer.parseInt(monthValue)) {
						BigDecimal ammount = variablePay.getAmount().divide(new BigDecimal(variablePayMonthList.size()),
								RoundingMode.HALF_EVEN);
						totalSalary = totalSalary.add(ammount);
						additional.put(variablePay.getDescription(),
								ammount.setScale(2, RoundingMode.HALF_EVEN).toString());
					}
				}
			}

			BigDecimal monthlyCTC = yearlyCTC.divide(new BigDecimal(12), RoundingMode.HALF_EVEN);

			if (empAnnualSalary.getCompanyPayrollInfo() == null) {
				log.error("Error! Unable to find payroll configuration for the employee Id: "
						+ employeelInfo.getEmployeeInfoId() + "Company Id:"
						+ employeelInfo.getCompanyInfo().getCompanyId());

			}
			List<CompanyPayrollEarning> earningList = empAnnualSalary.getCompanyPayrollInfo()
					.getCompanyPayrollEarningList();
			// Earning
			for (CompanyPayrollEarning e : earningList) {
				BigDecimal earningAmount = ZERO;
				if (e.getType().equalsIgnoreCase(PERCENTAGE)) {
					earningAmount = monthlyCTC.multiply(e.getValue()).divide(ONE_HUNDRED);
				} else if (e.getType().equalsIgnoreCase(FIXED)) {
					earningAmount = e.getValue();
				}
				totalSalary = totalSalary.add(earningAmount);
				earnings.put(e.getSalaryComponent(), earningAmount.setScale(2, RoundingMode.HALF_EVEN).toString());
			}

			if (Boolean.TRUE.equals(empAnnualSalary.getIsBonusEnabled())) {
				for (EmployeeBonus b : empAnnualSalary.getEmployeeBonusList()) {
					if (b.getEffectiveDate().getMonth() == currMonth && b.getEffectiveDate().getYear() == currYear) {
						totalSalary = totalSalary.add(b.getAmount());
						additional.put(b.getReason(), b.getAmount().setScale(2, RoundingMode.HALF_EVEN).toString());
					}
				}
			}

			List<EmployeeReimbursementInfo> employeeReimbursementInfos = employeelInfo
					.getEmployeeReimbursementInfoList();

			if (employeeReimbursementInfos != null && !employeeReimbursementInfos.isEmpty()) {
				for (EmployeeReimbursementInfo rInfo : employeeReimbursementInfos) {
					if (rInfo.getStatus().equalsIgnoreCase(STATUS_APPROVED)) {
						totalSalary = totalSalary.add(rInfo.getAmount());
						additional.put(REIMBURSEMENT + rInfo.getDescription(),
								rInfo.getAmount().setScale(2, RoundingMode.HALF_EVEN).toString());
					}
				}
			}

			List<EmployeeReferenceInfo> employeeReferenceInfo = employeelInfo.getEmployeeReferenceInfoList();
			BigDecimal rewards = ZERO;

			for (EmployeeReferenceInfo refInfo : employeeReferenceInfo) {
				if (refInfo.getRefferalEmployeePersonalInfo() != null && refInfo.getRewardAmount() != null
						&& refInfo.getIsIncludedInSalary() == Boolean.FALSE) {
					rewards = rewards.add(refInfo.getRewardAmount());
					refInfo.setIsIncludedInSalary(Boolean.TRUE);
				}
			}
			if (rewards != ZERO) {
				additional.put(REWARDS, rewards.toString());
				totalSalary = totalSalary.add(rewards);
				employeelInfo.setEmployeeReferenceInfoList(employeeReferenceInfo);
			}
			netPay = totalSalary;

			List<CompanyPayrollDeduction> deductionList = empAnnualSalary.getCompanyPayrollInfo()
					.getCompanyPayrollDeductionList();

			for (CompanyPayrollDeduction d : deductionList) {
				BigDecimal deductionAmount = ZERO;
				if (d.getType().equalsIgnoreCase(PERCENTAGE)) {
					deductionAmount = monthlyCTC.multiply(d.getValue()).divide(ONE_HUNDRED);
				} else if (d.getType().equalsIgnoreCase(FIXED)) {
					deductionAmount = d.getValue();
				}
				netPay = netPay.subtract(deductionAmount);
				deductions.put(d.getTitle(), deductionAmount.setScale(2, RoundingMode.HALF_EVEN).toString());
			}

			employeeSalaryDetails.setEmployeePersonalInfo(employeelInfo);
			employeeSalaryDetails.setDeduction(deductions);
			employeeSalaryDetails.setEarning(earnings);
			employeeSalaryDetails.setAdditional(additional);
			employeeSalaryDetails.setMonth(currMonthValue);
			employeeSalaryDetails.setYear(currYear);
			employeeSalaryDetails.setTotalSalary(totalSalary);
			employeeSalaryDetails.setNetPay(netPay);
			employeeSalaryDetails.setCompanyInfo(employeelInfo.getCompanyInfo());
			employeeSalaryDetails.setIsPaid(Boolean.FALSE);
			employeeSalaryDetails.setIsFinalized(Boolean.FALSE);
			employeeSalaryDetails.setIsPayslipGenerated(Boolean.FALSE);

			employeeSalaryDetailsList.add(employeeSalaryDetails);
		});

		employeeSalaryDetailsRepo.saveAll(employeeSalaryDetailsList);

		return Boolean.TRUE;
	}

	@Scheduled(cron = "0 5 0 * * *", zone = "Asia/Kolkata")
	@Transactional
	public Boolean calculateReviseSalary() {
		LocalDate currDate = LocalDate.now();
		List<CompanyInfo> companyInfoList = companyInfoRepository.findAll();
		List<EmployeeReviseSalary> employeeReviseSalaries = new ArrayList<>();

		companyInfoList.stream().forEach(company -> {
			if (company.getCompanyRuleInfo() == null || company.getCompanyRuleInfo().getApprisalCycle() == null) {
				log.error("No apprisal configuration for company Id: " + company.getCompanyId());
				return;
			}
			Integer apprisalCycle = Integer.parseInt(company.getCompanyRuleInfo().getApprisalCycle());

			company.getEmployeePersonalInfoList().stream().forEach(employee -> {
				userId = employee.getEmployeeInfoId();
				LocalDate lastRevisionDate;
				List<EmployeeReviseSalary> pastReviseSalaries = employee.getEmployeeReviseSalaryList();
				if (pastReviseSalaries == null || pastReviseSalaries.isEmpty()) {
					if (employee.getEmployeeOfficialInfo() == null) {
						lastRevisionDate = null;
					} else {
						lastRevisionDate = employee.getEmployeeOfficialInfo().getDoj();
					}
				} else {
					lastRevisionDate = pastReviseSalaries.get(pastReviseSalaries.size() - 1).getRevisedDate();
				}
				if (lastRevisionDate == null) {
					log.error("Unable to find last revise date for employee Id: " + employee.getEmployeeInfoId());
					return;
				}

				if (lastRevisionDate.plusMonths(apprisalCycle).minusDays(7).compareTo(currDate) != 0) {
					return;
				}
				EmployeeReviseSalary employeeReviseSalary = new EmployeeReviseSalary();
				employeeReviseSalary.setApprisalDate(currDate.plusDays(7));
				employeeReviseSalary.setApprisalDate(lastRevisionDate.plusMonths(apprisalCycle));
				employeeReviseSalary.setEmployeePersonalInfo(employee);
				employeeReviseSalary.setCompanyInfo(company);
				employeeReviseSalaries.add(employeeReviseSalary);
			});
		});
		employeeReviseSalaryRepository.saveAll(employeeReviseSalaries);
		return Boolean.TRUE;

	}

	@Scheduled(cron = "0 5 0 * * *", zone = "Asia/Kolkata")
	@Transactional
	public void notification() {
		List<EmployeePersonalInfo> employeeList = employeePersonalInfoRepository.findAll();

		List<EmployeePersonalInfo> inProgressEmployees = employeeList.stream()
				.filter(employee -> (employee.getStatus() == null || (employee.getStatus().get("rejectedBy") == null
						&& employee.getStatus().get("approvedBy") == null))
						&& employee.getCreatedDate().toLocalDate().plusDays(14).equals(LocalDate.now()))
				.collect(Collectors.toList());

		Map<CompanyInfo, List<EmployeePersonalInfo>> companyHRs = employeeList.stream()
				.filter(employee -> employee.getEmployeeOfficialInfo() != null
						&& employee.getEmployeeOfficialInfo().getDepartment() != null
						&& employee.getEmployeeOfficialInfo().getDepartment().equalsIgnoreCase("HR"))
				.collect(Collectors.groupingBy(EmployeePersonalInfo::getCompanyInfo));

		for (EmployeePersonalInfo employeePersonalInfo : inProgressEmployees) {
			List<EmployeePersonalInfo> companyHR = companyHRs.get(employeePersonalInfo.getCompanyInfo());
			if (companyHR != null && !companyHR.isEmpty()) {
				List<EmployeeNotification> notifications = companyHR.stream().map(hr -> {
					EmployeeNotification employeeNotification = new EmployeeNotification();
					employeeNotification.setDescription("Verification is pending for "
							+ employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
					employeeNotification.setReceiverEmployeePersonalInfo(hr);
					employeeNotification.setIsSeen(Boolean.FALSE);
					return employeeNotification;
				}).collect(Collectors.toList());
				userId = employeePersonalInfo.getEmployeeInfoId();
				employeeNotificationRepository.saveAll(notifications);
			}
		}
	}

	public Long getUserId() {
		return this.userId;
	}
}