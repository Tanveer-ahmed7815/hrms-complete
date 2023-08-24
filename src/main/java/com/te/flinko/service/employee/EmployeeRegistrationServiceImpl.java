package com.te.flinko.service.employee;

import static com.te.flinko.common.employee.EmployeeRegistrationConstants.ADMIN_WILL_VERIFY_YOUR_ACCOUNT;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.ALREADY_REGISTERED;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.DATA_NOT_FOUND;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.EMPLOYEE_ALREADY_REGISTERED;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.EMPLOYEE_ID_ALREADY_EXIST;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.INVALID_OTP;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.REGISTRATION_SUCCESSFULLY_DONE;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.SESSION_EXPIRED;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.SESSION_TIME_EXPIRED;
import static com.te.flinko.common.employee.EmployeeRegistrationConstants.VALID_OTP;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.admin.BussinessPlanDTO;
import com.te.flinko.dto.admin.CompanyDesignationNamesDto;
import com.te.flinko.dto.admin.CompanyInfoNamesDto;
import com.te.flinko.dto.admin.PlanDTO;
import com.te.flinko.dto.employee.EmployeeIdDto;
import com.te.flinko.dto.employee.EmployeeLoginDto;
import com.te.flinko.dto.employee.EmployeeOfficialInfoDto;
import com.te.flinko.dto.employee.EmployeePersonalInfoDto;
import com.te.flinko.dto.employee.MailDto;
import com.te.flinko.dto.employee.NewConfirmPasswordDto;
import com.te.flinko.dto.employee.Registration;
import com.te.flinko.dto.employee.VerifyOTPDto;
import com.te.flinko.dto.superadmin.PlanDetailsDTO;
import com.te.flinko.entity.Department;
import com.te.flinko.entity.admin.CompanyDesignationInfo;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeLoginInfo;
import com.te.flinko.entity.superadmin.PlanDetails;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.exception.employee.EmployeeNotRegisteredException;
import com.te.flinko.repository.DepartmentRepository;
import com.te.flinko.repository.admin.CompanyDesignationInfoRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeLoginInfoRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.superadmin.mongo.PlanDetailsRepository;
import com.te.flinko.service.mail.employee.EmailService;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.util.CacheStore;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeRegistrationServiceImpl implements EmployeeRegistrationService {

	private static final String ADMIN = "ADMIN";

	private static final String MOBILE_NUMBER_OR_EMAIL_ID_ALREADY_EXIST = "Mobile Number Or Email Id Already Exist!!!";

	private final CompanyInfoRepository companyInfoRepository;

	private final CompanyDesignationInfoRepository companyDesignationInfoRepository;

	private final EmployeeLoginInfoRepository employeeLoginInfoRepository;

	private final InAppNotificationServiceImpl inAppNotificationServiceImpl;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final DepartmentRepository departmentRepository;

	private final CacheStore<Registration> cacheStoreEmployeeRegistrationDto;

	private final CacheStore<Long> cacheStoreOTP;

	private final CacheStore<BussinessPlanDTO> cacheStore;

	private final CacheStore<PlanDTO> cacheStorePlanDTO;

	private final EmailService emailService;

	private final PasswordEncoder passwordEncoder;

	private final CacheStore<Boolean> cacheStoreValiedOTP;

	private final EmployeeLoginServiceImpl employeeLoginServiceImpl;

	private final PlanDetailsRepository planDetailsRepository;

	@Override
	public List<CompanyInfoNamesDto> getAllCompany() {

		return Stream.of(companyInfoRepository.getAllCompany()).map(company -> {
			CompanyInfoNamesDto dto = new CompanyInfoNamesDto();
			Object[] companyInfoNamesDto = (Object[]) company;
			return Stream.of(companyInfoNamesDto).map(x -> Optional.of(x).filter(Long.class::isInstance).map(y -> {
				dto.setCompanyId((Long) x);
				return dto;
			}).orElseGet(() -> {
				dto.setCompanyName((String) x);
				return dto;
			})).collect(Collectors.toSet());
		}).flatMap(Collection::stream).collect(Collectors.toList());

	}

	@Override
	public List<CompanyDesignationNamesDto> getAllDesignation(Long companyId) {
		return companyDesignationInfoRepository.findByCompanyInfoCompanyId(companyId).stream().map(x -> {
			CompanyDesignationNamesDto companyDesignationNamesDto = new CompanyDesignationNamesDto();
			BeanUtils.copyProperties(x, companyDesignationNamesDto);
			return companyDesignationNamesDto;
		}).collect(Collectors.toList());
	}

	private Long userId;

	@Transactional
	@Override
	public PlanDTO registration(NewConfirmPasswordDto newConfirmPasswordDto, String terminalId) {
		userId = 0l;
		if (employeeLoginInfoRepository.findByEmployeeIdAndEmployeePersonalInfoCompanyInfoCompanyId(
				newConfirmPasswordDto.getEmployeeId(), newConfirmPasswordDto.getCompanyId()).isPresent())
			return PlanDTO.builder().msg(EMPLOYEE_ALREADY_REGISTERED).build();

		return Optional.ofNullable(newConfirmPasswordDto)
				.filter(registration -> Optional.ofNullable(cacheStoreValiedOTP.get(registration.getEmployeeId()))
						.orElse(Boolean.FALSE)
						&& registration.getNewPassword().equals(registration.getConfirmPassword()))
				.map(companyRegistration -> {
					Registration employeeRegistrationDto2 = cacheStoreEmployeeRegistrationDto
							.get(newConfirmPasswordDto.getEmployeeId());
					employeeRegistrationDto2.setIsActive(Boolean.TRUE);
					CompanyInfo companyInfo2 = companyInfoRepository
							.findByCompanyName(employeeRegistrationDto2.getCompanyName()).filter(Objects::nonNull)
							.map(companyInfo -> {
								employeeRegistrationDto2.setIsActive(Boolean.FALSE);
								return companyInfo;
							}).orElse(null);
					employeeRegistrationDto2.setPassword(newConfirmPasswordDto.getNewPassword());

					CompanyInfo companyInfo3 = null;
					if (companyInfo2 == null) {
						employeeRegistrationDto2.setDesignationName("Admin");
						employeeRegistrationDto2.setDepartment(ADMIN);
						Department department = departmentRepository.findByDepartmentName(ADMIN).orElse(null);
						employeeRegistrationDto2.setRoles(department.getRoles());
						CompanyDesignationInfo companyDesignationInfo = BeanCopy
								.objectProperties(employeeRegistrationDto2, CompanyDesignationInfo.class);

						if (employeeRegistrationDto2.getIsActive().booleanValue()) {
							employeeRegistrationDto2.setStatus(Map.of("approvedBy", ADMIN));
						}
						CompanyInfo companyInfo = BeanCopy.objectProperties(employeeRegistrationDto2,
								CompanyInfo.class);
						companyDesignationInfo.setCompanyInfo(companyInfo3);
						List<CompanyDesignationInfo> companyDesignationInfos = List.of(companyDesignationInfo);
						companyInfo.setMobileNumber(employeeRegistrationDto2.getCompanyMobileNumber());
						companyInfo.setEmailId(employeeRegistrationDto2.getCompanyEmailId());
						companyInfo.setIsActive(Boolean.FALSE);
						companyInfo.setIsSubmited(Boolean.FALSE);
						companyInfo3 = companyInfoRepository.save(companyInfo);
						companyInfo3.setCompanyDesignationInfoList(companyDesignationInfos);
						CompanyDesignationInfo companyDesignationInfo2 = companyInfo3.getCompanyDesignationInfoList()
								.get(0);
						companyDesignationInfo2.setParentDesignationInfo(companyDesignationInfo2);
						companyDesignationInfo2.setCompanyInfo(companyInfo3);
						inAppNotificationServiceImpl.saveCompanyNotification(
								companyInfo3.getCompanyName() + " Company is registered successfully",
								companyInfo3.getCompanyId());
					} else if (companyInfo2 != null && !companyInfo2.getEmployeePersonalInfoList().isEmpty()) {
						emailService.sendMail(MailDto.builder().subject("Verification Employee Account")
								.body("Dear Admin," + "\r\n" + "\r\n"
										+ "The Employee Wants To Register In Your Company Please Verify The Account With EmployeeId :"
										+ employeeRegistrationDto2.getEmployeeId() + "\r\n" + "\r\n" + "\r\n"
										+ "Thanks and Regards," + "\r\n" + "Team FLINKO")
								.to(companyInfo2.getEmployeePersonalInfoList().get(0).getEmployeeOfficialInfo()
										.getOfficialEmailId())
								.body(ALREADY_REGISTERED).build());
					}

					EmployeeLoginDto employeeLoginDto = BeanCopy.objectProperties(employeeRegistrationDto2,
							EmployeeLoginDto.class);

					employeeLoginDto.setCurrentPassword(employeeRegistrationDto2.getPassword());

					EmployeePersonalInfoDto employeePersonalInfoDto = BeanCopy
							.objectProperties(employeeRegistrationDto2, EmployeePersonalInfoDto.class);

					EmployeeOfficialInfoDto employeeOfficialInfoDto = BeanCopy
							.objectProperties(employeeRegistrationDto2, EmployeeOfficialInfoDto.class);

					employeePersonalInfoDto.setEmployeeOfficialInfo(employeeOfficialInfoDto);
					employeePersonalInfoDto.setPan(null);
					employeeLoginDto.setEmployeePersonalInfo(employeePersonalInfoDto);
					EmployeeLoginInfo objectProperties = BeanCopy.objectProperties(employeeLoginDto,
							EmployeeLoginInfo.class);

					if (companyInfo2 != null && !companyInfo2.getEmployeePersonalInfoList().isEmpty()) {
						objectProperties.getEmployeePersonalInfo().setStatus(Map.of("RequestedTo", companyInfo2
								.getEmployeePersonalInfoList().get(0).getEmployeeOfficialInfo().getOfficialEmailId()));
					}

					objectProperties.getEmployeePersonalInfo()
							.setCompanyInfo(companyInfo2 == null ? companyInfo3 : companyInfo2);
					objectProperties.getEmployeePersonalInfo().getEmployeeOfficialInfo()
							.setDesignation(employeePersonalInfoDto.getEmployeeOfficialInfo().getDesignationName());
					objectProperties.setCurrentPassword(passwordEncoder.encode(objectProperties.getCurrentPassword()));
					EmployeeLoginInfo save = employeeLoginInfoRepository.save(objectProperties);
					userId = save.getEmployeePersonalInfo().getEmployeeInfoId();
					cleanUp(newConfirmPasswordDto.getEmployeeId());
					PlanDTO planDTO = cacheStorePlanDTO.get(terminalId);
					if (planDTO == null) {
						planDTO = new PlanDTO();
						planDTO.setPlanId(111l);
					}
					planDTO.setCompanyId(companyInfo3.getCompanyId());
					planDTO.setEmployeeInfoId(userId);
					planDTO.setMsg(
							companyInfo2 == null ? REGISTRATION_SUCCESSFULLY_DONE : ADMIN_WILL_VERIFY_YOUR_ACCOUNT);
					cacheStore.invalidate(terminalId);
					return planDTO;
				}).orElseThrow(() -> new DataNotFoundException(SESSION_EXPIRED));
	}

	@Override
	public String varifyEmployee(Registration employeeRegistrationDto, Long companyId, String terminalId) {
		BeanUtils.copyProperties(cacheStore.get(terminalId), employeeRegistrationDto);
		if (!employeePersonalInfoRepository.findByMobileNumberOrEmployeeOfficialInfoOfficialEmailId(
				employeeRegistrationDto.getMobileNumber(), employeeRegistrationDto.getOfficialEmailId()).isEmpty()) {
			throw new EmployeeNotRegisteredException(MOBILE_NUMBER_OR_EMAIL_ID_ALREADY_EXIST);
		}

		if (cacheStoreEmployeeRegistrationDto.get(employeeRegistrationDto.getEmployeeId()) != null)
			cacheStoreEmployeeRegistrationDto.invalidate(employeeRegistrationDto.getEmployeeId());

		return employeePersonalInfoRepository
				.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(employeeRegistrationDto.getCompanyId(),
						employeeRegistrationDto.getEmployeeId())
				.filter(List::isEmpty).map(x -> {
					cacheStoreEmployeeRegistrationDto.add(employeeRegistrationDto.getEmployeeId(),
							employeeRegistrationDto);
					return employeeLoginServiceImpl.sendOtp(employeeRegistrationDto.getEmployeeId(),
							employeeRegistrationDto.getOfficialEmailId(), employeeRegistrationDto.getMobileNumber(),
							employeeRegistrationDto.getFirstName() + " " + employeeRegistrationDto.getLastName());
				}).orElseThrow(() -> new EmployeeNotRegisteredException(EMPLOYEE_ID_ALREADY_EXIST));

	}

	@Override
	public String validateOTP(VerifyOTPDto verifyOTPDto) {
		Boolean valiedEmployee = Objects.nonNull(cacheStoreEmployeeRegistrationDto.get(verifyOTPDto.getEmployeeId()))
				? Boolean.TRUE
				: Boolean.FALSE;
		Boolean valiedOTP = Optional.ofNullable(cacheStoreOTP.get(verifyOTPDto.getEmployeeId()))
				.orElseThrow(() -> new DataNotFoundException(SESSION_EXPIRED)).equals(verifyOTPDto.getOtp())
						? valiedEmployee
						: Boolean.FALSE;
		if (cacheStoreValiedOTP.get(verifyOTPDto.getEmployeeId()) != null)
			cacheStoreValiedOTP.invalidate(verifyOTPDto.getEmployeeId());
		cacheStoreValiedOTP.add(verifyOTPDto.getEmployeeId(), valiedOTP);
		return Optional.of(valiedOTP).filter(Boolean::booleanValue).map(y -> VALID_OTP)
				.orElseThrow(() -> new DataNotFoundException(INVALID_OTP));
	}

	@Override
	public String resendOTP(EmployeeIdDto employeeIdDto) {
		Registration registration2 = cacheStoreEmployeeRegistrationDto.get(employeeIdDto.getEmployeeId());
		Registration registration = Optional.ofNullable(registration2)
				.orElseThrow(() -> new DataNotFoundException(SESSION_EXPIRED));
		return Objects.nonNull(registration)
				? employeeLoginServiceImpl.sendOtp(registration.getEmployeeId(), registration.getOfficialEmailId(),
						registration.getMobileNumber(), registration.getFirstName() + " " + registration.getLastName())
				: DATA_NOT_FOUND;
	}

	private void cleanUp(String employeeId) {
		try {
			cacheStoreValiedOTP.invalidate(employeeId);
			cacheStoreEmployeeRegistrationDto.invalidate(employeeId);
			cacheStoreOTP.invalidate(employeeId);
		} catch (Exception e) {
			throw new DataNotFoundException(SESSION_TIME_EXPIRED);
		}
	}

	@Override
	public List<PlanDetailsDTO> getAllPlanDetails(HttpServletRequest request, String terminalId) {
		List<PlanDetails> planDetailsList = planDetailsRepository.findAll();
		PlanDTO planDTO = cacheStorePlanDTO.get(terminalId);
		return planDetailsList.stream()
				.map(plan -> new PlanDetailsDTO(plan.getPlanId(), planDTO == null ? null : planDTO.getPlanName(),
						plan.getAmountPerMonth(), plan.getDuration(), plan.getDuration().intValue(), plan.getPlanName(),
						plan.getNoOfEmp(), plan.getAdditionalCostPerEmp(), plan.getDepartments()))
				.collect(Collectors.toList());
	}

	public Long getUserId() {
		return userId;
	}
}
