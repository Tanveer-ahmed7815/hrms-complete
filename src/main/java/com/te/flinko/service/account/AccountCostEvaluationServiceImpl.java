package com.te.flinko.service.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.account.AccountCostEvaluationDTO;
import com.te.flinko.dto.employee.EmployeeReimbursementExpenseCategoryDTO;
import com.te.flinko.entity.account.CompanyCostEvaluation;
import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admindept.CompanyStockGroupItems;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeeReimbursementInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.entity.it.CompanyHardwareItems;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.account.CompanyCostEvaluationRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admindept.CompanyHardwareItemsRepository;
import com.te.flinko.repository.admindept.CompanyPCLaptopRepository;
import com.te.flinko.repository.admindept.CompanyPurchaseOrderRepository;
import com.te.flinko.repository.admindept.CompanyStockGroupItemsRepository;
import com.te.flinko.repository.employee.EmployeeReimbursementInfoRepository;
import com.te.flinko.repository.employee.EmployeeSalaryDetailsRepository;
import com.te.flinko.service.employee.EmployeeReimbursementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCostEvaluationServiceImpl implements AccountCostEvaluationService {

	private static final String PARTTIME = "Part Time";

	private static final String FULLTIME = "Full Time";

	private static final String SALARY = "Salary";

	private static final String ASSET = "Asset";

	private static final String OVERHEADEXPENSE = "Overhead Expense";

	private static final String HARDWARE = "Hardware";

	private static final String SOFTWARE = "Software";

	private final EmployeeReimbursementService employeeReimbursementService;

	private final EmployeeSalaryDetailsRepository employeeSalaryDetailsRepository;

	private final CompanyInfoRepository companyInfoRepository;

	private final CompanyCostEvaluationRepository companyCostEvaluationRepository;

	private final CompanyPurchaseOrderRepository companyPurchaseOrderRepository;

	private final CompanyPCLaptopRepository companyPCLaptopRepository;

	private final CompanyStockGroupItemsRepository companyStockGroupItemsRepository;

	private final CompanyHardwareItemsRepository companyHardwareItemsRepository;

	private final EmployeeReimbursementInfoRepository employeeReimbursementInfoRepository;

	public Map<String, BigDecimal> getAccountCostEvaluation(int year,Long companyId) {
		try {
			log.error("company id {}, is paid {} and current year {}", companyId, Boolean.TRUE,
					year);

			Map<String, BigDecimal> totalManPower = employeeSalaryDetailsRepository
					.findByCompanyInfoCompanyIdAndIsPaidAndYear(companyId, Boolean.TRUE, year)
					.stream().filter(x -> x.getEmployeePersonalInfo() != null
							&& x.getEmployeePersonalInfo().getEmployeeOfficialInfo() != null)
					.map(employees -> {
						EmployeeOfficialInfo employeeOfficialInfo = employees.getEmployeePersonalInfo()
								.getEmployeeOfficialInfo();
						String employeeType = employeeOfficialInfo.getEmployeeType() == null
								|| employeeOfficialInfo.getEmployeeType().equalsIgnoreCase("Permanent") ? "Full Time"
										: employeeOfficialInfo.getEmployeeType();
						employeeOfficialInfo.setEmployeeType(employeeType);
						return employees;
					})
					.collect(Collectors.groupingBy(
							emp -> emp.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeType(),
							() -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
							Collectors.mapping(EmployeeSalaryDetails::getTotalSalary, Collectors
									.reducing(new BigDecimal(0), (subtotal, element) -> subtotal.add(element)))));

			Map<String, BigDecimal> totalHSCost = companyPurchaseOrderRepository
					.findByCompanyInfoCompanyIdAndTotalPayableAmountNotNull(companyId).stream()
					.filter(rem -> rem.getCreatedDate() != null
							&& rem.getCreatedDate().getYear() == year)
					.collect(Collectors.groupingBy(CompanyPurchaseOrder::getType,
							() -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
							Collectors.mapping(CompanyPurchaseOrder::getTotalPayableAmount,
									Collectors.reducing(new BigDecimal(0), (x, y) -> x.add(y)))));

			BigDecimal reimbursement = employeeReimbursementInfoRepository
					.findByStatusIgnoreCaseAndEmployeePersonalInfoCompanyInfoCompanyId("APPROVED", companyId).stream()
					.filter(rem -> rem.getCreatedDate() != null
							&& rem.getCreatedDate().getYear() == year)
					.map(EmployeeReimbursementInfo::getAmount).reduce(new BigDecimal(0), (x, y) -> x.add(y));

			BigDecimal companyStockGrItems = companyStockGroupItemsRepository.findByCompanyInfoCompanyId(companyId)
					.stream()
					.filter(rem -> rem.getCreatedDate() != null
							&& rem.getCreatedDate().getYear() == year)
					.map(CompanyStockGroupItems::getAmount).reduce(new BigDecimal(0), (sum, x) -> sum.add(x));

			BigDecimal pclaptop = companyPCLaptopRepository.findByCompanyInfoCompanyId(companyId).stream().filter(
					rem -> rem.getCreatedDate() != null && rem.getCreatedDate().getYear() == year)
					.map(CompanyPcLaptopDetails::getAmount).reduce(new BigDecimal(0), (sum, x) -> sum.add(x));

			BigDecimal otherHardware = companyHardwareItemsRepository.findByCompanyInfoCompanyId(companyId).stream()
					.filter(rem -> rem.getCreatedDate() != null
							&& rem.getCreatedDate().getYear() == year)
					.map(CompanyHardwareItems::getAmount).reduce(new BigDecimal(0), (sum, x) -> sum.add(x));

			companyStockGrItems = companyStockGrItems.add(pclaptop);
			companyStockGrItems = companyStockGrItems.add(otherHardware);

			Map<String, BigDecimal> inbuildCategory = inbuildCategory(totalHSCost, totalManPower, reimbursement,
					companyStockGrItems);

			List<CompanyCostEvaluation> companyCostEvaluations = companyCostEvaluationRepository
					.findByCompanyInfoCompanyId(companyId);
			Map<String, BigDecimal> inbuildCat = new LinkedHashMap<>(new TreeMap<>(inbuildCategory));

			companyCostEvaluations.stream()
					.filter(cost -> cost.getCategory() != null && inbuildCat.containsKey(cat(cost.getCategory())) && cost.getCreatedDate().getYear()==year)
					.forEach(c -> {
						CompanyCostEvaluation cat = getCompanyCost(c);
						if (inbuildCat.get(cat.getCategory()) != null) {
							inbuildCat.put(cat.getCategory(), inbuildCat.get(cat.getCategory()).add(cat.getAmount()));
						}
					});

			Map<String, BigDecimal> other = companyCostEvaluations.stream()
					.filter(cost -> cost.getCategory() != null && !inbuildCat.containsKey(cat(cost.getCategory())) &&  cost.getCreatedDate().getYear()==year)
					.map(this::getCompanyCost)
					.collect(Collectors.groupingBy(CompanyCostEvaluation::getCategory,
							() -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
							Collectors.mapping(CompanyCostEvaluation::getAmount,
									Collectors.reducing(new BigDecimal(0), (sum, x) -> sum.add(x)))));
			inbuildCat.putAll(other);
			return inbuildCat;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String cat(String cat) {
		return Stream.of(cat.trim().split(" ")).map(c -> c.substring(0, 1).toUpperCase() + c.substring(1))
				.collect(Collectors.joining(" "));
	}

	private List<String> inbulidCategories = List.of(SALARY, FULLTIME, PARTTIME, OVERHEADEXPENSE, SOFTWARE, HARDWARE,
			ASSET);

	@Transactional
	@Override
	public String addAccountCostEvaluation(AccountCostEvaluationDTO accountCostEvaluationDTO, Long companyId) {
		List<EmployeeReimbursementExpenseCategoryDTO> findByExpenseCategoryId = employeeReimbursementService
				.findByExpenseCategoryId(companyId);

		List<String> categories = findByExpenseCategoryId.stream()
				.map(EmployeeReimbursementExpenseCategoryDTO::getExpenseCategoryName).collect(Collectors.toList());
		categories.addAll(inbulidCategories);

		Optional<CompanyCostEvaluation> costEvaluation = companyCostEvaluationRepository
				.findByCompanyInfoCompanyIdAndCategoryIgnoreCase(companyId, accountCostEvaluationDTO.getCategory());

		if ((accountCostEvaluationDTO.getDeduction() == null && accountCostEvaluationDTO.getAdditional() == null)
				&& categories.contains(accountCostEvaluationDTO.getCategory())
				&& accountCostEvaluationDTO.getAmount() != null && costEvaluation.isPresent()
				&& costEvaluation.get().getAmount().doubleValue() > 0) {
			throw new DataNotFoundException(accountCostEvaluationDTO.getCategory() + " category already added");
		}

		CompanyInfo companyInfo = companyInfoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company not found"));

		return costEvaluation.map(x -> {
			Long costEvaluationId = x.getCostEvaluationId();
			CompanyInfo companyInfo2 = x.getCompanyInfo();
			BeanUtils.copyProperties(accountCostEvaluationDTO, x);
			x.setCostEvaluationId(costEvaluationId);
			x.setCompanyInfo(companyInfo2);
			return "Cost evaluation updated successfully";
		}).orElseGet(() -> {
			CompanyCostEvaluation companyCostEvaluation = BeanCopy.objectProperties(accountCostEvaluationDTO,
					CompanyCostEvaluation.class);
			companyCostEvaluation.setCompanyInfo(companyInfo);
			companyCostEvaluationRepository.save(companyCostEvaluation);
			return inbulidCategories.contains(accountCostEvaluationDTO.getCategory().toLowerCase())
					? "Cost evaluation updated successfully"
					: "Cost evaluation added successfully";
		});
	}

	public Map<String, BigDecimal> inbuildCategory(Map<String, BigDecimal> totalHSCost,
			Map<String, BigDecimal> totalManPower, BigDecimal reimbursement, BigDecimal companyStockGrItems) {

		return Map
				.of(SOFTWARE, totalHSCost.get(SOFTWARE) == null ? BigDecimal.ZERO : totalHSCost.get(SOFTWARE), HARDWARE,
						totalHSCost.get(HARDWARE) == null ? BigDecimal.ZERO : totalHSCost.get(HARDWARE), SALARY,
						totalManPower.isEmpty() ? BigDecimal.ZERO
								: totalManPower.entrySet().stream().map(Entry::getValue).reduce(new BigDecimal(0),
										(sum, y) -> sum.add(y)),
						OVERHEADEXPENSE, reimbursement == null ? BigDecimal.ZERO : reimbursement, ASSET,
						companyStockGrItems == null ? BigDecimal.ZERO : companyStockGrItems, FULLTIME,
						totalManPower.get("full time") == null ? BigDecimal.ZERO : totalManPower.get("full time"),
						PARTTIME,
						totalManPower.get("part time") == null ? BigDecimal.ZERO : totalManPower.get("part time"))
				.entrySet().stream().filter(v -> v.getValue().doubleValue() > 0)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

	}

	private int getDurationDif(CompanyCostEvaluation cost) {
		String duration = cost.getDuration();
		LocalDateTime lastModifiedDate = cost.getCreatedDate();
		int dayOfYearDif = LocalDateTime.now().getDayOfYear() - lastModifiedDate.getDayOfYear();
		int monthValueDif = LocalDateTime.now().getMonthValue() - lastModifiedDate.getMonthValue();
		int yearDif = LocalDateTime.now().getYear() - lastModifiedDate.getYear();

		return Optional.of(duration.equalsIgnoreCase("daily")).filter(t -> t).map(x -> dayOfYearDif).orElseGet(
				() -> Optional.of(duration.equalsIgnoreCase("weekly")).filter(t -> t).map(x -> dayOfYearDif / 7)
						.orElseGet(() -> Optional.of(duration.equalsIgnoreCase("monthly")).filter(t -> t)
								.map(x -> yearDif == 0 ? monthValueDif : yearDif * 12 + monthValueDif)
								.orElseGet(() -> yearDif)));
	}

	@Override
	public AccountCostEvaluationDTO getSingleCostEvaluation(String cat, Long companyId) {
		CompanyCostEvaluation costEvaluation = companyCostEvaluationRepository
				.findByCompanyInfoCompanyIdAndCategoryIgnoreCase(companyId, cat)
				.orElseGet(() -> CompanyCostEvaluation.builder().category(cat).build());
		AccountCostEvaluationDTO accountCostEvaluationDTO = new AccountCostEvaluationDTO();
		BeanUtils.copyProperties(costEvaluation, accountCostEvaluationDTO);
		return accountCostEvaluationDTO;
	}

	public CompanyCostEvaluation getCompanyCost(CompanyCostEvaluation cost) {
		int dur = getDurationDif(cost);
		BigDecimal mul = new BigDecimal(dur == 0 ? 1 : dur);
		BigDecimal addi = cost.getAdditional() == null ? BigDecimal.ZERO : cost.getAdditional().multiply(mul);
		BigDecimal dec = cost.getDeduction() == null ? BigDecimal.ZERO
				: cost.getDeduction().multiply(BigDecimal.valueOf(-1)).multiply(mul);
		BigDecimal amt = cost.getAmount() == null ? BigDecimal.ZERO : cost.getAmount();
		cost.setAmount(addi.add(dec).add(amt));
		return cost;
	}
}
