package com.te.flinko.controller.hr;

import static com.te.flinko.common.employee.EmployeeLoginConstants.EMPLYOEE_DOES_NOT_EXIST;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.admin.CompanyPayrollDropdownInfoDTO;
import com.te.flinko.dto.employee.AddEmployeeDocumentDTO;
import com.te.flinko.dto.employee.EmployeeAnnualSalaryDTO;
import com.te.flinko.dto.hr.AdditionalWorkInformationDTO;
import com.te.flinko.dto.hr.BankInformationDTO;
import com.te.flinko.dto.hr.DependentInformationDTO;
import com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO;
import com.te.flinko.dto.hr.EmployeeEducationDetailsDTO;
import com.te.flinko.dto.hr.EmployeeEmploymentDTO;
import com.te.flinko.dto.hr.EmployeeNoticePeriodDTO;
import com.te.flinko.dto.hr.EmployeeReportingResponseDTO;
import com.te.flinko.dto.hr.EmployementInformationDTO;
import com.te.flinko.dto.hr.GeneralInformationDTO;
import com.te.flinko.dto.hr.GetDesignationDTO;
import com.te.flinko.dto.hr.InterviewInformationDTO;
import com.te.flinko.dto.hr.PassAndVisaDTO;
import com.te.flinko.dto.hr.PersonalInformationDTO;
import com.te.flinko.dto.hr.RefrencePersonInfoDTO;
import com.te.flinko.dto.hr.ReportingInformationDTO;
import com.te.flinko.dto.hr.ShiftDropDownDTO;
import com.te.flinko.dto.hr.WorkInformationDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.hr.EmployeeManagementService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/hr/employee")
@Slf4j
public class AddEmployeeDetialsController extends BaseConfigController {

	@Autowired
	EmployeeManagementService employeeManagementService;

