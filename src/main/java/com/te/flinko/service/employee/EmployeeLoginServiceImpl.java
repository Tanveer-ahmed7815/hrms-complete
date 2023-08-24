package com.te.flinko.service.employee;

import static com.te.flinko.common.employee.EmployeeLoginConstants.CURRENT_PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCHED;
import static com.te.flinko.common.employee.EmployeeLoginConstants.EMPLYOEE_DOES_NOT_EXIST;
import static com.te.flinko.common.employee.EmployeeLoginConstants.INVALID_OTP;
import static com.te.flinko.common.employee.EmployeeLoginConstants.OLD_PASSWORD_AND_CURRENT_PASSWORD_DOES_NOT_MATCHED;
import static com.te.flinko.common.employee.EmployeeLoginConstants.OLD_PASSWORD_AND_NEW_PASSWORD_IS_SAME;
import static com.te.flinko.common.employee.EmployeeLoginConstants.OTP_SEND_TO_YOUR_RESPECTIVE_EMAIL;
import static com.te.flinko.common.employee.EmployeeLoginConstants.OTP_SEND_TO_YOUR_RESPECTIVE_EMAIL_AND_MOBILE_NUMBER;
import static com.te.flinko.common.employee.EmployeeLoginConstants.OTP_SEND_TO_YOUR_RESPECTIVE_MOBILE_NUMBER;
import static com.te.flinko.common.employee.EmployeeLoginConstants.RESET_PASSWORD_WITH_EMPLOYEE_ID;
import static com.te.flinko.common.employee.EmployeeLoginConstants.SOMETHING_WENT_WRONG;
import static com.te.flinko.common.employee.EmployeeLoginConstants.SUCCESSFULLY_LOGGED_IN;
import static com.te.flinko.common.employee.EmployeeLoginConstants.SUCCESSFULLY_UPDATE_PASSWORD;
import static com.te.flinko.common.employee.EmployeeLoginConstants.VALID_OTP;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.SESSION_EXPIRED_FORGOT;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.SESSION_TIME_EXPIRED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.employee.EmployeeCapabilityDTO;
import com.te.flinko.dto.employee.EmployeeIdDto;
import com.te.flinko.dto.employee.EmployeeLoginDto;
import com.te.flinko.dto.employee.EmployeeLoginResponseDto;
import com.te.flinko.dto.employee.MailDto;
import com.te.flinko.dto.employee.NewConfirmPasswordDto;
import com.te.flinko.dto.employee.ResetPasswordDto;
import com.te.flinko.dto.employee.VerifyOTPDto;
import com.te.flinko.entity.admin.CompanyAddressInfo;
import com.te.flinko.entity.admin.CompanyBranchInfo;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyRuleInfo;
import com.te.flinko.entity.admin.CompanyShiftInfo;
import com.te.flinko.entity.employee.EmployeeLoginInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.AttendanceDetails;
import com.te.flinko.entity.employee.mongo.EmployeeAttendanceDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.superadmin.PaymentDetails;
import com.te.flinko.entity.superadmin.PlanDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.exception.employee.EmployeeLoginException;
import com.te.flinko.exception.employee.EmployeeNotRegisteredException;
import com.te.flinko.repository.admin.DepartmentInfoRepository;
import com.te.flinko.repository.employee.EmployeeLoginInfoRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeReportingInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeAttendanceDetailsRepository;
import com.te.flinko.repository.project.ProjectDetailsRepository;
import com.te.flinko.service.mail.employee.EmailService;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.service.notification.employee.PushNotificationService;
import com.te.flinko.service.sms.employee.SmsService;
import com.te.flinko.util.CacheStore;
import com.te.flinko.util.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * @author Sahid
 *
 */

@Service
@RequiredArgsConstructor
public class EmployeeLoginServiceImpl extends BaseConfigController implements EmployeeLoginService {

	private static final String PASSWORD_UPDATED = "Password Updated";
	private static final String EMPLOYEE_MANAGEMENT = "EMPLOYEE MANAGEMENT";
	private static final String REPORTING_MANAGER = "REPORTING MANAGER";
	private static final String HELP_AND_SUPPORT = "HELP AND SUPPORT";

