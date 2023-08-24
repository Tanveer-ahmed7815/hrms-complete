package com.te.flinko.controller.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.constants.admin.AdminConstants;
import com.te.flinko.constants.sales.SalesConstants;
import com.te.flinko.dto.sales.ClientContactPersonDetailsDTO;
import com.te.flinko.dto.sales.CompanyClientInfoDTO;
import com.te.flinko.dto.sales.CompanyClientInfoUpdateDTO;
import com.te.flinko.dto.sales.ProjectDetailsDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.sales.CompanyClientInfoService;


@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/admin-company")
public class CompanyClientInfoController extends BaseConfigController {

	@Autowired
	CompanyClientInfoService companyClientInfoService;

	@PostMapping("/companyClientInfo")
	public ResponseEntity<SuccessResponse> createCompanyClientInfo(@RequestPart(required = false) MultipartFile companyLogo,
		 @RequestParam String data) {
		return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.addCompanyClientInfo(BeanCopy.jsonProperties(data, CompanyClientInfoDTO.class), getCompanyId(),companyLogo).booleanValue()
						? SalesConstants.COMPANY_CLIENT_INFO_CREATED_SUCCESS
						: SalesConstants.COMPANY_CLIENT_INFO_CREATED_FAILURE)
				.build());
	}

	@GetMapping("/allLead")
	public ResponseEntity<SuccessResponse> getCompanyLeadCategories() {

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE)
						.data(companyClientInfoService.getLeadCategory(getCompanyId()))
						.message("All Lead Category Fetched Successfully").build());

	}

	@GetMapping("/companyClientInfo/{clientType}")
	public ResponseEntity<SuccessResponse> getAllCompanyClientInfo(@PathVariable(value = "clientType")String clientType) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE)
						.data(companyClientInfoService.getAllLeads(getCompanyId(), clientType))
						.message(clientType.equalsIgnoreCase("lead")?SalesConstants.ALL_LEAD_FETCH_SUCCESS:SalesConstants.ALL_DEAL_FETCH_SUCCESS).build());
	}

	@GetMapping("/companyClientInfo/id/{clientId}")
	public ResponseEntity<SuccessResponse> getCompanyClientInfoById(@PathVariable(value = "clientId")Long clientId){
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE)
						.data(companyClientInfoService.getCompanyClientinfoById(getCompanyId(), clientId))
						.message(SalesConstants.COMPANY_CLIENT_INFO_FETCH_SUCCESS).build());
	}
	
	@PostMapping("/companyClientInfo/{clientId}")
	public ResponseEntity<SuccessResponse> addClientContactPersonDetails(@RequestBody ClientContactPersonDetailsDTO clientContactPersonDetailsDTO,@PathVariable(value = "clientId")Long clientId){
		
		return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.addClientContactPersonDetails(getCompanyId(), clientId, clientContactPersonDetailsDTO).booleanValue()
						? SalesConstants.ADD_CONTACT_PERSON_DETAILS_SUCCESS
						: SalesConstants.ADD_CONTACT_PERSON_DETAILS_FAILED)
				.build());
	}
	
	@PutMapping("/companyClientInfo")
	public ResponseEntity<SuccessResponse> updateCompanyClientInfo(@RequestParam String data,@RequestPart(required = false) MultipartFile companyLogo){
	
		return  ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.updateCompanyClientInfo(BeanCopy.jsonProperties(data, CompanyClientInfoUpdateDTO.class), getCompanyId(),companyLogo).booleanValue()
						? SalesConstants.UPDATE_COMPANY_CLIENT_INFO_SUCCESS
						: SalesConstants.UPDATE_COMPANY_CLIENT_INFO_FAIL)
				.build());
		
	}
	
	@PostMapping("/companyClientInfo/project/{clientId}")
	public ResponseEntity<SuccessResponse> addProject(@RequestBody ProjectDetailsDTO projectDetailsDTO,@PathVariable(value = "clientId")Long clientId){
		return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.addProject(projectDetailsDTO, getCompanyId(), clientId).booleanValue()
						? AdminConstants.WORK_WEEK_RULE_CREATED_SUCCESS
						: AdminConstants.WORK_WEEK_RULE_CREATED_FAIL)
				.build());
	}
	
	@DeleteMapping("/companyClientInfo/lead/{clientId}")
	public ResponseEntity<SuccessResponse> deleteLead(@PathVariable(value = "clientId")Long clientId){
		
		return  ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.deleteLead(clientId, getCompanyId()).booleanValue()
						? SalesConstants.DELETE_LEAD_SUCCESS
						: SalesConstants.DELETE_LEAD_FAILURE)
				.build());
	}
	
	@DeleteMapping("/companyClientInfo/deal/{clientId}")
	public ResponseEntity<SuccessResponse> deleteDeal(@PathVariable(value = "clientId")Long clientId){
		
		return  ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.deleteDeal(clientId, getCompanyId()).booleanValue()
						? SalesConstants.DELETE_DEAL_SUCCESS
						: SalesConstants.DELETE_DEAL_FAILURE)
				.build());
	}
	
	@PutMapping("/companyClientInfo/leadCategory/{clientId}/{leadCategoryId}")
	public ResponseEntity<SuccessResponse> updateLeadCategory(@PathVariable(value = "clientId") Long clientId,@PathVariable(value = "leadCategoryId")Long leadCategoryId){
		
		 return  ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(companyClientInfoService.updateLeadCategory(getCompanyId(), clientId, leadCategoryId).booleanValue()
						? "Deal Edited successfully"
						: SalesConstants.UPDATE_COMPANY_CLIENT_INFO_FAIL)
				.build());
	}
	
	@PostMapping("/companyClientInfo/attachments/{clientId}")
	public ResponseEntity<SuccessResponse> addAttachment(@PathVariable Long clientId,@RequestParam MultipartFile multipartFile){
		companyClientInfoService.addAttachments(getCompanyId(),clientId,multipartFile);
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("File Uploaded successfully").build());
	}
	
	
	
	
	
}
