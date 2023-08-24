package com.te.flinko.controller.tally;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.tally.TallyDetailsDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.tally.TallyOffPremisesService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/tally")
@RequiredArgsConstructor
@RestController
public class TallyOffPremisesController extends BaseConfigController {

	
	@Autowired
	private TallyOffPremisesService tallyOffPremisesService;
	
	@PostMapping("details")
	public ResponseEntity<SuccessResponse> tallyDetails(@RequestPart MultipartFile master,@RequestPart MultipartFile transaction,@RequestParam String flag){
	
		
		
		TallyDetailsDTO saveNetworkDetails = tallyOffPremisesService.tallyDetails(master, transaction, flag, getCompanyId());
		
		if (saveNetworkDetails!=null) {
			
			return new ResponseEntity<>(new SuccessResponse(false, "data fetched successfully", false), HttpStatus.OK);
		}else {
			
			return new ResponseEntity<>(new SuccessResponse(false, "Employee salary detail already present from application. Click on proceed if you want to change it from Tally or else Click on skip", true), HttpStatus.OK);
		}
	}
}