	private static final String YOUR_ACCOUNT_IS_NOT_ACTIVATE_PLEASE_CONTACT_ADMIN_OR_HR = "Your Account Is Inactive Please Contact Admin Or HR!!!";

	private final EmployeeLoginInfoRepository employeeLoginRepository;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final EmployeeAttendanceDetailsRepository employeeAttendanceDetailsRepository;

	private final ProjectDetailsRepository projectDetailsRepository;

	private final CacheStore<EmployeeLoginInfo> cacheStoreEmployeeLogin;

	private final DepartmentInfoRepository departmentInfoRepository;

	private final EmployeeReportingInfoRepository employeeReportingInfoRepository;

	private final CacheStore<Long> cacheStoreOTP;

	private final CacheStore<Boolean> cacheStoreValidOTP;

	private final PasswordEncoder passwordEncoder;

	private final EmailService emailService;

	private final SmsService smsService;

	private final InAppNotificationServiceImpl notificationServiceImpl;

	private final PushNotificationService pushNotificationService;

	private Optional<String> optional = Optional.of("optional");

	private List<EmployeeCapabilityDTO> roles;

	private final JwtUtil jwtService;

	List<String> lists = List.of("Daily Timesheet", "Weekly Timesheet", "Monthly Timesheet");
	String msg;

