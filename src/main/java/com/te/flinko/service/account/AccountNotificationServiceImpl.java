package com.te.flinko.service.account;

import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_REIMBURSEMENT_INFORMATION_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_REIMBURSEMENT_PAID_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_PAID_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_SLIP_GENERATED_SUCCESSFULLY;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.ADVANCE_SALARY_PAID_SUCCESSFULLY;
import static com.te.flinko.common.hr.HrConstants.COMPANY_INFORMATION_NOT_PRESENT;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.account.AccountMarkAsPaidDTO;
import com.te.flinko.dto.account.AccountPaySlipDTO;
import com.te.flinko.dto.account.AccountPaySlipListDTO;
import com.te.flinko.dto.account.AccountReimbursementMarkAsPaidDTO;
import com.te.flinko.dto.account.AccountSalaryDTO;
import com.te.flinko.dto.account.AdvanceSalaryDTO;
import com.te.flinko.dto.account.GeneratePayslipInputDTO;
import com.te.flinko.dto.account.MarkAsPaidInputDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryListDTO;
import com.te.flinko.dto.account.MarkAsReimbursedDTO;
import com.te.flinko.dto.account.ReimbursementInfoByIdDTO;
import com.te.flinko.dto.account.ReimbursementListDTO;
import com.te.flinko.dto.account.SalaryDTO;
import com.te.flinko.dto.employee.EmployeeReviseSalaryDTO;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeAdvanceSalary;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReimbursementInfo;
import com.te.flinko.entity.employee.EmployeeReviseSalary;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.exception.CompanyIdNotFoundException;
import com.te.flinko.exception.account.CustomExceptionForAccount;
import com.te.flinko.exception.admin.CompanyNotFound;
import com.te.flinko.exception.hr.CompanyNotFoundException;
import com.te.flinko.repository.DepartmentRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.employee.EmployeeAdvanceSalaryRepository;
import com.te.flinko.repository.employee.EmployeeReimbursementInfoRepository;
import com.te.flinko.repository.employee.EmployeeReviseSalaryRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountNotificationServiceImpl implements AccountNotificationService {
	@Autowired
	private EmployeeReimbursementInfoRepository employeeReimbursementInfoRepo;
	@Autowired
	private CompanyInfoRepository companyInfoRepository;
	@Autowired
	private EmployeeAdvanceSalaryRepository advanceSalaryRepo;
	@Autowired
	private EmployeeSalaryDetailsRepository employeeSalaryDetailsRepo;
	@Autowired
	private CompanyInfoRepository companyInfoRepo;
	@Autowired
	private EmployeeSalaryDetailsRepository salaryDetailsRepo;
	@Autowired
	private DepartmentRepository departmentRepo;
	@Autowired
	private EmployeeReviseSalaryRepository employeeReviseSalaryRepository;

	@Override
	/**
	 * this method is use to save the attachments details accept the object of
	 * companyPurchaseInvoice
	 * 
	 * @param invoiceDetailsDto object
	 * @return updated object of InvoiceDetailsDTO
	 **/
	public ArrayList<ReimbursementListDTO> reimbursement(Long companyId) {
		List<EmployeeReimbursementInfo> reimbursementInfo = employeeReimbursementInfoRepo
				.findByEmployeePersonalInfoCompanyInfoCompanyIdAndStatusIgnoreCaseAndIsPaidIsNullOrIsPaid(companyId,
						"APPROVED", false);
		ArrayList<ReimbursementListDTO> reimbursement = new ArrayList<>();

		for (EmployeeReimbursementInfo employeeReimbursementInfo : reimbursementInfo) {
			reimbursement
					.add(new ReimbursementListDTO(employeeReimbursementInfo.getReimbursementId(),
							employeeReimbursementInfo.getCompanyExpenseCategories().getExpenseCategoryName(),
							employeeReimbursementInfo.getExpenseDate(), employeeReimbursementInfo.getAmount(),
							employeeReimbursementInfo.getEmployeePersonalInfo().getFirstName() + " "
									+ employeeReimbursementInfo.getEmployeePersonalInfo().getLastName(),
							"Not Reimbursed"));

		}
		log.info(EMPLOYEE_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY);
		return reimbursement;
	}

	@Override
	public ReimbursementInfoByIdDTO reimbursementById(Long companyId, Long reimbursementId) {

		companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new CompanyIdNotFoundException(COMPANY_INFORMATION_NOT_PRESENT));
		EmployeeReimbursementInfo reimbursementInfo = employeeReimbursementInfoRepo.findById(reimbursementId)
				.orElseThrow(() -> new CustomExceptionForAccount(EMPLOYEES_REIMBURSEMENT_INFORMATION_NOT_FOUND));
		ReimbursementInfoByIdDTO reimbursementInfoByIdDTO = new ReimbursementInfoByIdDTO();
		BeanUtils.copyProperties(reimbursementInfo, reimbursementInfoByIdDTO);
		reimbursementInfoByIdDTO
				.setExpenseCategoryName(reimbursementInfo.getCompanyExpenseCategories().getExpenseCategoryName());
		reimbursementInfoByIdDTO.setFullName(reimbursementInfo.getEmployeePersonalInfo().getFirstName() + " "
				+ reimbursementInfo.getEmployeePersonalInfo().getLastName());
		String status = "";
		if (Boolean.TRUE.equals(reimbursementInfo.getIsPaid())) {
			status = "Paid";
		} else if (!Boolean.TRUE.equals(reimbursementInfo.getIsPaid())) {
			status = "Not paid";
		}
		reimbursementInfoByIdDTO.setStatus(status);
		log.info(EMPLOYEE_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY);
		return reimbursementInfoByIdDTO;

	}

	@Override
	public List<AccountMarkAsPaidDTO> markAsPaidAdvanceSalary(Long companyId, MarkAsPaidInputDTO advanceSalaryId) {
		ArrayList<Long> employeeAdvanceSalaryList = advanceSalaryId.getAdvanceSalaryId();
		List<EmployeeAdvanceSalary> advanceSalaryDetails = advanceSalaryRepo
				.findByAdvanceSalaryIdInAndEmployeePersonalInfoCompanyInfoCompanyId(employeeAdvanceSalaryList,
						companyId);
		List<AccountMarkAsPaidDTO> advanceSalaryList = new ArrayList<>();
		if (advanceSalaryDetails == null || advanceSalaryDetails.isEmpty()) {
			throw new CustomExceptionForAccount(EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND);
		}
		for (EmployeeAdvanceSalary employeeAdvanceSalary : advanceSalaryDetails) {
			employeeAdvanceSalary.setIsPaid(true);
			EmployeeAdvanceSalary advanceSalary = advanceSalaryRepo.save(employeeAdvanceSalary);
			AccountMarkAsPaidDTO accountMarkAsPaidDTO = new AccountMarkAsPaidDTO();
			BeanUtils.copyProperties(advanceSalary, accountMarkAsPaidDTO);
			advanceSalaryList.add(accountMarkAsPaidDTO);

		}
		log.info(ADVANCE_SALARY_PAID_SUCCESSFULLY);
		return advanceSalaryList;
	}

	@Override
	public ArrayList<AccountReimbursementMarkAsPaidDTO> markAsReimbursed(Long companyId,
			MarkAsReimbursedDTO reimbursementIdList) {
		ArrayList<Long> reimbursementIds = reimbursementIdList.getReimbursementIdList();
		System.out.println();
		List<EmployeeReimbursementInfo> reimbursementList = employeeReimbursementInfoRepo
				.findByReimbursementIdInAndEmployeePersonalInfoCompanyInfoCompanyId(reimbursementIds, companyId);
		if (reimbursementList == null || reimbursementList.isEmpty()) {
			throw new CustomExceptionForAccount(EMPLOYEES_REIMBURSEMENT_INFORMATION_NOT_FOUND);
		}
		ArrayList<AccountReimbursementMarkAsPaidDTO> reimbursementInfoList = new ArrayList<>();
		for (EmployeeReimbursementInfo employeeReimbursementInfo : reimbursementList) {
			employeeReimbursementInfo.setIsPaid(true);
			EmployeeReimbursementInfo reimbursementInfo = employeeReimbursementInfoRepo.save(employeeReimbursementInfo);
			AccountReimbursementMarkAsPaidDTO accountReimbursementMarkAsPaidDTO = new AccountReimbursementMarkAsPaidDTO();
			BeanUtils.copyProperties(reimbursementInfo, accountReimbursementMarkAsPaidDTO);
			reimbursementInfoList.add(accountReimbursementMarkAsPaidDTO);
		}
		log.info(EMPLOYEE_REIMBURSEMENT_PAID_SUCCESSFULLY);
		return reimbursementInfoList;

	}

	@Override
	public ArrayList<MarkAsPaidSalaryListDTO> markAsPaidSalary(MarkAsPaidSalaryDTO markAsPaidSalaryDTO,
			Long companyId) {
		ArrayList<Long> employeeSalaryIdList = markAsPaidSalaryDTO.getEmployeeSalaryIdList();
		List<EmployeeSalaryDetails> salaryDetailList = employeeSalaryDetailsRepo
				.findByEmployeeSalaryIdInAndCompanyInfoCompanyId(employeeSalaryIdList, companyId);
		if (salaryDetailList == null || salaryDetailList.isEmpty()) {
			throw new CustomExceptionForAccount(EMPLOYEE_SALARY_DETAILS_NOT_FOUND);
		}
		ArrayList<MarkAsPaidSalaryListDTO> salaryDetailsList = new ArrayList<>();
		for (EmployeeSalaryDetails employeeSalaryDetails : salaryDetailList) {
			employeeSalaryDetails.setIsPaid(true);
			EmployeeSalaryDetails salaryDetails = employeeSalaryDetailsRepo.save(employeeSalaryDetails);
			MarkAsPaidSalaryListDTO markAsPaidSalaryListDTO = new MarkAsPaidSalaryListDTO();
			BeanUtils.copyProperties(salaryDetails, markAsPaidSalaryListDTO);
			salaryDetailsList.add(markAsPaidSalaryListDTO);
		}
		log.info(EMPLOYEE_SALARY_PAID_SUCCESSFULLY);
		return salaryDetailsList;
	}

	@Override
	public ArrayList<MarkAsPaidSalaryListDTO> generatePaySlip(Long companyId,
			GeneratePayslipInputDTO employeeSalaryIdList) {
		ArrayList<Long> employeeSalaryIds = employeeSalaryIdList.getEmployeeSalaryIdList();
		List<EmployeeSalaryDetails> salaryDetailList = employeeSalaryDetailsRepo
				.findByEmployeeSalaryIdInAndCompanyInfoCompanyId(employeeSalaryIds, companyId);
		if (salaryDetailList == null || salaryDetailList.isEmpty()) {
			throw new CustomExceptionForAccount(EMPLOYEE_SALARY_DETAILS_NOT_FOUND);
		}
		ArrayList<MarkAsPaidSalaryListDTO> salaryDetailsList = new ArrayList<>();
		for (EmployeeSalaryDetails employeeSalaryDetails : salaryDetailList) {
			employeeSalaryDetails.setIsPayslipGenerated(true);
			EmployeeSalaryDetails salaryDetails = employeeSalaryDetailsRepo.save(employeeSalaryDetails);
			MarkAsPaidSalaryListDTO markAsPaidSalaryListDTO = new MarkAsPaidSalaryListDTO();
			BeanUtils.copyProperties(salaryDetails, markAsPaidSalaryListDTO);
			salaryDetailsList.add(markAsPaidSalaryListDTO);
		}
		log.info(EMPLOYEE_SALARY_SLIP_GENERATED_SUCCESSFULLY);
		return salaryDetailsList;
	}

	@Override
	public AccountPaySlipDTO paySlipDetailsById(Long employeeSalaryId, Long companyId) {
		List<EmployeeSalaryDetails> salaryDetails = employeeSalaryDetailsRepo
				.findByemployeeSalaryIdAndCompanyInfoCompanyId(employeeSalaryId, companyId);
		if (salaryDetails == null || salaryDetails.isEmpty()) {
			throw new CustomExceptionForAccount(EMPLOYEE_SALARY_DETAILS_NOT_FOUND);
		}
		AccountPaySlipDTO accountPaySlipDTO = new AccountPaySlipDTO();
		EmployeeSalaryDetails employeeSalaryDetails = salaryDetails.get(0);
		BeanUtils.copyProperties(employeeSalaryDetails, accountPaySlipDTO);
		accountPaySlipDTO.setFullName(employeeSalaryDetails.getEmployeePersonalInfo().getFirstName() + " "
				+ employeeSalaryDetails.getEmployeePersonalInfo().getLastName());
		accountPaySlipDTO
				.setEmployeeId(employeeSalaryDetails.getEmployeePersonalInfo().getEmployeeOfficialInfo() == null ? null
						: employeeSalaryDetails.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId());
		if (Boolean.TRUE.equals(employeeSalaryDetails.getIsPaid())) {
			accountPaySlipDTO.setPaymentStatus("Paid");
		} else {
			accountPaySlipDTO.setPaymentStatus("Pending");
		}
		if (Boolean.TRUE.equals(employeeSalaryDetails.getIsPayslipGenerated())) {
			accountPaySlipDTO.setStatus("Generated");
		} else {
			accountPaySlipDTO.setStatus("Not generated");
		}

		log.info(EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY);
		return accountPaySlipDTO;
	}

	@Override
	public List<AdvanceSalaryDTO> advanceSalary(Long companyId) {
		ArrayList<AdvanceSalaryDTO> advanceSalaryList = new ArrayList<>();
		companyInfoRepo.findById(companyId)
				.orElseThrow(() -> new CompanyIdNotFoundException(COMPANY_INFORMATION_NOT_PRESENT));
		log.info("company details verified");
		List<EmployeeAdvanceSalary> advanceSalary = advanceSalaryRepo
				.findByEmployeePersonalInfoCompanyInfoCompanyIdAndStatusIgnoreCaseAndIsPaidIsNullOrIsPaid(companyId,
						"Approved", false);
		for (EmployeeAdvanceSalary employeeAdvanceSalary : advanceSalary) {
			advanceSalaryList.add(new AdvanceSalaryDTO(
					employeeAdvanceSalary.getEmployeePersonalInfo().getFirstName() + " "
							+ employeeAdvanceSalary.getEmployeePersonalInfo().getLastName(),
					employeeAdvanceSalary.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId(),
					employeeAdvanceSalary.getAmount(), employeeAdvanceSalary.getEmi(), "Not Paid",
					employeeAdvanceSalary.getEmployeePersonalInfo().getCompanyInfo().getCompanyId(),
					employeeAdvanceSalary.getAdvanceSalaryId(), employeeAdvanceSalary.getCreatedDate()));

		}

		log.info(EMPLOYEE_ADVANCE_SALARY_SUCCESSFULLY_FETCHED);
		return advanceSalaryList;
	}

	@Override
	public List<AccountSalaryDTO> salaryDetailsList(Long companyId) {
		List<EmployeeSalaryDetails> salarydetails;
		List<AccountSalaryDTO> addData = null;
		CompanyInfo companyInfo = companyInfoRepo.findById(companyId)
				.orElseThrow(() -> new CompanyNotFound(COMPANY_INFORMATION_NOT_PRESENT));
		log.info("company details verified");
		LocalDate now = LocalDate.now();

		Map<Long, Integer> employeeMonthlyPayDetails = companyInfo.getCompanyPayrollInfoList().stream()
				.filter(payroll -> {
					Month paymentMonth = Optional.of(payroll)
							.filter(pay -> (pay.getSalaryApprovalDate() > pay.getPaymentDate())
									&& pay.getSalaryApprovalDate() < now.getDayOfMonth())
							.map(b -> now.getMonth().plus(1)).orElse(now.getMonth());

					int paymentYear = Optional.of(payroll)
							.filter(pay -> (pay.getSalaryApprovalDate() > pay.getPaymentDate())
									&& (pay.getSalaryApprovalDate() < now.getDayOfMonth()) && now.getMonthValue() == 12)
							.map(b -> now.getYear() + 1).orElse(now.getYear());

					boolean isLeapYear = LocalDate.of(paymentYear, paymentMonth, now.getDayOfMonth()).isLeapYear();

					LocalDate paymentDate = Optional.of(payroll)
							.filter(payInfo -> paymentMonth.length(isLeapYear) > payInfo.getPaymentDate())
							.map(b -> LocalDate.of(paymentYear, paymentMonth, payroll.getPaymentDate()))
							.orElse(LocalDate.of(paymentYear, paymentMonth, paymentMonth.length(isLeapYear)));
					return now.isBefore(paymentDate);
				}).map(payroll -> {
					Month month = Optional.of(payroll)
							.filter(pay -> (pay.getSalaryApprovalDate() > pay.getPaymentDate())
									&& pay.getSalaryApprovalDate() > now.getDayOfMonth())
							.map(b -> now.getMonth().minus(1)).orElse(now.getMonth());
					return payroll.getEmployeeAnnualSalaryList().stream().map(employeeSalary -> {
						return SalaryDTO.builder()
								.employeeInfoId(employeeSalary.getEmployeePersonalInfo().getEmployeeInfoId())
								.month(month.getValue()).build();
					}).collect(Collectors.toList());
				}).flatMap(Collection::stream).distinct()
				.collect(Collectors.toMap(SalaryDTO::getEmployeeInfoId, SalaryDTO::getMonth));

		salarydetails = salaryDetailsRepo.findByCompanyInfoCompanyIdAndIsPaidAndIsFinalized(companyInfo.getCompanyId(),
				false, true);

		salarydetails = salarydetails.stream().filter(salary -> salary.getEmployeePersonalInfo()
				.getEmployeeInfoId() != null
				&& (employeeMonthlyPayDetails.get(salary.getEmployeePersonalInfo().getEmployeeInfoId()) == null
						|| !Objects.equals(
								employeeMonthlyPayDetails.get(salary.getEmployeePersonalInfo().getEmployeeInfoId()),
								salary.getMonth())))
				.collect(Collectors.toList());

		addData = addSalaryData(salarydetails);
		log.info(EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY);
		return addData;
	}

	private List<AccountSalaryDTO> addSalaryData(List<EmployeeSalaryDetails> salaryDetails) {
		ArrayList<AccountSalaryDTO> dropDownlist = new ArrayList<>();
		Double totalDeduction = 0.0d;
		Double totalAdditional = 0.0d;
		for (EmployeeSalaryDetails employeeSalaryDetails : salaryDetails) {
			Map<String, String> deduction = employeeSalaryDetails.getDeduction();
			EmployeePersonalInfo employeePersonalInfo = employeeSalaryDetails.getEmployeePersonalInfo();
			EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
			if ((employeePersonalInfo != null) && (employeeOfficialInfo != null)) {

				String lop = null;
				if ((deduction != null)) {
					lop = deduction.get("lop");
				}

				lop = (lop == null) ? Integer.toString(0) : lop;
				dropDownlist.add(new AccountSalaryDTO(employeeOfficialInfo.getEmployeeId(),
						employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName(),
						employeeSalaryDetails.getTotalSalary(), employeeSalaryDetails.getAdditional(),
						employeeSalaryDetails.getDeduction(), lop, employeeSalaryDetails.getNetPay(), "Not Paid",
						employeeSalaryDetails.getEmployeeSalaryId(), totalAdditional, totalDeduction,
						Month.of(employeeSalaryDetails.getMonth()).name()));
				totalAdditional = 0.0d;
				totalDeduction = 0.0d;
			}

		}
		return dropDownlist;
	}

	@Override
	public List<AccountPaySlipListDTO> paySlip(Long companyId) {
		List<EmployeeSalaryDetails> salarydetails;
		List<AccountPaySlipListDTO> addData = null;
		CompanyInfo companyInfo = companyInfoRepo.findById(companyId)
				.orElseThrow(() -> new CompanyNotFound(COMPANY_INFORMATION_NOT_PRESENT));
		log.info("company details verified");
		LocalDate now = LocalDate.now();

		Map<Long, Integer> employeeMonthlyPayDetails = companyInfo.getCompanyPayrollInfoList().stream()
				.filter(payroll -> {
					Month payslipGenerationMonth = Optional.of(payroll)
							.filter(pay -> (pay.getSalaryApprovalDate() > pay.getPaySlipGenerationDate())
									&& pay.getSalaryApprovalDate() < now.getDayOfMonth())
							.map(b -> now.getMonth().plus(1)).orElse(now.getMonth());

					int payslipGenerationYear = Optional.of(payroll)
							.filter(pay -> (pay.getSalaryApprovalDate() > pay.getPaySlipGenerationDate())
									&& (pay.getSalaryApprovalDate() < now.getDayOfMonth()) && now.getMonthValue() == 12)
							.map(b -> now.getYear() + 1).orElse(now.getYear());

					boolean isLeapYear = LocalDate
							.of(payslipGenerationYear, payslipGenerationMonth, now.getDayOfMonth()).isLeapYear();

					LocalDate payslipGenerationDate = Optional.of(payroll).filter(
							payInfo -> payslipGenerationMonth.length(isLeapYear) > payInfo.getPaySlipGenerationDate())
							.map(b -> LocalDate.of(payslipGenerationYear, payslipGenerationMonth,
									payroll.getPaySlipGenerationDate()))
							.orElse(LocalDate.of(payslipGenerationYear, payslipGenerationMonth,
									payslipGenerationMonth.length(isLeapYear)));
					return now.isBefore(payslipGenerationDate);
				}).map(payroll -> {
					Month month = Optional.of(payroll)
							.filter(pay -> (pay.getSalaryApprovalDate() > pay.getPaySlipGenerationDate())
									&& pay.getSalaryApprovalDate() > now.getDayOfMonth())
							.map(b -> now.getMonth().minus(1)).orElse(now.getMonth());
					return payroll.getEmployeeAnnualSalaryList().stream().map(employeeSalary -> {
						return SalaryDTO.builder()
								.employeeInfoId(employeeSalary.getEmployeePersonalInfo().getEmployeeInfoId())
								.month(month.getValue()).build();
					}).collect(Collectors.toList());
				}).flatMap(Collection::stream).distinct()
				.collect(Collectors.toMap(SalaryDTO::getEmployeeInfoId, SalaryDTO::getMonth));

		salarydetails = salaryDetailsRepo.findByCompanyInfoCompanyIdAndIsPaidAndIsFinalizedAndIsPayslipGenerated(
				companyInfo.getCompanyId(), true, true, false);

		salarydetails = salarydetails.stream().filter(salary -> salary.getEmployeePersonalInfo()
				.getEmployeeInfoId() != null
				&& (employeeMonthlyPayDetails.get(salary.getEmployeePersonalInfo().getEmployeeInfoId()) == null
						|| !Objects.equals(
								employeeMonthlyPayDetails.get(salary.getEmployeePersonalInfo().getEmployeeInfoId()),
								salary.getMonth())))
				.collect(Collectors.toList());
		addData = addData(salarydetails);
		log.info(EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY);
		return addData;
	}

	private List<AccountPaySlipListDTO> addData(List<EmployeeSalaryDetails> salarydetails) {

		ArrayList<AccountPaySlipListDTO> paySalList = new ArrayList<>();
		for (EmployeeSalaryDetails employeeSalaryDetails : salarydetails) {
			AccountPaySlipListDTO accountPaySlipListDTO = new AccountPaySlipListDTO();
			accountPaySlipListDTO.setStatus("Paid");
			BeanUtils.copyProperties(employeeSalaryDetails, accountPaySlipListDTO);
			EmployeeOfficialInfo employeeOfficialInfo = employeeSalaryDetails.getEmployeePersonalInfo()
					.getEmployeeOfficialInfo();
			accountPaySlipListDTO
					.setEmployeeId(employeeOfficialInfo == null ? null : employeeOfficialInfo.getEmployeeId());
			accountPaySlipListDTO.setFullname(employeeSalaryDetails.getEmployeePersonalInfo().getFirstName() + " "
					+ employeeSalaryDetails.getEmployeePersonalInfo().getLastName());
			accountPaySlipListDTO.setMonth(Month.of(employeeSalaryDetails.getMonth()).name());
			accountPaySlipListDTO.setNetPay(employeeSalaryDetails.getNetPay());
			paySalList.add(accountPaySlipListDTO);
		}
		return paySalList.stream().filter(x -> x.getEmployeeId() != null).collect(Collectors.toList());
	}

	@Override
	public List<EmployeeReviseSalaryDTO> reviseSalary(Long companyId) {
		CompanyInfo companyInfo = companyInfoRepo.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException(COMPANY_INFORMATION_NOT_PRESENT));
		log.info("company info verify");
		ArrayList<EmployeeReviseSalaryDTO> dropDownList = new ArrayList<>();
		List<EmployeeReviseSalary> employeedetails = employeeReviseSalaryRepository
				.findByCompanyInfoCompanyIdAndRevisedDateNull(companyInfo.getCompanyId());

		for (EmployeeReviseSalary employeeReviseSalary : employeedetails) {
			EmployeePersonalInfo employeePersonalInfo = employeeReviseSalary.getEmployeePersonalInfo();
			EmployeeOfficialInfo employeeOfficialInfo = employeeReviseSalary.getEmployeePersonalInfo()
					.getEmployeeOfficialInfo();

			if (employeePersonalInfo != null && employeeOfficialInfo != null) {
				dropDownList.add(new EmployeeReviseSalaryDTO(employeeReviseSalary.getReviseSalaryId(),
						employeeOfficialInfo.getEmployeeId(), companyId,
						employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName(),
						employeeOfficialInfo.getDesignation(), employeeOfficialInfo.getDepartment(),
						employeeReviseSalary.getAmount(), employeeReviseSalary.getReason()));

			}

		}
		return dropDownList;
	}

}
