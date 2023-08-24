package com.te.flinko.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.te.flinko.dto.admin.CompanyLetterDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.admin.mongo.CompanyLetterFormatService;

@CrossOrigin(origins = "https://hrms.flinko.app")
@RestController
@RequestMapping("/api/v1/company/letter")
public class CompanyLetterFormatController extends BaseConfigController {

	@Autowired
	private CompanyLetterFormatService companyLetterFormatService;

	@PostMapping
	public ResponseEntity<SuccessResponse> uploadLetter(@RequestParam String data, @RequestParam MultipartFile file)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setVisibility(
				VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

		CompanyLetterDTO companyLetterDTO = mapper.readValue(data, CompanyLetterDTO.class);
		return new ResponseEntity<>(
				new SuccessResponse(false, "Letter Uploaded Successfully",
						companyLetterFormatService.addLetterFormat(companyLetterDTO, getCompanyId(), file)),
				HttpStatus.OK);

	}

	@GetMapping
	public ResponseEntity<SuccessResponse> getAllLetterDetails() {
		return new ResponseEntity<>(new SuccessResponse(false, "Letters Fetched Successfully",
				companyLetterFormatService.getAllLetterDetails(getCompanyId())), HttpStatus.OK);

	}

	@PostMapping("by-type")
	public ResponseEntity<SuccessResponse> getLetterDetails(@RequestBody CompanyLetterDTO companyLetterDTO) {
		CompanyLetterDTO letterDetails = companyLetterFormatService.getLetterDetails(getCompanyId(), companyLetterDTO);
		if (letterDetails == null) {
			return new ResponseEntity<>(new SuccessResponse(false, "No Letter Found", letterDetails), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new SuccessResponse(false, "Letter Fetched Successfully", letterDetails),
					HttpStatus.OK);
		}
	}

}
