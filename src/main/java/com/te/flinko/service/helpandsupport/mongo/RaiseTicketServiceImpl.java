package com.te.flinko.service.helpandsupport.mongo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.audit.Audit;
import com.te.flinko.audit.Audit;
import com.te.flinko.audit.common.db.DBConstants;
import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.constants.admin.AdminConstants;
import com.te.flinko.constants.admindept.AdminDeptConstants;
import com.te.flinko.dto.account.mongo.CompanyAccountTicketsDTO;
import com.te.flinko.dto.helpandsupport.mongo.CompanyTicketDto;
import com.te.flinko.dto.helpandsupport.mongo.ITProductNameDTO;
import com.te.flinko.dto.helpandsupport.mongo.ProductNameDTO;
import com.te.flinko.dto.helpandsupport.mongo.RaiseTicketDto;
import com.te.flinko.dto.helpandsupport.mongo.ReportingManagerDto;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.hr.EmployeeInformationDTO;
import com.te.flinko.dto.hr.EventManagementDepartmentNameDTO;
import com.te.flinko.dto.hr.mongo.CompanyHrTicketsDTO;
import com.te.flinko.dto.hr.mongo.TicketHistoryDTO;
import com.te.flinko.entity.Department;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAccountTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAdminDeptTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyHrTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyItTickets;
import com.te.flinko.entity.helpandsupport.mongo.SuperAdminTickets;
import com.te.flinko.entity.it.CompanyHardwareItems;
import com.te.flinko.exception.CompanyIdNotFoundException;
import com.te.flinko.exception.admin.NoCompanyPresentException;
import com.te.flinko.exception.admin.NoDataPresentException;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.exception.employee.EmployeeNotFoundException;
import com.te.flinko.exception.helpandsupport.EmployeeNotActiveException;
import com.te.flinko.exception.helpandsupport.TicketAlreadyRaisedException;
import com.te.flinko.exception.helpandsupport.WrongAttachmentFileException;
import com.te.flinko.exception.hr.CompanyNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admindept.CompanyHardwareItemsRepository;
import com.te.flinko.repository.admindept.CompanyItTicketsRepository;
import com.te.flinko.repository.employee.EmployeeOfficialInfoRepository;
import com.te.flinko.repository.employee.EmployeePersonelInfoRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAccountTicketsRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAdminDeptTicketsRepo;
import com.te.flinko.repository.helpandsupport.mongo.CompanyHrTicketsRepository;
import com.te.flinko.repository.superadmin.mongo.SuperAdminTicketsRepository;
import com.te.flinko.util.Generator;
import com.te.flinko.util.S3UploadFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class RaiseTicketServiceImpl implements RaiseTicketService {

	private static final String RESOLVED = "Resolved";

	private static final String ACCOUNT_TICKET_NOT_FOUND_WITH_RESPECT_TO_COMPANY_ID_AND_MONITORING_DEPARTMENT = "Account Ticket not found with respect to company id and monitoring department";

	private static final String CREATED = "Created";

	private static final String SALES_PURCHASE = "salesPurchase";

	private static final String FOR_EMPLOYEE_ID = " for  ";

	private static final String FOR_SUB_CATEGORY = " in ";

	private static final String FOR_CATEGORY = "for";

	private static final String ALREADY_PRESENT = "Already ticket raised ";

	private static final String EMPLOYEE_NOT_ACTIVE = "Employee not active";

	private static final String EMPLOYEE_NOT_FOUND = "Employee not found";

	private static final String WRONG_ATTACHMENT_FILE = "Wrong attachment file";

	private Map<String, String> attachment;

	@Autowired
	private CompanyHardwareItemsRepository companyHardwareItemsRepository;

	@Autowired
	private CompanyInfoRepository infoRepository;

	@Autowired
	private SuperAdminTicketsRepository superAdminTicketsRepository;

	@Autowired
	private CompanyAccountTicketsRepository companyAccountTicketsRepository;

	@Autowired
	private CompanyAdminDeptTicketsRepo adminDeptTicketsRepo;

	@Autowired
	private EmployeePersonelInfoRepository employeePersonelInfoRepository;

	@Autowired
	private EmployeeOfficialInfoRepository employeeOfficialInfoRepository;

	@Autowired
	private CompanyItTicketsRepository companyItTicketsRepository;

	@Autowired
	private CompanyHrTicketsRepository companyHrTicketsRepository;

	@Autowired
	private Generator generator;

	@Autowired
	private S3UploadFile uploadFileService;

	@Override
	@Transactional
	public boolean createTickets(Long companyId, Long employeeInfoId, List<MultipartFile> files,
			RaiseTicketDto raiseTicketDto) {

		attachment = new HashMap<>();

		log.info("service method createTickets is called");
		CompanyInfo companyInfo = infoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new CompanyNotFoundException("Company not found"));

		if (raiseTicketDto.getDepartment().equalsIgnoreCase("super admin")) {
			return createSuperAdminTicket(companyInfo.getCompanyId(), raiseTicketDto, employeeInfoId, files,
					companyInfo.getCompanyName());
		}

		if (raiseTicketDto.getDepartment().equalsIgnoreCase("ACCOUNT")) {

			return createAccountTicket(companyInfo.getCompanyId(), raiseTicketDto, employeeInfoId, files);

		}
		if (raiseTicketDto.getDepartment().equalsIgnoreCase("ADMIN")) {

			return createAdminTicket(companyInfo.getCompanyId(), raiseTicketDto, employeeInfoId, files);

		}
		if (raiseTicketDto.getDepartment().equalsIgnoreCase("IT")) {

			return saveItTickets(companyInfo.getCompanyId(), employeeInfoId, files, raiseTicketDto);
		}
		if (raiseTicketDto.getDepartment().equalsIgnoreCase("HR")) {

			return saveHrTickets(companyInfo.getCompanyId(), employeeInfoId, files, raiseTicketDto);
		}

		return true;
	}

	private String checkFile(MultipartFile multipartFile) {
		if (attachment.containsKey(multipartFile.getOriginalFilename())) {
			return attachment.get(multipartFile.getOriginalFilename());
		} else {
			String uploadFile = uploadFileService.uploadFile(multipartFile);
			attachment.put(multipartFile.getOriginalFilename(), uploadFile);
			return uploadFile;
		}
	}

	private boolean createAccountTicket(Long companyId, RaiseTicketDto raiseTicketDto, Long employeeInfoId,
			List<MultipartFile> listOfMultipartFile) {

		List<CompanyAccountTickets> accountTicketsList = new ArrayList<>();

		List<String> employees = raiseTicketDto.getCompanyTicketDto().stream().filter(x -> x.getEmployeeId() != null)
				.map(CompanyTicketDto::getEmployeeId).collect(Collectors.toList());

		if (raiseTicketDto.getCompanyTicketDto().stream()
				.noneMatch(x -> x.getCategory().equalsIgnoreCase(SALES_PURCHASE))) {
			List<EmployeeOfficialInfo> employeeIdIn = employeeOfficialInfoRepository.findByEmployeeIdIn(employees);
			if (employeeIdIn.isEmpty() && employeeIdIn.get(0) == null) {
				throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND);
			}
		}

		List<String> identificationList = raiseTicketDto.getCompanyTicketDto().stream()
				.filter(x -> x.getCategory().equalsIgnoreCase(SALES_PURCHASE))
				.map(CompanyTicketDto::getIdentificationNumber).collect(Collectors.toList());

		if (!identificationList.isEmpty() && identificationList.size() != new HashSet<>(identificationList).size()) {
			throw new TicketAlreadyRaisedException("Duplicate unique number");
		}

		Optional.of(
				raiseTicketDto.getCompanyTicketDto().stream()
						.filter(x -> x.getCategory().equalsIgnoreCase(SALES_PURCHASE)).map(
								CompanyTicketDto::getIdentificationNumber)
						.collect(Collectors.toList()))
				.ifPresent(identificationNumberList -> companyAccountTicketsRepository
						.findByCategoryAndCompanyIdAndIdentificationNumberIn(SALES_PURCHASE, companyId,
								identificationNumberList)
						.filter(List::isEmpty).orElseThrow(() -> {
							throw new TicketAlreadyRaisedException(
									ALREADY_PRESENT + FOR_CATEGORY + split(SALES_PURCHASE));
						}));

		raiseTicketDto.getCompanyTicketDto().stream().filter(x -> x.getSubCategory() == null)
				.collect(Collectors.toList()).forEach(x -> x.setSubCategory(x.getCategory()));

		for (CompanyTicketDto companyTicketDto2 : raiseTicketDto.getCompanyTicketDto()) {

			CompanyAccountTickets accountTickets = BeanCopy.objectProperties(companyTicketDto2,
					CompanyAccountTickets.class);

			List<CompanyAccountTickets> findByCategoryAndSubCategoryAndCompanyIdAndDepartment = companyAccountTicketsRepository
					.findByCategoryAndSubCategoryAndCompanyIdAndEmployeeIdAndTicketHistroysDateAndIdentificationNumber(
							accountTickets.getCategory(), accountTickets.getSubCategory(), companyId,
							accountTickets.getEmployeeId(), LocalDate.now(), accountTickets.getIdentificationNumber());

			if (!findByCategoryAndSubCategoryAndCompanyIdAndDepartment.isEmpty()) {

				throw new TicketAlreadyRaisedException(ALREADY_PRESENT + FOR_CATEGORY
						+ split(findByCategoryAndSubCategoryAndCompanyIdAndDepartment.get(0).getCategory())
						+ FOR_SUB_CATEGORY
						+ split(findByCategoryAndSubCategoryAndCompanyIdAndDepartment.get(0).getSubCategory())
						+ FOR_EMPLOYEE_ID
						+ findByCategoryAndSubCategoryAndCompanyIdAndDepartment.get(0).getEmployeeId());
			}

			List<TicketHistroy> list = List.of(TicketHistroy.builder().by(employeeInfoId).date(LocalDate.now())
					.department(raiseTicketDto.getDepartment()).status(CREATED).build());

			accountTickets.setCompanyId(companyId);
			accountTickets.setTicketId(generator.idGenerator(1l, DBConstants.ACCOUNT_TICKET_SEQUENCE_NAME));
			accountTickets.setTicketHistroys(list);

			if (listOfMultipartFile != null && !listOfMultipartFile.isEmpty()
					&& companyTicketDto2.getAttachmentsUrl() != null
					&& !"".equals(companyTicketDto2.getAttachmentsUrl())) {

				Optional<MultipartFile> relativeFile = listOfMultipartFile.stream()
						.filter(x -> x.getOriginalFilename().equalsIgnoreCase(accountTickets.getAttachmentsUrl()))
						.findFirst();
				if (relativeFile.isEmpty()) {
					throw new WrongAttachmentFileException(WRONG_ATTACHMENT_FILE);
				}

				String uploadFile = checkFile(relativeFile.get());

				accountTickets.setAttachmentsUrl(uploadFile);
			}
			accountTicketsList.add(accountTickets);
		}

		return !companyAccountTicketsRepository.saveAll(accountTicketsList).isEmpty();
	}

	private boolean createAdminTicket(Long companyId, RaiseTicketDto raiseTicketDto, Long employeeInfoId,
			List<MultipartFile> listOfMultipartFile) {

		raiseTicketDto.getCompanyTicketDto().stream().filter(x -> x.getSubCategory() == null)
				.collect(Collectors.toList()).forEach(x -> x.setSubCategory(x.getCategory()));

		List<CompanyAdminDeptTickets> adminDeptTicketsList = new ArrayList<>();
		for (CompanyTicketDto companyTicketDto2 : raiseTicketDto.getCompanyTicketDto()) {

			CompanyAdminDeptTickets adminDeptTicket = BeanCopy.objectProperties(companyTicketDto2,
					CompanyAdminDeptTickets.class);

			List<CompanyAdminDeptTickets> findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId = adminDeptTicketsRepo
					.findByCategoryAndSubCategoryAndCompanyIdAndEmployeeIdAndTicketHistroysDate(
							adminDeptTicket.getCategory(), adminDeptTicket.getSubCategory(), companyId,
							adminDeptTicket.getEmployeeId(), LocalDate.now());

			if (!findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.isEmpty()) {
				throw new TicketAlreadyRaisedException(ALREADY_PRESENT + FOR_CATEGORY
						+ split(findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.get(0).getCategory())
						+ FOR_SUB_CATEGORY
						+ split(findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.get(0).getSubCategory())
						+ FOR_EMPLOYEE_ID
						+ findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.get(0).getEmployeeId());
			}

			List<TicketHistroy> list = List.of(TicketHistroy.builder().by(employeeInfoId).date(LocalDate.now())
					.department(raiseTicketDto.getDepartment()).status(CREATED).build());

			adminDeptTicket.setCompanyId(companyId);
			adminDeptTicket.setTicketHistroys(list);
			adminDeptTicket.setTicketId(generator.idGenerator(2l, DBConstants.ADMIN_DEPARTMENT_TICKET_SEQUENCE_NAME));
			if (listOfMultipartFile != null && !listOfMultipartFile.isEmpty()
					&& companyTicketDto2.getAttachmentsUrl() != null
					&& !"".equals(companyTicketDto2.getAttachmentsUrl())) {

				Optional<MultipartFile> relativeFile = listOfMultipartFile.stream()
						.filter(x -> x.getOriginalFilename().equalsIgnoreCase(adminDeptTicket.getAttachmentsUrl()))
						.findFirst();
				if (relativeFile.isEmpty()) {
					throw new WrongAttachmentFileException(WRONG_ATTACHMENT_FILE);
				}

				String uploadFile = checkFile(relativeFile.get());

				adminDeptTicket.setAttachmentsUrl(uploadFile);
			}
			adminDeptTicketsList.add(adminDeptTicket);
		}
		for (CompanyAdminDeptTickets adminDeptTicket1 : adminDeptTicketsList)
			adminDeptTicketsRepo.save(adminDeptTicket1);
		return true;
	}

	private boolean createSuperAdminTicket(Long companyId, RaiseTicketDto raiseTicketDto, Long employeeInfoId,
			List<MultipartFile> listOfMultipartFile, String companyName) {

		List<SuperAdminTickets> superAdminDeptTicketsList = new ArrayList<>();
		for (CompanyTicketDto companyTicketDto2 : raiseTicketDto.getCompanyTicketDto()) {

			EmployeePersonalInfo employeePersonal = employeePersonelInfoRepository.findById(employeeInfoId)
					.orElseThrow(() -> new CompanyNotFoundException("employee not found"));

			Optional<EmployeeOfficialInfo> employeeOfficial = employeeOfficialInfoRepository
					.findById(employeePersonal.getEmployeeOfficialInfo().getOfficialId());

			if (employeeOfficial.isPresent() && !employeeOfficial.get().getDepartment().equalsIgnoreCase("ADMIN")) {
				throw new CompanyNotFoundException("only admin can raise ticket for superadmin");
			}

			SuperAdminTickets superAdminDeptTicket = BeanCopy.objectProperties(companyTicketDto2,
					SuperAdminTickets.class);

			List<SuperAdminTickets> findByCategoryAndCompanyIdAndTicketHistroysDate = superAdminTicketsRepository
					.findByCategoryAndCompanyIdAndTicketHistroysDate(superAdminDeptTicket.getCategory(), companyId,
							LocalDate.now());

			if (!findByCategoryAndCompanyIdAndTicketHistroysDate.isEmpty()) {
				throw new TicketAlreadyRaisedException(ALREADY_PRESENT + FOR_CATEGORY
						+ split(findByCategoryAndCompanyIdAndTicketHistroysDate.get(0).getCategory()));

			}

			List<TicketHistroy> list = List.of(TicketHistroy.builder().by(employeeInfoId).date(LocalDate.now())
					.department(raiseTicketDto.getDepartment()).status(CREATED).build());

			superAdminDeptTicket.setCompanyId(companyId);
			superAdminDeptTicket.setCompanyName(companyName);
			superAdminDeptTicket.setTicketHistroys(list);
			superAdminDeptTicket
					.setTicketId(generator.idGenerator(2l, DBConstants.SUPER_ADMIN_DEPARTMENT_TICKET_SEQUENCE_NAME));
			if (listOfMultipartFile != null && !listOfMultipartFile.isEmpty()
					&& companyTicketDto2.getAttachmentsUrl() != null
					&& !"".equals(companyTicketDto2.getAttachmentsUrl())) {

				Optional<MultipartFile> relativeFile = listOfMultipartFile.stream()
						.filter(x -> x.getOriginalFilename().equalsIgnoreCase(superAdminDeptTicket.getAttachmentsUrl()))
						.findFirst();
				if (relativeFile.isEmpty()) {
					throw new WrongAttachmentFileException(WRONG_ATTACHMENT_FILE);
				}

				String uploadFile = checkFile(relativeFile.get());

				superAdminDeptTicket.setAttachmentsUrl(uploadFile);
			}
			superAdminDeptTicketsList.add(superAdminDeptTicket);
		}
		for (SuperAdminTickets superAdminDeptTicket1 : superAdminDeptTicketsList)
			superAdminTicketsRepository.save(superAdminDeptTicket1);
		return true;
	}

	private boolean saveItTickets(Long companyId, Long employeeInfoId, List<MultipartFile> listOfMultipartFile,
			RaiseTicketDto raiseTicketDto) {

		raiseTicketDto.getCompanyTicketDto().stream().filter(x -> x.getSubCategory() == null)
				.collect(Collectors.toList()).forEach(x -> x.setSubCategory(x.getCategory()));

		List<CompanyItTickets> companyItTickets1 = new ArrayList<>();

		for (CompanyTicketDto companyTicketDto : raiseTicketDto.getCompanyTicketDto()) {

			List<EmployeePersonalInfo> employeePersonalInfo = employeePersonelInfoRepository
					.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId,
							companyTicketDto.getEmployeeId());

			if (employeePersonalInfo.isEmpty() && employeePersonalInfo.get(0) == null) {
				throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND);
			}

			EmployeePersonalInfo employeePersonalInfo1 = employeePersonalInfo.get(0);

			if (Boolean.FALSE.equals(employeePersonalInfo1.getIsActive())) {
				throw new EmployeeNotActiveException(EMPLOYEE_NOT_ACTIVE);
			}

			CompanyItTickets companyItTickets = BeanCopy.objectProperties(companyTicketDto, CompanyItTickets.class);

			List<CompanyItTickets> companyItTicketsExist = companyItTicketsRepository
					.findByCategoryAndSubCategoryAndCompanyIdAndEmployeeIdAndTicketHistroysDate(
							companyItTickets.getCategory(), companyItTickets.getSubCategory(), companyId,
							companyItTickets.getEmployeeId(), LocalDate.now());

			if (!companyItTicketsExist.isEmpty()) {
				throw new TicketAlreadyRaisedException(
						ALREADY_PRESENT + FOR_CATEGORY + split(companyItTicketsExist.get(0).getCategory())
								+ FOR_SUB_CATEGORY + split(companyItTicketsExist.get(0).getSubCategory())
								+ FOR_EMPLOYEE_ID + companyItTicketsExist.get(0).getEmployeeId());
			}

			List<TicketHistroy> ticketHistroys = List.of(TicketHistroy.builder().by(employeeInfoId)
					.date(LocalDate.now()).department(raiseTicketDto.getDepartment()).status(CREATED).build());

			companyItTickets.setTicketHistroys(ticketHistroys);

			companyItTickets.setTicketId(generator.idGenerator(4l, DBConstants.IT_TICKET_SEQUENCE_NAME));

			companyItTickets.setCompanyId(companyId);

			if (listOfMultipartFile != null && !listOfMultipartFile.isEmpty()
					&& companyTicketDto.getAttachmentsUrl() != null
					&& !"".equals(companyTicketDto.getAttachmentsUrl())) {

				Optional<MultipartFile> relativeFile = listOfMultipartFile.stream()
						.filter(x -> x.getOriginalFilename().equalsIgnoreCase(companyItTickets.getAttachmentsUrl()))
						.findFirst();
				if (relativeFile.isEmpty()) {
					throw new WrongAttachmentFileException(WRONG_ATTACHMENT_FILE);
				}

				String uploadFile = checkFile(relativeFile.get());

				companyItTickets.setAttachmentsUrl(uploadFile);
			}
			companyItTickets1.add(companyItTickets);
		}

		for (CompanyItTickets itTickets : companyItTickets1) {
			companyItTicketsRepository.save(itTickets);
		}

		return true;
	}

	private Boolean saveHrTickets(Long companyId, Long employeeInfoId, List<MultipartFile> listOfMultipartFile,
			RaiseTicketDto raiseTicketDto) {

		raiseTicketDto.getCompanyTicketDto().stream().filter(x -> x.getSubCategory() == null)
				.collect(Collectors.toList()).forEach(x -> x.setSubCategory(x.getCategory()));

		List<CompanyHrTickets> companyHrTicketsList = new ArrayList<>();

		for (CompanyTicketDto companyTicketDto : raiseTicketDto.getCompanyTicketDto()) {

			List<EmployeePersonalInfo> employeePersonalInfo = employeePersonelInfoRepository
					.findByCompanyInfoCompanyIdAndEmployeeOfficialInfoEmployeeId(companyId,
							companyTicketDto.getEmployeeId());

			if (employeePersonalInfo.isEmpty() && employeePersonalInfo.get(0) == null) {
				throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND);
			}

			EmployeePersonalInfo employeePersonalInfo1 = employeePersonalInfo.get(0);

			if (Boolean.FALSE.equals(employeePersonalInfo1.getIsActive())) {
				throw new EmployeeNotActiveException(EMPLOYEE_NOT_ACTIVE);
			}

			CompanyHrTickets companyHrTickets = BeanCopy.objectProperties(companyTicketDto, CompanyHrTickets.class);

			List<CompanyHrTickets> findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId = companyHrTicketsRepository
					.findByCategoryAndSubCategoryAndCompanyIdAndEmployeeIdAndTicketHistroysDate(
							companyHrTickets.getCategory(), companyHrTickets.getSubCategory(), companyId,
							companyHrTickets.getEmployeeId(), LocalDate.now());

			if (!findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.isEmpty()) {
				throw new TicketAlreadyRaisedException(ALREADY_PRESENT + FOR_CATEGORY
						+ split(findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.get(0).getCategory())
						+ FOR_SUB_CATEGORY
						+ split(findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.get(0).getSubCategory())
						+ FOR_EMPLOYEE_ID
						+ findByCategoryAndSubCategoryAndCompanyIdAndEmployeeId.get(0).getEmployeeId());
			}

			List<TicketHistroy> ticketHistroys = List.of(TicketHistroy.builder().by(employeeInfoId)
					.date(LocalDate.now()).department(raiseTicketDto.getDepartment()).status(CREATED).build());

			companyHrTickets.setTicketHistroys(ticketHistroys);

			companyHrTickets.setTicketId(generator.idGenerator(3l, DBConstants.HR_TICKET_SEQUENCE_NAME));

			companyHrTickets.setCompanyId(companyId);

			if (listOfMultipartFile != null && !listOfMultipartFile.isEmpty()
					&& companyTicketDto.getAttachmentsUrl() != null
					&& !"".equals(companyTicketDto.getAttachmentsUrl())) {

				Optional<MultipartFile> relativeFile = listOfMultipartFile.stream()
						.filter(x -> x.getOriginalFilename().equalsIgnoreCase(companyHrTickets.getAttachmentsUrl()))
						.findFirst();

				if (relativeFile.isEmpty()) {
					throw new WrongAttachmentFileException(WRONG_ATTACHMENT_FILE);
				}

				String uploadFile = checkFile(relativeFile.get());

				companyHrTickets.setAttachmentsUrl(uploadFile);
			}

			companyHrTicketsList.add(companyHrTickets);
		}
		for (CompanyHrTickets hrTickets : companyHrTicketsList) {
			companyHrTicketsRepository.save(hrTickets);
		}
		return true;

	}

	@Override
	public List<ReportingManagerDto> getAllReportingManagaer(Long companyId, String department) {

		log.info("service method getAllReportingManagaer is called");
		CompanyInfo companyInfo = infoRepository.findById(companyId)
				.orElseThrow(() -> new NoCompanyPresentException(AdminConstants.NO_COMPANY_PRESENT_WITH_ID));
		ArrayList<ReportingManagerDto> arrayList = new ArrayList<>();
		List<ReportingManagerDto> getfrs = employeePersonelInfoRepository.findByCompanyIdAndDepartment(companyId,
				department);
		if (getfrs != null && companyInfo != null) {

			for (ReportingManagerDto reportingManagerDto : getfrs) {

				arrayList.add(new ReportingManagerDto(reportingManagerDto.getEmployeeId(),
						reportingManagerDto.getFirstName(), reportingManagerDto.getLastName()));
			}
		}
		return arrayList;
	}

	@Override
	public ITProductNameDTO getProducts(Long companyId) {
		log.info("Products List with respect to company Id: " + companyId);

		List<CompanyHardwareItems> hardwareItems = companyHardwareItemsRepository.findByCompanyInfoCompanyId(companyId);

		if (hardwareItems.isEmpty()) {
			throw new CompanyIdNotFoundException(AdminDeptConstants.COMPANY_NOT_FOUND);
		}

		ITProductNameDTO itProductNameDTO = new ITProductNameDTO();

		List<ProductNameDTO> distictProductName = hardwareItems.stream().map(y -> {
			ProductNameDTO productNameDTO = new ProductNameDTO();
			productNameDTO.setProductId(y.getIndentificationNumber());
			productNameDTO.setProductName(y.getProductName());
			return productNameDTO;
		}).filter(distinctByKey(ProductNameDTO::getProductName)).collect(Collectors.toList());

		List<ProductNameDTO> productName = hardwareItems.stream().map(y -> {
			ProductNameDTO productNameDTO = new ProductNameDTO();
			productNameDTO.setProductId(y.getIndentificationNumber());
			productNameDTO.setProductName(y.getProductName());
			return productNameDTO;
		}).collect(Collectors.toList());

		itProductNameDTO.setDistinctProductName(distictProductName);

		itProductNameDTO.setProductName(productName);

		return itProductNameDTO;

	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {

		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public String split(String str) {
		if (str.equals("PC"))
			str = str.toLowerCase();
		StringBuilder split = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (c > 96)
				split.append(c);
			else
				split.append(" " + c);
		}
		return new String(split).toLowerCase();
	}

	@Override
	public List<CompanyTicketDto> getDelayedTickets(Long companyId, CompanyTicketDto companyTicketDto) {

		CompanyInfo companyInfo = infoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new CompanyIdNotFoundException("Comapny Not Found"));

		return checkDepartment(companyInfo.getCompanyId(), companyTicketDto);

	}

	private List<CompanyTicketDto> checkDepartment(Long companyId, CompanyTicketDto companyTicketDto) {

		List<CompanyTicketDto> data = new ArrayList<>();

		Map<Long, String> idAndName = new HashMap<>();

		List<Long> employeeInfoIds = new ArrayList<>();

		List<CompanyHrTickets> companyHrTicketsList = companyHrTicketsRepository
				.findByCompanyIdAndMonitoringDepartmentIgnoreCase(companyId,
						companyTicketDto.getMonitoringDepartment());

		employeeInfoIds.addAll(companyHrTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		List<CompanyItTickets> companyItTicketsList = companyItTicketsRepository
				.findByCompanyIdAndMonitoringDepartmentIgnoreCase(companyId,
						companyTicketDto.getMonitoringDepartment());

		employeeInfoIds.addAll(companyItTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		List<CompanyAdminDeptTickets> companyAdminTicketsList = adminDeptTicketsRepo
				.findByCompanyIdAndMonitoringDepartmentIgnoreCase(companyId,
						companyTicketDto.getMonitoringDepartment());

		employeeInfoIds.addAll(companyAdminTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		List<CompanyAccountTickets> companyAccountTicketsList = companyAccountTicketsRepository
				.findByCompanyIdAndMonitoringDepartmentIgnoreCase(companyId,
						companyTicketDto.getMonitoringDepartment());

		employeeInfoIds
				.addAll(companyAccountTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		employeeInfoIds = employeeInfoIds.stream().distinct().collect(Collectors.toList());

		List<EmployeePersonalInfo> employeePersonalInfoList = employeePersonelInfoRepository
				.findByEmployeeInfoIdIn(employeeInfoIds);

		employeePersonalInfoList
				.forEach(x -> idAndName.put(x.getEmployeeInfoId(), x.getFirstName() + " " + x.getLastName()));

		for (CompanyHrTickets companyHrTickets : companyHrTicketsList) {

			CompanyTicketDto hrTicketDto = new CompanyTicketDto();
			LocalDateTime createdDate = companyHrTickets.getCreatedDate();
			LocalDateTime present = LocalDateTime.now();

			Duration duration = Duration.between(createdDate, present);
			long hoursDifference = duration.toHours();
			long minutesDifference = duration.toMinutes();
			List<TicketHistroy> hrTicketHistroys = companyHrTickets.getTicketHistroys();
			if (((hrTicketHistroys != null && !hrTicketHistroys.isEmpty())
					&& hrTicketHistroys.get(hrTicketHistroys.size() - 1).getStatus().equalsIgnoreCase(CREATED))
					&& hoursDifference > 24 && minutesDifference > 60) {
				BeanUtils.copyProperties(companyHrTickets, hrTicketDto);
				hrTicketDto.setId(companyHrTickets.getTicketObjectId());
				hrTicketDto.setTicketId(companyHrTickets.getTicketId());
				hrTicketDto.setTicketOwner(idAndName.get(companyHrTickets.getCreatedBy()));
				hrTicketDto.setDepartment("HR");
				data.add(hrTicketDto);
			}
		}

		for (CompanyItTickets companyItTickets : companyItTicketsList) {
			CompanyTicketDto itTicketDto = new CompanyTicketDto();
			LocalDateTime createdDate = companyItTickets.getCreatedDate();
			LocalDateTime present = LocalDateTime.now();
			Duration duration = Duration.between(createdDate, present);
			long hoursDifference = duration.toHours();
			long minutesDifference = duration.toMinutes();
			List<TicketHistroy> itTicketHistroys = companyItTickets.getTicketHistroys();
			if (((itTicketHistroys != null && !itTicketHistroys.isEmpty())
					&& itTicketHistroys.get(itTicketHistroys.size() - 1).getStatus().equalsIgnoreCase(CREATED))
					&& hoursDifference > 24 && minutesDifference > 60) {
				BeanUtils.copyProperties(companyItTickets, itTicketDto);
				itTicketDto.setId(companyItTickets.getId());
				itTicketDto.setTicketId(companyItTickets.getTicketId());
				itTicketDto.setTicketOwner(idAndName.get(companyItTickets.getCreatedBy()));
				itTicketDto.setDepartment("IT");
				data.add(itTicketDto);
			}
		}

		for (CompanyAdminDeptTickets companyAdminDeptTickets : companyAdminTicketsList) {
			CompanyTicketDto adminTicketDto = new CompanyTicketDto();
			LocalDateTime createdDate = companyAdminDeptTickets.getCreatedDate();
			LocalDateTime present = LocalDateTime.now();

			Duration duration = Duration.between(createdDate, present);
			long hoursDifference = duration.toHours();
			long minutesDifference = duration.toMinutes();
			List<TicketHistroy> adminDeptTicketHistroys = companyAdminDeptTickets.getTicketHistroys();
			if (((adminDeptTicketHistroys != null && !adminDeptTicketHistroys.isEmpty()) && adminDeptTicketHistroys
					.get(adminDeptTicketHistroys.size() - 1).getStatus().equalsIgnoreCase(CREATED))
					&& hoursDifference > 24 && minutesDifference > 60) {
				BeanUtils.copyProperties(companyAdminDeptTickets, adminTicketDto);
				adminTicketDto.setId(companyAdminDeptTickets.getObjectTicketId());
				adminTicketDto.setTicketId(companyAdminDeptTickets.getTicketId());
				adminTicketDto.setTicketOwner(idAndName.get(companyAdminDeptTickets.getCreatedBy()));
				adminTicketDto.setDepartment("ADMIN DEPARTMENT");
				data.add(adminTicketDto);
			}
		}

		for (CompanyAccountTickets companyAccountTickets : companyAccountTicketsList) {
			CompanyTicketDto accountTicketDto = new CompanyTicketDto();
			LocalDateTime createdDate = companyAccountTickets.getCreatedDate();
			LocalDateTime present = LocalDateTime.now();

			Duration duration = Duration.between(createdDate, present);
			long hoursDifference = duration.toHours();
			long minutesDifference = duration.toMinutes();
			List<TicketHistroy> accountTicketHistroys = companyAccountTickets.getTicketHistroys();
			if (((accountTicketHistroys != null && !accountTicketHistroys.isEmpty()) && accountTicketHistroys
					.get(accountTicketHistroys.size() - 1).getStatus().equalsIgnoreCase(CREATED))
					&& hoursDifference > 24 && minutesDifference > 60) {
				BeanUtils.copyProperties(companyAccountTickets, accountTicketDto);
				accountTicketDto.setId(companyAccountTickets.getObjectTicketId());
				accountTicketDto.setTicketId(companyAccountTickets.getTicketId());
				accountTicketDto.setTicketOwner(idAndName.get(companyAccountTickets.getCreatedBy()));
				accountTicketDto.setDepartment("ACCOUNT");
				data.add(accountTicketDto);
			}
		}

		return data;
	}

	@Override
	public List<CompanyTicketDto> getAllTickets(Long companyId, CompanyTicketDto companyTicketDto) {
		CompanyInfo companyInfo = infoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new CompanyIdNotFoundException("Comapny Not Found"));

		return getAllTicket(companyInfo.getCompanyId(), companyTicketDto.getMonitoringDepartment());

	}

	private List<CompanyTicketDto> getAllTicket(Long companyId, String monitoringDepartment) {

		List<CompanyTicketDto> data = new ArrayList<>();

		Map<Long, String> idAndName = new HashMap<>();

		List<Long> employeeInfoIdList = new ArrayList<>();

		List<CompanyAdminDeptTickets> companyAdminDeptTicketsList = adminDeptTicketsRepo
				.findByCompanyIdAndMonitoringDepartmentIgnoreCaseAndTicketHistroysStatusIgnoreCaseNot(companyId,
						monitoringDepartment, RESOLVED);

		employeeInfoIdList
				.addAll(companyAdminDeptTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		List<CompanyHrTickets> companyHrTicketsList = companyHrTicketsRepository
				.findByCompanyIdAndMonitoringDepartmentIgnoreCaseAndTicketHistroysStatusIgnoreCaseNot(companyId,
						monitoringDepartment, RESOLVED);

		employeeInfoIdList.addAll(companyHrTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		List<CompanyItTickets> comapnyItTicketsList = companyItTicketsRepository
				.findByCompanyIdAndMonitoringDepartmentIgnoreCaseAndTicketHistroysStatusIgnoreCaseNot(companyId,
						monitoringDepartment, RESOLVED);

		employeeInfoIdList.addAll(comapnyItTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		List<CompanyAccountTickets> companyAccountTicketsList = companyAccountTicketsRepository
				.findByCompanyIdAndMonitoringDepartmentIgnoreCaseAndTicketHistroysStatusIgnoreCaseNot(companyId,
						monitoringDepartment, RESOLVED);

		employeeInfoIdList
				.addAll(companyAccountTicketsList.stream().map(Audit::getCreatedBy).collect(Collectors.toList()));

		employeeInfoIdList = employeeInfoIdList.stream().distinct().collect(Collectors.toList());

		List<EmployeePersonalInfo> employeePersonalInfoList = employeePersonelInfoRepository
				.findByEmployeeInfoIdIn(employeeInfoIdList);

		employeePersonalInfoList
				.forEach(x -> idAndName.put(x.getEmployeeInfoId(), x.getFirstName() + " " + x.getLastName()));

		for (CompanyItTickets companyItTickets : comapnyItTicketsList) {
			CompanyTicketDto companyTicketDto = new CompanyTicketDto();
			BeanUtils.copyProperties(companyItTickets, companyTicketDto);
			companyTicketDto.setId(companyItTickets.getId());
			companyTicketDto.setTicketId(companyItTickets.getTicketId());
			companyTicketDto.setTicketId(companyItTickets.getTicketId());
			companyTicketDto.setTicketOwner(idAndName.get(companyItTickets.getCreatedBy()));
			companyTicketDto.setDepartment("IT");
			data.add(companyTicketDto);
		}
		for (CompanyHrTickets companyHrTickets : companyHrTicketsList) {
			CompanyTicketDto companyTicketDto = new CompanyTicketDto();
			BeanUtils.copyProperties(companyHrTickets, companyTicketDto);
			companyTicketDto.setId(companyHrTickets.getTicketObjectId());
			companyTicketDto.setTicketId(companyHrTickets.getTicketId());
			companyTicketDto.setTicketOwner(idAndName.get(companyHrTickets.getCreatedBy()));
			companyTicketDto.setDepartment("HR");
			data.add(companyTicketDto);
		}

		for (CompanyAdminDeptTickets companyAdminDeptTickets : companyAdminDeptTicketsList) {
			CompanyTicketDto companyTicketDto = new CompanyTicketDto();
			BeanUtils.copyProperties(companyAdminDeptTickets, companyTicketDto);
			companyTicketDto.setId(companyAdminDeptTickets.getObjectTicketId());
			companyTicketDto.setTicketId(companyAdminDeptTickets.getTicketId());
			companyTicketDto.setTicketOwner(idAndName.get(companyAdminDeptTickets.getCreatedBy()));
			companyTicketDto.setDepartment("ADMIN DEPARTMENT");
			data.add(companyTicketDto);
		}
		for (CompanyAccountTickets companyAccountTickets : companyAccountTicketsList) {
			CompanyTicketDto companyTicketDto = new CompanyTicketDto();
			BeanUtils.copyProperties(companyAccountTickets, companyTicketDto);
			companyTicketDto.setId(companyAccountTickets.getObjectTicketId());
			companyTicketDto.setTicketId(companyAccountTickets.getTicketId());
			companyTicketDto.setTicketOwner(idAndName.get(companyAccountTickets.getCreatedBy()));
			companyTicketDto.setDepartment("ACCOUNT");
			data.add(companyTicketDto);
		}

		return data;
//				
	}

	@Override
	public CompanyTicketDto getTicketsDetails(Long companyId, String id, String department) {
		CompanyInfo companyInfo = infoRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new CompanyNotFoundException("company not found exception"));

		if (department.equalsIgnoreCase("IT")) {
			return itTicketDetails(companyInfo.getCompanyId(), id, department);
		}
		if (department.equalsIgnoreCase("Account")) {
			return accountTicketDetails(companyInfo.getCompanyId(), id, department);
		}
		if (department.equalsIgnoreCase("Admin")) {
			return adminTicketDetails(companyInfo.getCompanyId(), id, department);
		}
		if (department.equalsIgnoreCase("HR")) {
			return hrTicketDetails(companyInfo.getCompanyId(), id, department);
		}
		return null;
	}

	private CompanyTicketDto hrTicketDetails(Long companyId, String id, String department) {

		log.info("Get the details of HR ticket against companyId ::" + companyId + "and ticket object id:: " + id);

		CompanyHrTickets hrTickets = companyHrTicketsRepository.findByCompanyIdAndTicketObjectId(companyId, id)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyTicketDto companyTicketDto = new CompanyTicketDto();

		BeanUtils.copyProperties(hrTickets, companyTicketDto);

		companyTicketDto.setId(hrTickets.getTicketObjectId());
		companyTicketDto.setHrTicketId(hrTickets.getTicketId());

		//
		Optional<EmployeePersonalInfo> findById = employeePersonelInfoRepository.findById(hrTickets.getCreatedBy());

		if (findById.isEmpty()) {
			throw new EmployeeNotFoundException("No employee present");
		}

		//
		companyTicketDto.setTicketOwner(findById.get().getFirstName() + " " + findById.get().getLastName());
		companyTicketDto.setRaisedDate(hrTickets.getCreatedDate().toLocalDate());
		companyTicketDto.setCreatedBy(hrTickets.getCreatedBy());
		companyTicketDto.setTicketId(hrTickets.getTicketId());
		List<TicketHistroy> ticketHistroys = hrTickets.getTicketHistroys();

		List<EmployeePersonalInfo> employeeInfo = employeePersonelInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {
			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());

				}

			}
		}

		return companyTicketDto;

	}

	private CompanyTicketDto adminTicketDetails(Long companyId, String id, String department) {

		log.info("Get the details of Admin Department ticket against companyId ::" + companyId
				+ "and object ticket id:: " + id);

		CompanyAdminDeptTickets adminDeptTickets = adminDeptTicketsRepo.findByCompanyIdAndObjectTicketId(companyId, id)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyTicketDto companyTicketDto = new CompanyTicketDto();

		BeanUtils.copyProperties(adminDeptTickets, companyTicketDto);

		companyTicketDto.setId(adminDeptTickets.getObjectTicketId());
		companyTicketDto.setAdminTicketId(adminDeptTickets.getTicketId());
		companyTicketDto.setTicketId(adminDeptTickets.getTicketId());

		//
		Optional<EmployeePersonalInfo> findById = employeePersonelInfoRepository
				.findById(adminDeptTickets.getCreatedBy());

		if (findById.isEmpty()) {
			throw new EmployeeNotFoundException("No employee present");
		}

		//
		companyTicketDto.setTicketOwner(findById.get().getFirstName() + " " + findById.get().getLastName());
		companyTicketDto.setRaisedDate(adminDeptTickets.getCreatedDate().toLocalDate());
		companyTicketDto.setCreatedBy(adminDeptTickets.getCreatedBy());

		List<TicketHistroy> ticketHistroys = adminDeptTickets.getTicketHistroys();

		List<EmployeePersonalInfo> employeeInfo = employeePersonelInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {
			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());

				}

			}
		}

		return companyTicketDto;

	}

	private CompanyTicketDto accountTicketDetails(Long companyId, String id, String department) {

		log.info("Get the details of Account department ticket against companyId ::" + companyId
				+ "and object ticket id:: " + id);

		CompanyAccountTickets accountTickets = companyAccountTicketsRepository
				.findByCompanyIdAndObjectTicketId(companyId, id)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyTicketDto companyTicketDto = new CompanyTicketDto();

		BeanUtils.copyProperties(accountTickets, companyTicketDto);

		companyTicketDto.setId(accountTickets.getObjectTicketId());
		companyTicketDto.setAccountTicketId(accountTickets.getTicketId());
		companyTicketDto.setTicketId(accountTickets.getTicketId());

		//
		Optional<EmployeePersonalInfo> findById = employeePersonelInfoRepository
				.findById(accountTickets.getCreatedBy());

		if (findById.isEmpty()) {
			throw new EmployeeNotFoundException("No employee present");
		}

		//
		companyTicketDto.setTicketOwner(findById.get().getFirstName() + " " + findById.get().getLastName());
		companyTicketDto.setRaisedDate(accountTickets.getCreatedDate().toLocalDate());
		companyTicketDto.setCreatedBy(accountTickets.getCreatedBy());

		List<TicketHistroy> ticketHistroys = accountTickets.getTicketHistroys();

		List<EmployeePersonalInfo> employeeInfo = employeePersonelInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {
			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());

				}

			}
		}

		return companyTicketDto;

	}

	private CompanyTicketDto itTicketDetails(Long companyId, String id, String department) {

		log.info("Get the details of IT Department tickets against companyId ::" + companyId + "and object ticket id:: "
				+ id);

		CompanyItTickets itTickets = companyItTicketsRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyTicketDto companyTicketDto = new CompanyTicketDto();

		BeanUtils.copyProperties(itTickets, companyTicketDto);

		companyTicketDto.setId(itTickets.getId());
		companyTicketDto.setItTicketId(itTickets.getTicketId());
		companyTicketDto.setTicketId(itTickets.getTicketId());
		//
		Optional<EmployeePersonalInfo> findById = employeePersonelInfoRepository.findById(itTickets.getCreatedBy());

		if (findById.isEmpty()) {
			throw new EmployeeNotFoundException("No employee present");
		}

		//
		companyTicketDto.setTicketOwner(findById.get().getFirstName() + " " + findById.get().getLastName());
		companyTicketDto.setRaisedDate(itTickets.getCreatedDate().toLocalDate());
		companyTicketDto.setCreatedBy(itTickets.getCreatedBy());

		List<TicketHistroy> ticketHistroys = itTickets.getTicketHistroys();

		List<EmployeePersonalInfo> employeeInfo = employeePersonelInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {
			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());

				}

			}
		}

		return companyTicketDto;

	}
}
