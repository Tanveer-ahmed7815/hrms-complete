package com.te.flinko.service.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.report.EmployeePerformanceDTO;
import com.te.flinko.dto.report.HolidayListDTO;
import com.te.flinko.dto.report.PayslipGenerationDTO;
import com.te.flinko.dto.report.ReportDTO;
import com.te.flinko.entity.ReportFile;
import com.te.flinko.entity.admin.CompanyAddressInfo;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.employee.EmployeeAnnualSalary;
import com.te.flinko.entity.employee.EmployeeBankInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReportingInfo;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.entity.employee.mongo.EmployeeAttendanceDetails;
import com.te.flinko.entity.report.mongo.EmployeePerformance;
import com.te.flinko.entity.report.mongo.MonthlyPerformance;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.FileSupportException;
import com.te.flinko.repository.ReportFileRepository;
import com.te.flinko.repository.admin.CompanyHolidayDetailsRepository;
import com.te.flinko.repository.employee.EmployeeAnnualSalaryRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeReportingInfoRepository;
import com.te.flinko.repository.report.EmployeePerformanceRepository;
import com.te.flinko.util.FileToMultipartConverter;
import com.te.flinko.util.S3UploadFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleCsvReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import pl.allegro.finance.tradukisto.MoneyConverters;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerateServiceImpl implements ReportGenerateService {

	private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

	@Value("${logo.star0}")
	private String logoStar0;
	@Value("${logo.star1}")
	private String logoStar1;
	@Value("${logo.star2}")
	private String logoStar2;
	@Value("${logo.star3}")
	private String logoStar3;
	@Value("${logo.star4}")
	private String logoStar4;
	@Value("${logo.star5}")
	private String logoStar5;
	@Value("${logo.star6}")
	private String logoStar6;
	@Value("${logo.star7}")
	private String logoStar7;
	@Value("${logo.star8}")
	private String logoStar8;
	@Value("${logo.star9}")
	private String logoStar9;
	@Value("${logo.star10}")
	private String logoStar10;

	private final EmployeePerformanceRepository employeePerformanceRepository;

	private String[] star;

	private static final String COMPANY_ADDRESS_DETAILS_NOT_FOUND = "Company Address Details Not Found";

	private static final String DD_MM_YYYY = "dd-MM-yyyy";

	private static final String P_DATE = "p_date";

	private static final String P_EMPLOYEE_INFO_ID = "p_employee_info_id";

	private static final String P_COMPANY_ID = "p_company_id";

	private static final String ATTACHMENT_FILENAME = "attachment;filename=";

	private final ReportFileRepository fileRepository;

	private final EmployeePersonalInfoRepository employeePersonalInfoRepository;

	private final EmployeeAnnualSalaryRepository annualSalaryRepository;

	private final EmployeeReportingInfoRepository employeeReportingInfoRepository;

	private final CompanyHolidayDetailsRepository companyHolidayDetailsRepository;

	private final S3UploadFile s3UploadFile;

	private final FileToMultipartConverter fileToMultipartConverter;

	private final DataSource dataSource;

	@Override
	public boolean exportReport(String reportFormat, HttpServletResponse response, ReportDTO reportDTO, Long companyId,
			Long employeeInfoId) {
		try {
			ReportFile reportFile = fileRepository.findByReportId(reportDTO.getReportId())
					.orElseThrow(() -> new DataNotFoundException("Report Not Present"));
			reportDTO.getParameters().put(P_COMPANY_ID, companyId);
			reportDTO.getParameters().put(P_EMPLOYEE_INFO_ID, employeeInfoId);
			InputStream transactionReportStream = getClass()
					.getResourceAsStream("/public/reports/" + reportFile.getReportName());
			String title = reportFile.getReportName().split("\\.")[0];
			JasperReport jasperReport = JasperCompileManager.compileReport(transactionReportStream);
			JasperPrint jasperPrint = null;
			if (!reportFile.getIsSqlConn().booleanValue()) {
				List<?> data = getData(reportFile.getReportId(), reportDTO);
				JRBeanCollectionDataSource jrBeanCollection = new JRBeanCollectionDataSource(data);
				jasperPrint = JasperFillManager.fillReport(jasperReport, reportDTO.getParameters(), jrBeanCollection);
			} else
				jasperPrint = JasperFillManager.fillReport(jasperReport, reportDTO.getParameters(),
						dataSource.getConnection());

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String dateTimeNow = LocalDateTime.now().format(formatter);

			String fileName = title.replace(" ", "") + dateTimeNow;

			if (reportFormat.equals("PDF")) {
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				response.setContentType("application/pdf");
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + ".pdf");
				exporter.exportReport();

			} else if (reportFormat.equals("EXCEL")) {
				JRXlsxExporter exporter = new JRXlsxExporter();
				SimpleXlsxReportConfiguration reportConfigXLS = new SimpleXlsxReportConfiguration();
				reportConfigXLS.setSheetNames(new String[] { title });

				reportConfigXLS.setCollapseRowSpan(Boolean.FALSE);
				reportConfigXLS.setDetectCellType(Boolean.TRUE);
				reportConfigXLS.setWhitePageBackground(Boolean.FALSE);
				reportConfigXLS.setForcePageBreaks(Boolean.FALSE);
				reportConfigXLS.setWrapText(Boolean.TRUE);
				reportConfigXLS.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);

				exporter.setConfiguration(reportConfigXLS);
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + ".xlsx");
				response.setContentType("application/octet-stream");
				exporter.exportReport();

			} else if (reportFormat.equals("CSV")) {
				JRCsvExporter exporter = new JRCsvExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
				SimpleCsvReportConfiguration configuration = new SimpleCsvReportConfiguration();
				exporter.setConfiguration(configuration);
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + ".csv");
				response.setContentType("application/csv");
				exporter.exportReport();
				response.flushBuffer();

			} else if (reportFormat.equals("DOCX")) {
				JRDocxExporter exporter = new JRDocxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + ".docx;");
				response.setContentType("application/octet-stream");
				exporter.exportReport();
			} else {
				throw new FileSupportException("File Format isn't supported!");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new FileSupportException(exception.getMessage());
		}
		return true;
	}

	private List<PayslipGenerationDTO> getPayslipData(ReportDTO reportDTO) {
		Long employeeInfoId = (Long) reportDTO.getParameters().get(P_EMPLOYEE_INFO_ID);
		Long companyId = (Long) reportDTO.getParameters().get(P_COMPANY_ID);
		EmployeePersonalInfo employeePersonalInfo = getEmployeePersonalInfo(employeeInfoId, companyId);
		String dateStr = (String) reportDTO.getParameters().get(P_DATE);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY);
		LocalDate parse = LocalDate.parse(dateStr, formatter);
		EmployeeSalaryDetails employeeSalaryDetails = Optional
				.of(employeePersonalInfo.getEmployeeSalaryDetailsList().stream()
						.filter(sal -> sal.getMonth() == parse.getMonthValue()).collect(Collectors.toList()))
				.filter(x -> !x.isEmpty()).map(y -> y.get(0))
				.orElseThrow(() -> new DataNotFoundException("Employee Salary Details Not Found"));
		EmployeeAnnualSalary employeeAnnualSalary = Optional
				.of(annualSalaryRepository.findByEmployeePersonalInfoEmployeeInfoId(employeeInfoId))
				.filter(x -> !x.isEmpty()).map(y -> y.get(0))
				.orElseThrow(() -> new DataNotFoundException("Employee Annual Salary Details Not Found"));
		CompanyAddressInfo companyAddressInfo = Optional
				.of(employeePersonalInfo.getEmployeeOfficialInfo().getCompanyBranchInfo().getCompanyAddressInfoList())
				.filter(x -> !x.isEmpty()).map(y -> y.get(0))
				.orElseThrow(() -> new DataNotFoundException(COMPANY_ADDRESS_DETAILS_NOT_FOUND));
		Map<String, String> additional = new LinkedHashMap<>();

		Map<String, String> additional1 = employeeSalaryDetails.getAdditional();
		Map<String, String> earning = employeeSalaryDetails.getEarning();
		additional.putAll(earning);
		additional.putAll(additional1);
		Map<String, String> deduction = employeeSalaryDetails.getDeduction();
		BigDecimal netPay = employeeSalaryDetails.getNetPay().setScale(2, RoundingMode.HALF_EVEN);
		LocalDate dob = employeePersonalInfo.getDob();
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		String employeeId = employeeOfficialInfo.getEmployeeId();
		String designation = employeeOfficialInfo.getDesignation();
		LocalDate doj = employeeOfficialInfo.getDoj();
		String name = employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName();
		List<EmployeeBankInfo> employeeBankInfoList = Optional.of(employeePersonalInfo.getEmployeeBankInfoList())
				.filter(x -> !x.isEmpty())
				.orElseThrow(() -> new DataNotFoundException("Employee Bank Details Not Found"));
		EmployeeBankInfo employeeBankInfo = employeeBankInfoList.get(employeeBankInfoList.size() - 1);
		String bankName = employeeBankInfo.getBankName();
		Long accountNumber = employeeBankInfo.getAccountNumber();
		String pfNumber = employeeAnnualSalary.getPfNumber();
		String uan = employeeAnnualSalary.getUan();
		String pan = employeePersonalInfo.getPan();
		CompanyInfo companyInfo = employeePersonalInfo.getCompanyInfo();
		String companyLogoUrl = companyInfo.getCompanyLogoUrl();
		String companyName = companyInfo.getCompanyName();
		PayslipGenerationDTO objectProperties = BeanCopy.objectProperties(companyAddressInfo.getGeoFencingLocation(),
				PayslipGenerationDTO.class);
		Long pincode = companyAddressInfo.getPincode();
		BigDecimal addSum = additional.entrySet().stream().map(x -> new BigDecimal(x.getValue()))
				.reduce(BigDecimal.ZERO, (add, x1) -> add.add(x1));
		BigDecimal deductionSum = deduction.entrySet().stream().map(x -> new BigDecimal(x.getValue()))
				.reduce(BigDecimal.ZERO, (add, x1) -> add.add(x1));
		long lop = deduction.entrySet().stream().map(Entry::getKey).filter(y -> y.equalsIgnoreCase("lop")).count();
		LocalDate date = parse.withDayOfMonth(parse.getMonth().length(parse.isLeapYear()));

		return Optional.of(additional.size() > deduction.size()).filter(f -> f)
				.map(map -> additional.entrySet().stream().map(x -> {
					String key = null;
					String value = null;
					if (!deduction.isEmpty()) {
						Entry<String, String> entry = deduction.entrySet().stream().findFirst().get();
						key = entry.getKey();
						value = entry.getValue();
					}
					if (key != null)
						deduction.remove(key);
					return PayslipGenerationDTO.builder().earningParticulars(x.getKey())
							.earAmount(new BigDecimal(x.getValue())).deductionParticulars(key)
							.ducAmount(new BigDecimal(Optional.ofNullable(value).orElseGet(() -> "0")))
							.bankName(bankName).accountNo(accountNumber).dob(dob).employeeName(name).panNo(pan).uan(uan)
							.employeeId(employeeId).designation(designation).pfNo(pfNumber).pincode(pincode)
							.totalEar(addSum).totalDuc(deductionSum).companyUrl(companyLogoUrl).companyName(companyName)
							.doj(doj).inWord(inWord(netPay)).netPay(netPay).lop(lop).ndp(date.getDayOfMonth() - lop)
							.addressDetails(objectProperties.getLabel()).build();
				}).collect(Collectors.toList())

				).orElseGet(() -> deduction.entrySet().stream().map(x -> {
					String key = null;
					String value = null;
					if (!additional.isEmpty()) {
						Entry<String, String> entry = additional.entrySet().stream().findFirst().get();
						key = entry.getKey();
						value = entry.getValue();
					}
					if (key != null)
						additional.remove(key);
					return PayslipGenerationDTO.builder().earningParticulars(key)
							.earAmount(new BigDecimal(Optional.ofNullable(value).orElseGet(() -> "0")))
							.deductionParticulars(x.getKey()).ducAmount(new BigDecimal(x.getValue())).bankName(bankName)
							.accountNo(accountNumber).dob(dob).employeeName(name).panNo(pan).uan(uan)
							.employeeId(employeeId).designation(designation).pfNo(pfNumber).companyUrl(companyLogoUrl)
							.companyName(companyName).inWord(inWord(netPay)).doj(doj).totalEar(addSum)
							.totalDuc(deductionSum).netPay(netPay).pincode(pincode).lop(lop)
							.ndp(date.getDayOfMonth() - lop).addressDetails(objectProperties.getLabel()).build();
				}).collect(Collectors.toList()));
	}

	private String inWord(BigDecimal netPay) {
		String[] split2 = netPay.toString().split("\\.");
		MoneyConverters converter = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;
		String asWords = converter.asWords(new BigDecimal(split2[0]));
		String[] split = asWords.split("£");
		if (!split2[1].equals("00")) {
			String asWord = converter.asWords(new BigDecimal(split2[1]));
			String amt = split[0] + "Point " + asWord.split("£")[0] + "Only";
			return Stream.of(amt.split(" ")).map(x -> ("" + x.charAt(0)).toUpperCase() + x.substring(1))
					.collect(Collectors.joining(" "));
		}
		return Stream.of(split[0].split(" ")).map(x -> ("" + x.charAt(0)).toUpperCase() + x.substring(1))
				.collect(Collectors.joining(" "));
	}

	String address;

	private List<HolidayListDTO> getHolidayList(ReportDTO reportDTO) {
		Long employeeInfoId = (Long) reportDTO.getParameters().get(P_EMPLOYEE_INFO_ID);
		Long companyId = (Long) reportDTO.getParameters().get(P_COMPANY_ID);
		CompanyAddressInfo companyAddressInfo = getCompanyAddressInfo(employeeInfoId, companyId);
		HolidayListDTO dto = BeanCopy.objectProperties(companyAddressInfo.getGeoFencingLocation(),
				HolidayListDTO.class);
		if (dto.getLabel() != null) {
			String[] split = dto.getLabel().split(",");
			int length = split.length;
			address = split[length - 3] + " , " + split[length - 2] + " , " + split[length - 1];
		}
		Map<LocalDate, HolidayListDTO> collect2 = companyHolidayDetailsRepository.findByCompanyInfoCompanyId(companyId)
				.filter(x -> !x.isEmpty()).orElseThrow().stream().filter(day -> day.getHolidayDate() != null)
				.map(holiday -> {
					CompanyInfo companyInfo = holiday.getCompanyInfo();
					LocalDate holidayDate = holiday.getHolidayDate();
					String format = holidayDate.format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
					HolidayListDTO holidayListDTO = HolidayListDTO.builder().companyName(companyInfo.getCompanyName())
							.companyUrl(companyInfo.getCompanyLogoUrl()).addressDetails(address).date(format)
							.dayOfWeek(holidayDate.getDayOfWeek().toString()).holidayName(holiday.getHolidayName())
							.holidayType(holiday.getIsOptional() != null && holiday.getIsOptional().booleanValue()
									? "Regional"
									: "Optional")
							.state(companyAddressInfo.getState()).pincode(companyAddressInfo.getPincode())
							.holidayDate(holidayDate).cin(companyInfo.getCin()).build();
					BeanUtils.copyProperties(holiday, holidayListDTO);
					return holidayListDTO;
				}).filter(x -> x.getIsOptional() != null && x.getHolidayDate() != null && x.getHolidayName() != null)
				.collect(Collectors.toMap(HolidayListDTO::getHolidayDate, v -> v, (k1, k2) -> HolidayListDTO.builder()
						.companyName(k1.getCompanyName()).holidayDate(k1.getHolidayDate())
						.holidayName(k1.getHolidayName() + " / " + k2.getHolidayName()).date(k1.getDate())
						.dayOfWeek(k1.getDayOfWeek()).holidayType(k1.getHolidayType() + " / " + k2.getHolidayType())
						.isOptional(k1.getIsOptional()).companyUrl(k1.getCompanyUrl()).addressDetails(dto.getLabel())
						.state(companyAddressInfo.getState()).pincode(companyAddressInfo.getPincode()).cin(k1.getCin())
						.build()));
		return new ArrayList<>(collect2.values());
	}

	EmployeePerformanceDTO build;

	private List<EmployeePerformanceDTO> getEmployeePerformance(ReportDTO reportDTO) {
		this.star = new String[] { logoStar0, logoStar1, logoStar2, logoStar3, logoStar4, logoStar5, logoStar6,
				logoStar7, logoStar8, logoStar9, logoStar10 };
		Long employeeInfoId = (Long) reportDTO.getParameters().get(P_EMPLOYEE_INFO_ID);
		Long companyId = (Long) reportDTO.getParameters().get(P_COMPANY_ID);
		LocalDate date = LocalDate.parse((String) reportDTO.getParameters().get(P_DATE),
				DateTimeFormatter.ofPattern(DD_MM_YYYY));
		EmployeePersonalInfo employeePersonalInfo = getEmployeePersonalInfo(employeeInfoId, companyId);
		CompanyAddressInfo companyAddressInfo = Optional
				.of(employeePersonalInfo.getEmployeeOfficialInfo().getCompanyBranchInfo().getCompanyAddressInfoList())
				.filter(x -> !x.isEmpty()).map(y -> y.get(0))
				.orElseThrow(() -> new DataNotFoundException(COMPANY_ADDRESS_DETAILS_NOT_FOUND));

		EmployeePerformanceDTO dto = BeanCopy.objectProperties(companyAddressInfo.getGeoFencingLocation(),
				EmployeePerformanceDTO.class);

		if (dto.getLabel() != null) {
			String[] split = dto.getLabel().split(",");
			int length = split.length;
			address = split[length - 3] + " , " + split[length - 2] + " , " + split[length - 1];
		}
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		CompanyInfo companyInfo = employeePersonalInfo.getCompanyInfo();
		EmployeeReportingInfo employeeReportingInfo = employeeReportingInfoRepository
				.findByEmployeePersonalInfoEmployeeInfoId(employeeInfoId);
		EmployeePersonalInfo reportingHR = employeeReportingInfo.getReportingHR();
		EmployeePersonalInfo reportingManager = employeeReportingInfo.getReportingManager();

		EmployeePerformance leavePerformance = employeePerformanceRepository
				.findByCompanyIdAndEmployeeInfoIdAndYear(companyId, employeeInfoId, (long) date.getYear())
				.orElseThrow();

		MonthlyPerformance monthlyPerformance = leavePerformance.getMonthlyPerformance()
				.get(date.getMonth().toString());
		String month = date.getMonth().toString();
		if (monthlyPerformance == null)
			throw new DataNotFoundException("Performance Is Not Calculated For The Month " + month.substring(0, 1)
					+ month.substring(1).toLowerCase());

		EmployeePerformanceDTO employeePerformance = EmployeePerformanceDTO.builder().addressDetails(address)
				.pincode(companyAddressInfo.getPincode()).date(date).employeeId(employeeOfficialInfo.getEmployeeId())
				.employeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
				.logoStar(this.star[9]).department(employeeOfficialInfo.getDepartment())
				.designation(employeeOfficialInfo.getDesignation()).companyName(companyInfo.getCompanyName())
				.companyUrl(companyInfo.getCompanyLogoUrl())
				.reportingHR(reportingHR.getFirstName() + " " + reportingHR.getLastName())
				.targetAchived(monthlyPerformance.getTargetAchived()).punctual(monthlyPerformance.getPunctual())
				.leaves(monthlyPerformance.getLeaves()).activities(monthlyPerformance.getActivities())
				.reportingManager(reportingManager.getFirstName() + " " + reportingManager.getLastName()).build();
		build = employeePerformance;

		EmployeePerformanceDTO star2 = getStar(Map.of("activitiesStar", monthlyPerformance.getActivities(),
				"targetAchivedStar", monthlyPerformance.getTargetAchived(), "leavesStar",
				monthlyPerformance.getLeaves(), "punctualStar", monthlyPerformance.getPunctual()));
		return List.of(star2);
	}

	private EmployeePersonalInfo getEmployeePersonalInfo(Long employeeInfoId, Long companyId) {
		return Optional
				.of(employeePersonalInfoRepository.findByCompanyInfoCompanyIdAndEmployeeInfoId(companyId,
						employeeInfoId))
				.filter(x -> !x.isEmpty()).map(y -> y.get(0))
				.orElseThrow(() -> new DataNotFoundException("Employee Personal Details Not Found"));
	}

	private CompanyAddressInfo getCompanyAddressInfo(Long employeeInfoId, Long companyId) {
		EmployeePersonalInfo employeePersonalInfo = getEmployeePersonalInfo(employeeInfoId, companyId);
		return Optional
				.of(employeePersonalInfo.getEmployeeOfficialInfo().getCompanyBranchInfo().getCompanyAddressInfoList())
				.filter(x -> !x.isEmpty()).map(y -> y.get(0))
				.orElseThrow(() -> new DataNotFoundException(COMPANY_ADDRESS_DETAILS_NOT_FOUND));
	}

	List<?> l = new ArrayList<>();

	private List<?> getData(Long id, ReportDTO reportDTO) {

		switch (id.intValue()) {
		case 1:
			return getPayslipData(reportDTO);
		case 2:
			return getHolidayList(reportDTO);
		case 3:
			return getEmployeePerformance(reportDTO);
		default:
			return List.of();
		}
	}

	@Override
	public EmployeeAttendanceDetails test(String reportFormat, HttpServletResponse response, ReportDTO reportDTO,
			Long companyId, Long employeeInfoId) {
		return null;
	}

	@Transactional
	@Override
	public EmployeeAttendanceDetails test1(String reportFormat, HttpServletResponse response, ReportDTO reportDTO,
			Long companyId, Long employeeInfoId) {
		return EmployeeAttendanceDetails.builder().build();
	}

	private EmployeePerformanceDTO getStar(Map<String, BigDecimal> performanceValue) {
		EmployeePerformanceDTO buildFinal = EmployeePerformanceDTO.builder().build();
		performanceValue.forEach((key, val) -> {
			BigDecimal value = val;
			int i = 1;
			String[] split = value.toString().split("\\.");
			if (split.length == 1)
				split = new String[] { split[0], "0" };

			split[1] = split[1].substring(0, 1);
			for (i = 1; i <= Integer.parseInt(split[0]); i++) {
				build = get(key, build, i, 10);
			}
			if (!split[1].equalsIgnoreCase("0")) {
				build = get(key, build, i, Integer.parseInt(split[1]));
				i++;
			}
			for (int j = i; j <= 5; j++) {
				build = get(key, build, j, 0);
			}
			BeanUtils.copyProperties(build, buildFinal);
		});
		return buildFinal;
	}

	private EmployeePerformanceDTO get(String per, EmployeePerformanceDTO build, int i, int starId) {
		String starKey = "set" + per.substring(0, 1).toUpperCase() + per.substring(1) + i;
		try {
			Method method = build.getClass().getMethod(starKey, String.class);
			method.invoke(build, star[starId]);
		} catch (Exception e) {
			throw new DataNotFoundException(e.getMessage());
		}
		return build;
	}

	List<String> fileDoc = new ArrayList<>();

	@Override
	public String exportReportMobile(String reportFormat, HttpServletResponse response, ReportDTO reportDTO,
			Long companyId, Long employeeInfoId) {
		try {
			ReportFile reportFile = fileRepository.findByReportId(reportDTO.getReportId())
					.orElseThrow(() -> new DataNotFoundException("Report Not Present"));
			reportDTO.getParameters().put(P_COMPANY_ID, companyId);
			reportDTO.getParameters().put(P_EMPLOYEE_INFO_ID, employeeInfoId);

			InputStream transactionReportStream = getClass()
					.getResourceAsStream("/public/reports/" + reportFile.getReportName());

			JasperReport jasperReport = JasperCompileManager.compileReport(transactionReportStream);

			JasperPrint jasperPrint = null;
			if (!reportFile.getIsSqlConn().booleanValue()) {
				List<?> data = getData(reportFile.getReportId(), reportDTO);
				JRBeanCollectionDataSource jrBeanCollection = new JRBeanCollectionDataSource(data);
				jasperPrint = JasperFillManager.fillReport(jasperReport, reportDTO.getParameters(), jrBeanCollection);
			} else
				jasperPrint = JasperFillManager.fillReport(jasperReport, reportDTO.getParameters(),
						dataSource.getConnection());
			String title = reportFile.getReportName().split("\\.")[0];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String dateTimeNow = LocalDateTime.now().format(formatter);
			String fileName = title.replace(" ", "") + dateTimeNow;

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			String tempDir = System.getProperty(JAVA_IO_TMPDIR) + File.separator + fileName + ".pdf";

			JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(tempDir));
			File file = new File(tempDir);
			MultipartFile convert = fileToMultipartConverter.convert(file);
			fileDoc.add(tempDir);
			return s3UploadFile.uploadFile(convert);
		} catch (Exception exception) {
			throw new DataNotFoundException(exception.getMessage());
		}
	}

	@Override
	public String exportReportDeleteMobile(ReportDTO reportDTO) {
		try {
			String url = reportDTO.getUrl();
			s3UploadFile.deleteS3Folder(url);
			if (fileDoc.contains(url)) {
				Files.delete(Paths.get(url));
				fileDoc.remove(url);
				return "Delete Successfully";
			}
			return null;
		} catch (IOException e) {
			throw new DataNotFoundException(e.getMessage());
		}
	}

}