	@PostMapping("/generaldetials")
	public ResponseEntity<SuccessResponse> addGenaralInformation(@RequestBody GeneralInformationDTO information) {
		long employeeInfoId = information.getEmployeeInfoId();
		GeneralInformationDTO addEmployeePersonalInfo = employeeManagementService.addEmployeePersonalInfo(information,
				getCompanyId());
		log.info("service method called and returned to controller");
		if (addEmployeePersonalInfo == null) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee details is not acceptable", null),
					HttpStatus.NOT_ACCEPTABLE);
 
		} else if (employeeInfoId != 0) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details updated successfully", addEmployeePersonalInfo),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details added successfully", addEmployeePersonalInfo),
				HttpStatus.OK);
	}

	@PostMapping("/workInformation")
	public ResponseEntity<SuccessResponse> addWorkInformation(@RequestBody WorkInformationDTO workInformation) {
		Long officialId = workInformation.getOfficialId();
		WorkInformationDTO addWorkInformation = employeeManagementService.addWorkInformation(workInformation,
				getUserId(), getCompanyId());
		if (addWorkInformation == null) {
			return new ResponseEntity<>(new SuccessResponse(true, "The employee is not present", null),
					HttpStatus.NOT_FOUND);
		} else if (officialId == null) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details added successfully", addWorkInformation),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addWorkInformation), HttpStatus.OK);
	}

	@PostMapping("/reportingdetails")
	public ResponseEntity<SuccessResponse> addReportingInformation(
			@RequestBody ReportingInformationDTO reportingInformation) {
		EmployeeReportingResponseDTO mapReportingInformation = employeeManagementService
				.mapReportingInformation(reportingInformation);
		if (mapReportingInformation == null)
			return new ResponseEntity<>(new SuccessResponse(true, "Reporting details is not updated", null),
					HttpStatus.NOT_FOUND);
		else if (reportingInformation.getReportId() == null) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details added successfully", mapReportingInformation),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", mapReportingInformation),
				HttpStatus.OK);
	}

	@PostMapping("/personalinformation")
	public ResponseEntity<SuccessResponse> addPersonalInformation(@RequestBody PersonalInformationDTO information) {
		PersonalInformationDTO addPersonalInformation = employeeManagementService.addPersonalInformation(information);
		if (addPersonalInformation == null) { 
			return new ResponseEntity<>(new SuccessResponse(true, "Employee Detials not updated", null),
					HttpStatus.NOT_FOUND);
		} else if (information.getEmployeeInfoId() == null) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details added successfully", addPersonalInformation),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addPersonalInformation),
				HttpStatus.OK);
	}

	@PostMapping("/dependentInformation/{employeeId}")
	public ResponseEntity<SuccessResponse> addDependentInformation(
			@RequestBody List<DependentInformationDTO> dependentInformation, @PathVariable Long employeeId) {
		List<DependentInformationDTO> addDependentInformation = employeeManagementService
				.addDependentInformation(dependentInformation, employeeId, getCompanyId());
		if (addDependentInformation.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee Detials not updated", null),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addDependentInformation),
				HttpStatus.OK);
	}

	@PostMapping("/employementinformation/{employeeId}")
	public ResponseEntity<SuccessResponse> addEmployementInformation(
			@RequestBody List<EmployementInformationDTO> information, @PathVariable Long employeeId) {
		List<EmployeeEmploymentDTO> addEmploymentInformation = employeeManagementService
				.addEmploymentInformation(information, employeeId);
		if (addEmploymentInformation.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee Detials not updated", null),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addEmploymentInformation),
				HttpStatus.OK);
	}

	@PostMapping("/educationinformation/{employeeId}")
	public ResponseEntity<SuccessResponse> addEducationInformation(
			@RequestBody List<EmployeeEducationDetailsDTO> information, @PathVariable Long employeeId) {
		List<EmployeeEducationDetailsDTO> addEducationInfo = employeeManagementService
				.addEducaitonInformation(information, employeeId);
		if (addEducationInfo.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee Detials not updated", null),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addEducationInfo), HttpStatus.OK);
	}

	@PostMapping("/bankinformation/{employeeId}")
	public ResponseEntity<SuccessResponse> addBankInformation(@RequestBody List<BankInformationDTO> information,
			@PathVariable Long employeeId) {
		log.info("Into the bank details");
		List<BankInformationDTO> addBankDetailsInfo = employeeManagementService.addBankDetailsInfo(information,
				employeeId);
		if (addBankDetailsInfo.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee Detials not updated", null),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addBankDetailsInfo), HttpStatus.OK);
	}

	@PostMapping("/referenceinfo")
	public ResponseEntity<SuccessResponse> addReferedEmployee(@RequestBody RefrencePersonInfoDTO information) {
		RefrencePersonInfoDTO addReferenceInfo = employeeManagementService.addReferenceInfo(information);
		if (addReferenceInfo == null) {
			return new ResponseEntity<>(new SuccessResponse(true, "Reference Information Not updated", null),
					HttpStatus.BAD_REQUEST);
		} else if (information.getReferenceId() == null) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details added successfully", addReferenceInfo), HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addReferenceInfo), HttpStatus.OK);
	}

	@PostMapping("/visa/{employeeId}")
	public ResponseEntity<SuccessResponse> addPassAndVisa(@RequestBody PassAndVisaDTO information,
			@PathVariable Long employeeId) {
		PassAndVisaDTO addPassandVisaInfo = employeeManagementService.addPassandVisaInfo(information, employeeId);
		if (addPassandVisaInfo == null) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee visa details failed to update", null),
					HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addPassandVisaInfo), HttpStatus.OK);
	}

	@PostMapping("/interviewinformation/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> addInterviewInformation(
			@RequestBody List<InterviewInformationDTO> informationList, @PathVariable Long employeeInfoId) {
		informationList = employeeManagementService.addInterviewInformation(informationList, employeeInfoId);
		if (informationList.isEmpty()) {
			return new ResponseEntity<>(
					new SuccessResponse(true, "Candidate interview information transaction failed ", null),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", informationList), HttpStatus.OK);
	}

	@PostMapping("/noticeperiod/{employeeId}")
	public ResponseEntity<SuccessResponse> addNoticePeriodInformation(
			@Valid @RequestBody EmployeeNoticePeriodDTO noticePeriodInformation, @PathVariable Long employeeId) {
		EmployeeNoticePeriodDTO addNoticePeriodInformation = employeeManagementService
				.addNoticePeriodInformation(noticePeriodInformation, employeeId, getCompanyId());
		if (addNoticePeriodInformation == null) {
			return new ResponseEntity<>(new SuccessResponse(true, "Employee details transaction failed ", null),
					HttpStatus.BAD_REQUEST);
		} else if (noticePeriodInformation.getResignationId() == null) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details added successfully", addNoticePeriodInformation),
					HttpStatus.OK);

		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details updated successfully", addNoticePeriodInformation),
				HttpStatus.OK);
	}

	public ResponseEntity<SuccessResponse> getExitEmployees() {
		List<EmployeeDisplayDetailsDTO> exitEmployees = employeeManagementService.getExitEmployees(getCompanyId());
		if (exitEmployees == null) {
			return new ResponseEntity<>(
					new SuccessResponse(true, "Failed Collecting information of exit employees", null),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Successfully fetched the exit employees ", exitEmployees), HttpStatus.OK);
	}

	@GetMapping("/shift/{companyId}")
	public ResponseEntity<SuccessResponse> getShiftDetails(@PathVariable Long companyId) {
		List<ShiftDropDownDTO> shiftDetails = employeeManagementService.getCompanyShifts(companyId);
		if (shiftDetails.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, "No Shifts Found", shiftDetails), HttpStatus.OK);
		}
		return new ResponseEntity<>(new SuccessResponse(false, "Shifts Fetched Successfully", shiftDetails),
				HttpStatus.OK);

	}

	@PostMapping("/additionalWorkInformation")
	public ResponseEntity<SuccessResponse> additionalWorkInformation(
			@RequestBody AdditionalWorkInformationDTO additionalWorkInforamtionDTO) {

		AdditionalWorkInformationDTO additionalWorkInformation = employeeManagementService
				.additionalWorkInformation(additionalWorkInforamtionDTO);
		if (additionalWorkInformation == null) {
			return new ResponseEntity<>(new SuccessResponse(true, "The employee is not present", null),
					HttpStatus.NOT_FOUND);
		}
		if (additionalWorkInformation.getToaster()) {

			return new ResponseEntity<>(
					new SuccessResponse(false, "Employee details updated successfully", additionalWorkInforamtionDTO),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee details added successfully", additionalWorkInforamtionDTO),
				HttpStatus.OK);
	}

	@PostMapping("/salaryinformation")
	public ResponseEntity<SuccessResponse> addSalaryInformation(@RequestBody EmployeeAnnualSalaryDTO annualSalaryDTO) {
		Long result = employeeManagementService.addAnnualSalaryInformation(annualSalaryDTO);
		if (annualSalaryDTO.getAnnualSalaryId() == null) {
			return new ResponseEntity<>(new SuccessResponse(false, "Employee details added successfully", result),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(new SuccessResponse(false, "Employee details updated successfully", result),
				HttpStatus.OK);
	}

	@PostMapping("/employeeDocument/{employeeInfoId}")
	public ResponseEntity<SuccessResponse> employeeDocument(@RequestBody AddEmployeeDocumentDTO employeeDocumentDTO,
			@PathVariable Long employeeInfoId) {
		employeeDocumentDTO = employeeManagementService.addEmployeeDocuments(employeeDocumentDTO, getCompanyId(),
				employeeInfoId);

		return new ResponseEntity<>(
				new SuccessResponse(false, "Employee Documents Updated Successfully", employeeDocumentDTO),
				HttpStatus.OK);

	}

	/*
	 * @PostMapping("/employeeDocument/{employeeInfoId}") public
	 * ResponseEntity<SuccessResponse> employeeDocument(@RequestParam String data,
	 * 
	 * @RequestParam MultipartFile[] files, @PathVariable Long employeeInfoId)
	 * throws JsonMappingException, JsonProcessingException { ObjectMapper mapper =
	 * new ObjectMapper();
	 * mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	 * mapper.setVisibility(
	 * VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.
	 * Visibility.ANY));
	 * 
	 * List<EmployeeDocumentDTO> employeeDocumentDTO = mapper.readValue(data, new
	 * TypeReference<List<EmployeeDocumentDTO>>() {});
	 * System.err.println("Covereted"); List<EmployeeDocumentDTO>
	 * addEmployeeDocuments =
	 * employeeManagementService.addEmployeeDocuments(employeeDocumentDTO,
	 * getCompanyId(), employeeInfoId, files); return new ResponseEntity<>(new
	 * SuccessResponse(false, "File Uploaded Successfully", addEmployeeDocuments),
	 * HttpStatus.OK);
	 * 
	 * }
	 */

	@GetMapping("/payrolldropdown")
	public ResponseEntity<SuccessResponse> getPayrollInfo() {
		List<CompanyPayrollDropdownInfoDTO> companyPayrollInfoDTOList = employeeManagementService
				.getPayrollInfo(getCompanyId());

		return new ResponseEntity<>(
				new SuccessResponse(false, "Payroll Info fetched sucessfully", companyPayrollInfoDTOList),
				HttpStatus.OK);

	}

	@PostMapping("designation")
	public ResponseEntity<SuccessResponse> getAllDesignationInfo(@RequestBody GetDesignationDTO getDesignationDTO) {
		log.info("controller method of UserDetailsController class, company id is : {}", getCompanyId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("All branches fetched successfully")
						.data(employeeManagementService.getAllDesignationInfo(getCompanyId(), getDesignationDTO))
						.build());
	}
}