	@Override
	public EmployeeLoginResponseDto login(EmployeeLoginDto employeeLoginDto, HttpServletRequest request) {
		msg = null;
		EmployeeLoginInfo employeeLoginInfo = employeeLoginRepository
				.findByEmployeeIdAndEmployeePersonalInfoIsActiveTrueAndEmployeePersonalInfoCompanyInfoIsActiveTrueAndEmployeePersonalInfoCompanyInfoCompanyCode(
						employeeLoginDto.getEmployeeId(), employeeLoginDto.getCompanyCode())
				.orElseThrow(() -> new DataNotFoundException(YOUR_ACCOUNT_IS_NOT_ACTIVATE_PLEASE_CONTACT_ADMIN_OR_HR));
		EmployeePersonalInfo info = employeeLoginInfo.getEmployeePersonalInfo();

		CompanyInfo companyInfo = Optional.ofNullable(info.getCompanyInfo())
				.filter(x -> x.getIsActive() != null && x.getIsActive().booleanValue())
				.map(y -> Optional.ofNullable(y)
						.filter(z -> z.getCompanyCode().equals(employeeLoginDto.getCompanyCode()))
						.orElseThrow(() -> new EmployeeLoginException("Company Code Is Incorrect")))
				.orElseThrow(() -> new EmployeeLoginException("Company Is Inactive"));

		CompanyRuleInfo companyRuleInfo = companyInfo == null ? null : companyInfo.getCompanyRuleInfo();

// geo location validation
//		isValidRedius(employeeLoginDto,
//				employeeLoginInfo.getEmployeePersonalInfo().getCompanyInfo().getCompanyBranchInfoList());

		roles = new ArrayList<>();
		if (employeeLoginInfo.getRoles() != null && !employeeLoginInfo.getRoles().equals("{}"))
			roles = BeanCopy.objectProperties(employeeLoginInfo.getRoles(),
					new TypeReference<List<EmployeeCapabilityDTO>>() {
					});
		if (employeeLoginInfo.getRoles() != null && (employeeLoginInfo.getRoles().equals("{}")
				|| employeeLoginInfo.getRoles().toString().equalsIgnoreCase("[]")))
			msg = "Role Is Not Set To The Employee.";

		List<PaymentDetails> paymentDetailsList = info.getCompanyInfo().getPaymentDetailsList();

		List<EmployeeCapabilityDTO> userLoggedInRoles = Optional.ofNullable(paymentDetailsList)
				.filter(x -> !x.isEmpty()).map(plan -> {
					PaymentDetails paymentDetails = plan.get(plan.size() - 1);
					PlanDetails planDetails = paymentDetails.getPlanDetails();
					int monthDiff = LocalDate.now().getMonthValue()
							- paymentDetails.getLastModifiedDate().getMonthValue();

					if (planDetails.getDuration().doubleValue() <= monthDiff)
						return new ArrayList<EmployeeCapabilityDTO>();

					List<EmployeeCapabilityDTO> employeeCapabilityDTOs = Optional.ofNullable(planDetails)
							.filter(x -> x.getPlanName().equalsIgnoreCase("PLATINUM")).map(v -> {
								if (msg != null)
									msg += " Only Default Role For The Company Is Set For The Employee";
								List<String> departmentNames = employeeReportingInfoRepository
										.findByReportingManagerEmployeeInfoIdAndEmployeePersonalInfoNotNull(
												info.getEmployeeInfoId())
										.filter(List::isEmpty)
										.map(dept -> List.of(EMPLOYEE_MANAGEMENT, HELP_AND_SUPPORT))
										.orElseGet(() -> List.of(EMPLOYEE_MANAGEMENT, REPORTING_MANAGER,
												HELP_AND_SUPPORT));

								Map<String, List<EmployeeCapabilityDTO>> collect = departmentInfoRepository
										.findByDepartmentNameIgnoreCaseIn(departmentNames).filter(d -> !d.isEmpty())
										.orElseThrow().stream().map(dept -> {
											List<EmployeeCapabilityDTO> objectProperties = BeanCopy.objectProperties(
													dept.getRoles(), new TypeReference<List<EmployeeCapabilityDTO>>() {
													});
											return Map.of(dept.getDepartmentName(), objectProperties);
										}).flatMap(x -> x.entrySet().stream())
										.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (k1, k2) -> k2));
								List<EmployeeCapabilityDTO> list = collect.get(EMPLOYEE_MANAGEMENT);
								List<ProjectDetails> projectList = projectDetailsRepository
										.findByEmployeePersonalInfoListEmployeeInfoId(info.getEmployeeInfoId());
								if (projectList.isEmpty()) {
									list = list.stream().map(x -> Optional.ofNullable(x)
											.filter(p -> p.getCapabilityType().equalsIgnoreCase("Employee Dashboard"))
											.map(r -> {
												r.setIsEnable(Boolean.FALSE);
												return r;
											}).orElseGet(() -> x)).collect(Collectors.toList());
								}

								Boolean isChatBoxEnabled = companyRuleInfo != null
										&& companyRuleInfo.getIsChatBoxEnabled() != null
												? companyRuleInfo.getIsChatBoxEnabled().booleanValue()
												: Boolean.FALSE;

								List<EmployeeCapabilityDTO> helpAndSupport = collect.get(HELP_AND_SUPPORT).stream()
										.map(chat -> {
											List<EmployeeCapabilityDTO> chlidCap = chat.getChildCapabilityNameList()
													.stream()
													.map(r -> Optional
															.ofNullable(r).filter(c -> !isChatBoxEnabled && c
																	.getCapabilityType().equalsIgnoreCase("Chat Box"))
															.map(rol -> {
																rol.setIsEnable(false);
																return rol;
															}).orElse(r))
													.collect(Collectors.toList());
											chat.setChildCapabilityNameList(chlidCap);
											return chat;
										}).collect(Collectors.toList());

								return departmentNames.size() > 2
										? List.of(roles, list, collect.get(REPORTING_MANAGER), helpAndSupport).stream()
												.flatMap(Collection::stream).collect(Collectors.toList())
										: List.of(roles, list, helpAndSupport).stream().flatMap(Collection::stream)
												.collect(Collectors.toList());
							}).orElseGet(() -> roles);
					List<String> timeSheet = new ArrayList<>(lists);
					if (companyInfo.getCompanyRuleInfo() != null) {
						timeSheet.remove(companyInfo.getCompanyRuleInfo().getTimeSheetApproval() + " Timesheet");
					}
					return employeeCapabilityDTOs.stream().map(t -> Optional.ofNullable(t)
							.filter(tm -> timeSheet.contains(tm.getCapabilityType())).map(s -> {
								s.setIsEnable(Boolean.FALSE);
								return s;
							}).orElse(t)).collect(Collectors.toList());
				}).orElseGet(List::of);
		var jwtToken = jwtService.generateToken(employeeLoginInfo, getTerminalId());
		return Optional.ofNullable(employeeLoginInfo).map(employeeLogedin -> {
			EmployeeOfficialInfo employeeOfficialInfo = info.getEmployeeOfficialInfo();
			return EmployeeLoginResponseDto.builder().companyId(info.getCompanyInfo().getCompanyId())
					.employeeId(employeeOfficialInfo.getEmployeeId()).employeeInfoId(info.getEmployeeInfoId())
					.name(info.getFirstName() + " " + info.getLastName())
					.designation(employeeOfficialInfo.getDesignation()).logo(info.getCompanyInfo().getCompanyLogoUrl())
					.accessToken(jwtToken[0]).refreshToken(jwtToken[1]).department(employeeOfficialInfo.getDepartment())
					.msg(msg != null ? msg
							: Optional.ofNullable(paymentDetailsList.isEmpty()).filter(x -> x)
									.map(y -> "Plan Not Yet Subscribed")
									.orElseGet(() -> Optional.ofNullable(userLoggedInRoles.isEmpty()).filter(x -> x)
											.map(y -> "Plan Is Expired").orElseGet(() -> SUCCESSFULLY_LOGGED_IN)))
					.roles(userLoggedInRoles)
					.dateFormat(companyRuleInfo == null ? null : companyRuleInfo.getDateFormate()).build();
		}).orElseThrow(() -> new EmployeeLoginException(EMPLYOEE_DOES_NOT_EXIST));

	}

	@Override
	public String forgotPassword(EmployeeIdDto employeeIdDto) {

		if (employeeLoginRepository
				.findByEmployeePersonalInfoEmployeeOfficialInfoOfficialEmailIdAndEmployeePersonalInfoIsActive(
						employeeIdDto.getEmailId(), Boolean.FALSE)
				.isPresent())
			throw new DataNotFoundException(YOUR_ACCOUNT_IS_NOT_ACTIVATE_PLEASE_CONTACT_ADMIN_OR_HR);

		EmployeeLoginInfo employeeLoginInfo = employeeLoginRepository
				.findByEmployeePersonalInfoEmployeeOfficialInfoOfficialEmailIdAndEmployeePersonalInfoIsActive(
						employeeIdDto.getEmailId(), Boolean.TRUE)
				.orElseThrow(() -> new EmployeeNotRegisteredException(EMPLYOEE_DOES_NOT_EXIST));

		if (cacheStoreEmployeeLogin.get(employeeIdDto.getEmailId()) != null)
			cacheStoreEmployeeLogin.invalidate(employeeIdDto.getEmailId());
		cacheStoreEmployeeLogin.add(employeeIdDto.getEmailId(), employeeLoginInfo);

		return sendOtp(employeeIdDto.getEmailId(),
				employeeLoginInfo.getEmployeePersonalInfo().getEmployeeOfficialInfo().getOfficialEmailId(),
				employeeLoginInfo.getEmployeePersonalInfo().getMobileNumber(),
				employeeLoginInfo.getEmployeePersonalInfo().getFirstName() + " "
						+ employeeLoginInfo.getEmployeePersonalInfo().getLastName());

	}

	@Override
	public String resendOTP(EmployeeIdDto employeeIdDto) {
		EmployeeLoginInfo employeeLoginInfo = Optional
				.ofNullable(cacheStoreEmployeeLogin.get(employeeIdDto.getEmailId()))
				.orElseThrow(() -> new DataNotFoundException(SESSION_TIME_EXPIRED));
		return Objects.nonNull(employeeLoginInfo)
				? sendOtp(employeeIdDto.getEmailId(),
						employeeLoginInfo.getEmployeePersonalInfo().getEmployeeOfficialInfo().getOfficialEmailId(),
						employeeLoginInfo.getEmployeePersonalInfo().getMobileNumber(),
						employeeLoginInfo.getEmployeePersonalInfo().getFirstName() + " "
								+ employeeLoginInfo.getEmployeePersonalInfo().getLastName())
				: SESSION_TIME_EXPIRED;
	}

	@Override
	public String validateOTP(VerifyOTPDto verifyOTPDto) {
		EmployeeLoginInfo employeeLoginInfo = cacheStoreEmployeeLogin.get(verifyOTPDto.getEmailId());
		Boolean valiedEmployee = Objects.nonNull(employeeLoginInfo) ? Boolean.TRUE : Boolean.FALSE;
		Boolean valiedOTP = Optional.ofNullable(cacheStoreOTP.get(verifyOTPDto.getEmailId()))
				.orElseThrow(() -> new DataNotFoundException(SESSION_TIME_EXPIRED)).equals(verifyOTPDto.getOtp())
						? valiedEmployee
						: Boolean.FALSE;
		if (cacheStoreValidOTP.get(verifyOTPDto.getEmailId()) != null)
			cacheStoreValidOTP.invalidate(verifyOTPDto.getEmailId());
		cacheStoreValidOTP.add(verifyOTPDto.getEmailId(), valiedOTP);
		return Optional.of(valiedOTP).filter(Boolean::booleanValue).map(y -> VALID_OTP)
				.orElseThrow(() -> new DataNotFoundException(INVALID_OTP));
	}

	String sendOtp(String emailId, String email, Long mobileNumber, String userName) {
		Long otp = ThreadLocalRandom.current().nextLong(1000, 10000);
		Integer emailStatus = emailService.sendMail(new MailDto(email, "Your OTP For Verification",
				"Dear " + userName + ",\r\n" + "\r\n" + "One Time Password for Password Verification is :" + otp
						+ "\r\n" + "Please use this password to complete the verification." + "\r\n" + "\r\n" + "\r\n"
						+ "Do not share OTP with anyone." + "\r\n" + "Thanks and Regards," + "\r\n" + "Team FLINKO"));
		Integer smsStatus = smsService.sendSms("Dear " + userName + ",\r\n" + "\r\n" + "Your OTP For Verification :"
				+ otp + "\r\n" + "\r\n" + "Thanks and Regards," + "\r\n" + "Team FLINKO", "" + mobileNumber);

		if (emailStatus >= 400 && smsStatus >= 400)
			throw new DataNotFoundException("OTP does not send to respectivr email as well as mobile number");

		if (cacheStoreOTP.get(emailId) != null)
			cacheStoreOTP.invalidate(emailId);
		cacheStoreOTP.add(emailId, otp);

		return optional.filter(emailSuccess -> emailStatus == 200)
				.map(emialRespone -> optional.filter(smsSuccess -> smsStatus == 200)
						.map(emialSmsRespone -> OTP_SEND_TO_YOUR_RESPECTIVE_EMAIL_AND_MOBILE_NUMBER)
						.orElseGet(() -> OTP_SEND_TO_YOUR_RESPECTIVE_EMAIL))
				.orElseGet(() -> optional.filter(smsSuccess -> smsStatus == 200)
						.map(smsRespone -> OTP_SEND_TO_YOUR_RESPECTIVE_MOBILE_NUMBER)
						.orElseThrow(() -> new DataNotFoundException(SOMETHING_WENT_WRONG)));
	}

	private Long userId;

	@Transactional
	@Override
	public String resetPassword(NewConfirmPasswordDto newConfirmPasswordDto) {
		userId = 0l;
		EmployeeLoginInfo employeeLoginInfo = Optional
				.ofNullable(cacheStoreEmployeeLogin.get(newConfirmPasswordDto.getEmailId()))
				.orElseThrow(() -> new DataNotFoundException(SESSION_EXPIRED_FORGOT));

		return optional
				.filter(checkOtp -> cacheStoreValidOTP
						.get(newConfirmPasswordDto.getEmailId()))
				.map(q -> optional
						.filter(newConfirmPassword -> newConfirmPasswordDto.getNewPassword()
								.equals(newConfirmPasswordDto.getConfirmPassword()))
						.map(newOldPassword -> optional
								.filter(newOldPassword1 -> !passwordEncoder
										.matches(
												newConfirmPasswordDto.getNewPassword(), employeeLoginInfo
														.getOldPassword()))
								.map(password1 -> optional.filter(
										e -> !passwordEncoder.matches(newConfirmPasswordDto.getConfirmPassword(),
												employeeLoginInfo.getCurrentPassword()))
										.map(resetPassword -> {
											employeeLoginInfo.setOldPassword(employeeLoginInfo.getCurrentPassword());
											employeeLoginInfo.setCurrentPassword(
													passwordEncoder.encode(newConfirmPasswordDto.getNewPassword()));
											employeeLoginRepository.save(employeeLoginInfo);
											emailService.sendMail(setMailDto(newConfirmPasswordDto.getEmailId(),
													employeeLoginInfo.getEmployeePersonalInfo().getFirstName() + " "
															+ employeeLoginInfo.getEmployeePersonalInfo()
																	.getLastName()));
											cleanUp(newConfirmPasswordDto.getEmailId());
											notificationServiceImpl.saveNotification(PASSWORD_UPDATED,
													employeeLoginInfo.getEmployeePersonalInfo().getEmployeeInfoId());

											if (employeeLoginInfo.getEmployeePersonalInfo().getExpoToken() != null) {
												pushNotificationService.pushMessage("Flinko", PASSWORD_UPDATED,
														employeeLoginInfo.getEmployeePersonalInfo().getExpoToken());
											}

											return RESET_PASSWORD_WITH_EMPLOYEE_ID + employeeLoginInfo.getEmployeeId();
										})
										.orElseThrow(() -> new DataNotFoundException(
												"Current Password And New Password Can Not Be Same!!!")))
								.orElseThrow(() -> new DataNotFoundException(OLD_PASSWORD_AND_NEW_PASSWORD_IS_SAME)))
						.orElseThrow(() -> new DataNotFoundException(
								CURRENT_PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCHED)))
				.orElseThrow(() -> new DataNotFoundException(SESSION_EXPIRED_FORGOT));
	}

	private MailDto setMailDto(String email, String employeeName) {
		return new MailDto(email, "Your Password Was Reset Successfully", "Dear " + employeeName + ",\r\n" + "\r\n"
				+ "We wanted to let you know that your Flinko password was reset." + "\r\n"
				+ " If you run into problems, please contact support team" + ",\r\n" + "\r\n"
				+ "Please do not reply to this email with your password. We will never ask for your password, and we strongly discourage you from sharing it with anyone."
				+ "\r\n" + "\r\n" + "\r\n" + "Thanks and Regards," + "\r\n" + "Team FLINKO");
	}

	@Transactional
	@Override
	public String updatePassword(ResetPasswordDto resetPasswordDto, Long logedInUserId) {
		EmployeeLoginInfo employeeLoginInfo = employeeLoginRepository
				.findByEmployeePersonalInfoEmployeeInfoId(logedInUserId);

		return optional
				.filter(newOldPassword -> !passwordEncoder.matches(resetPasswordDto.getNewPassword(),
						employeeLoginInfo.getCurrentPassword()))
				.map(newOldPasswordSuccess -> optional
						.filter(currentOldPassword -> passwordEncoder.matches(resetPasswordDto.getOldPassword(),
								employeeLoginInfo.getCurrentPassword()))
						.map(currentOldPasswordSuccess -> optional.filter(newConfirmPassword -> resetPasswordDto
								.getNewPassword().equals(resetPasswordDto.getConfirmPassword())).map(updatePassword -> {
									employeeLoginInfo.setOldPassword(employeeLoginInfo.getCurrentPassword());
									employeeLoginInfo.setCurrentPassword(
											passwordEncoder.encode(resetPasswordDto.getNewPassword()));
									notificationServiceImpl.saveNotification(PASSWORD_UPDATED, logedInUserId);

									if (employeeLoginInfo.getEmployeePersonalInfo().getExpoToken() != null) {
										pushNotificationService.pushMessage("Flinko", PASSWORD_UPDATED,
												employeeLoginInfo.getEmployeePersonalInfo().getExpoToken());
									}

									return SUCCESSFULLY_UPDATE_PASSWORD;
								})
								.orElseThrow(() -> new DataNotFoundException(
										CURRENT_PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCHED)))
						.orElseThrow(
								() -> new DataNotFoundException(OLD_PASSWORD_AND_CURRENT_PASSWORD_DOES_NOT_MATCHED)))
				.orElseThrow(() -> new DataNotFoundException(OLD_PASSWORD_AND_NEW_PASSWORD_IS_SAME));
	}

	private void cleanUp(String emailId) {
		try {
			cacheStoreOTP.invalidate(emailId);
			cacheStoreEmployeeLogin.invalidate(emailId);
			cacheStoreValidOTP.invalidate(emailId);
		} catch (Exception e) {
			throw new DataNotFoundException(SESSION_TIME_EXPIRED);
		}
	}

	private Boolean isValidRedius(EmployeeLoginDto employeeLoginDto, List<CompanyBranchInfo> companyBranchInfos,
			HttpServletRequest request) {
		Boolean isValidRadius = Boolean.FALSE;
		for (CompanyBranchInfo companyBranchInfo : companyBranchInfos) {
			CompanyAddressInfo companyAddressInfo = companyBranchInfo.getCompanyAddressInfoList().get(0);
			final double R = 6371.01; // Radius of the earth
			Double lat2 = employeeLoginDto.getLatitude();
			Double lon2 = employeeLoginDto.getLongitude();
			Double lat1 = companyAddressInfo.getLatitude();
			Double lon1 = companyAddressInfo.getLongitude();

			double latDistance = Math.toRadians(lat2 - lat1);
			double lonDistance = Math.toRadians(lon2 - lon1);

			double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			if (companyAddressInfo.getRadius() >= R * c * 1000) {
				isValidRadius = Boolean.TRUE;
				break;
			}
		}
		return isValidRadius;
//		if (!isValidRadius.booleanValue())
//			throw new DataNotFoundException("Out of range can't be logged in");
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public String punchedIn(EmployeeLoginDto employeeLoginDto, HttpServletRequest request) {
		Long employeeInfoId = getEmployeeInfoId();
		return employeePersonalInfoRepository.findByEmployeeInfoIdAndIsActiveTrue(getEmployeeInfoId()).map(emp -> {
			Boolean validRedius = isValidRedius(employeeLoginDto, emp.getCompanyInfo().getCompanyBranchInfoList(),
					request);
			LocalDate now = LocalDate.now();
			int monthValue = now.getMonthValue();
			int year = now.getYear();
			EmployeeOfficialInfo employeeOfficialInfo = emp.getEmployeeOfficialInfo();
			if(employeeOfficialInfo== null) {
				throw new DataNotFoundException("Employee Official Details Not Found");
			}
			CompanyShiftInfo companyShiftInfo = employeeOfficialInfo.getCompanyShiftInfo();
			if(companyShiftInfo== null) {
				throw new DataNotFoundException("Employee Shift Details Not Found");
			}
			EmployeeAttendanceDetails employeeAttendanceDetails = employeeAttendanceDetailsRepository
					.findByEmployeeInfoIdAndMonthNoAndYear(employeeInfoId, monthValue, year).map(empLogin -> {
						List<AttendanceDetails> attendanceDetails = empLogin.getAttendanceDetails();
						AttendanceDetails attendanceDetails2 = attendanceDetails.get(attendanceDetails.size() - 1);
						if (attendanceDetails2.getPunchOut() != null && attendanceDetails2.getPunchOut()
								.getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
							attendanceDetails2.setIsInsideLocation(
									attendanceDetails2.getIsInsideLocation().booleanValue() ? validRedius
											: attendanceDetails2.getIsInsideLocation());
							attendanceDetails2.setPunchIn(LocalDateTime.now());
							return empLogin;
						}

						if (attendanceDetails2.getPunchIn().getDayOfMonth()
								+ (companyShiftInfo.getLogoutTime().isAfter(companyShiftInfo.getLoginTime()) ? 1
										: 0) != LocalDate.now().getDayOfMonth()) {
							empLogin.getAttendanceDetails().add(AttendanceDetails.builder().punchIn(LocalDateTime.now())
									.isInsideLocation(validRedius).detailsId(attendanceDetails.size()+1).build());
						}
						return empLogin;
					})
					.orElseGet(() -> EmployeeAttendanceDetails
							.builder().monthNo(monthValue).year(year).employeeInfoId(employeeInfoId)
							.companyId(getCompanyId()).attendanceDetails(List.of(AttendanceDetails.builder()
									.punchIn(LocalDateTime.now()).isInsideLocation(validRedius).detailsId(1).build()))
							.build());
			employeeAttendanceDetailsRepository.save(employeeAttendanceDetails);
			return "Attendance Added Successfully";
		}).orElseThrow(() -> new DataNotFoundException("Employee Does Not Exist"));
	}

	@Override
	public String punchedOut(EmployeeLoginDto employeeLoginDto, HttpServletRequest request) {
		Long employeeInfoId = getEmployeeInfoId();
		return employeePersonalInfoRepository.findByEmployeeInfoIdAndIsActiveTrue(getEmployeeInfoId()).map(emp -> {
			Boolean validRedius = isValidRedius(employeeLoginDto, emp.getCompanyInfo().getCompanyBranchInfoList(),
					request);
			LocalDate now = LocalDate.now();
			int monthValue = now.getMonthValue();
			int year = now.getYear();
			EmployeeOfficialInfo employeeOfficialInfo = emp.getEmployeeOfficialInfo();
			if(employeeOfficialInfo== null) {
				throw new DataNotFoundException("Employee Official Details Not Found");
			}
			CompanyShiftInfo companyShiftInfo = employeeOfficialInfo.getCompanyShiftInfo();
			if(companyShiftInfo== null) {
				throw new DataNotFoundException("Employee Shift Details Not Found");
			}
			EmployeeAttendanceDetails employeeAttendanceDetails = employeeAttendanceDetailsRepository
					.findByEmployeeInfoIdAndMonthNoAndYear(employeeInfoId, monthValue, year).map(empLogin -> {
						List<AttendanceDetails> attendanceDetails = empLogin.getAttendanceDetails();
						AttendanceDetails attendanceDetails2 = attendanceDetails.get(attendanceDetails.size() - 1);
						if (attendanceDetails2.getPunchOut() != null && attendanceDetails2.getPunchOut().getDayOfMonth()
								+ (companyShiftInfo.getLogoutTime().isAfter(companyShiftInfo.getLoginTime()) ? 1
										: 0) != LocalDate.now().getDayOfMonth()) {
							attendanceDetails.add(AttendanceDetails.builder().punchOut(LocalDateTime.now())
									.isInsideLocation(validRedius).detailsId(attendanceDetails.size()+1).build());
							return empLogin;
						}
						attendanceDetails2.setIsInsideLocation(
								attendanceDetails2.getIsInsideLocation().booleanValue() ? validRedius
										: attendanceDetails2.getIsInsideLocation());
						attendanceDetails2.setPunchOut(LocalDateTime.now());
						return empLogin;
					})
					.orElseGet(() -> EmployeeAttendanceDetails
							.builder().monthNo(monthValue).year(year).employeeInfoId(employeeInfoId)
							.companyId(getCompanyId()).attendanceDetails(List.of(AttendanceDetails.builder()
									.punchOut(LocalDateTime.now()).isInsideLocation(validRedius).detailsId(1).build()))
							.build());
			employeeAttendanceDetailsRepository.save(employeeAttendanceDetails);
			return "Attendance Added Successfully";
		}).orElseThrow(() -> new DataNotFoundException("Employee Does Not Exist"));
	}


}
