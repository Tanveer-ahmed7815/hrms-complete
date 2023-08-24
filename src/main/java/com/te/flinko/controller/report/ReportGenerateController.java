package com.te.flinko.controller.report;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.hr.LetterDetailsDTO;
import com.te.flinko.dto.report.ReportDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.report.LetterGenerateService;
import com.te.flinko.service.report.ReportGenerateService;

import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class ReportGenerateController extends BaseConfigController {

	private final ReportGenerateService reportService;

	private final LetterGenerateService letterGenerateService;

	@PostMapping("/letter")
	public ResponseEntity<SuccessResponse> estimateProject(@RequestParam String data, @RequestParam MultipartFile file)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setVisibility(
				VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

		LetterDetailsDTO letterDetailsDTO = mapper.readValue(data, LetterDetailsDTO.class);

		return new ResponseEntity<>(
				new SuccessResponse(false, "Letter Issued Successfully",
						letterGenerateService.saveLetter(letterDetailsDTO, file, getCompanyId(), getUserId())),
				HttpStatus.OK);

	}

	@PostMapping("report")
	public void exportReport(HttpServletResponse response, @RequestBody ReportDTO reportDTO) {
		log.info("Reporting Service Controller With Company Id {}", getCompanyId());
		reportService.exportReport(reportDTO.getReportFormat(), response, reportDTO, getCompanyId(),
				getEmployeeInfoId());

	}

	@PostMapping(path = "report/mobile")
	public ResponseEntity<SuccessResponse> exportReportMobile(@RequestBody ReportDTO reportDTO,
			HttpServletResponse response) {
		log.info("Report Delete Mobile Controller");
		return ResponseEntity.ok()
				.body(SuccessResponse.builder().error(false).message("Report Generate Successfully")
						.data(reportService.exportReportMobile(reportDTO.getReportFormat(), response, reportDTO,
								getCompanyId(), getEmployeeInfoId()))
						.build());

	}

	@DeleteMapping(path = "report/mobile")
	public ResponseEntity<SuccessResponse> exportReportDeleteMobile(@RequestBody ReportDTO reportDTO) {
		log.info("Report Delete Mobile Controller");
		return ResponseEntity.ok().body(SuccessResponse.builder().error(false).message("Delete Successfully Done")
				.data(reportService.exportReportDeleteMobile(reportDTO)).build());

	}

	@GetMapping("letter/employee")
	public ResponseEntity<SuccessResponse> getEmployee() {
		log.info("Get Employee With Company Id {}", getCompanyId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Employee Fetched Successfully")
						.data(letterGenerateService.getEmployeeDetails(getCompanyId())).build());
	}

	@GetMapping("letter/employee/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> getEmployeeLetter(@PathVariable Long employeeInfoId) {
		log.info("Get Employee Letter With employeeInfoId", employeeInfoId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Employee Letters Fetched Successfully")
						.data(letterGenerateService.getEmployeeLetters(employeeInfoId, getCompanyId())).build());
	}
}
