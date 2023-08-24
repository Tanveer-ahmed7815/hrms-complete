package com.te.flinko.controller.reportingmanager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.dto.reportingmanager.ExtraWorkDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.reportingmanager.ExtraWorkApprovalService;

@RestController
@RequestMapping("/api/v1/extra-work")
@CrossOrigin(origins = "https://hrms.flinko.app")
public class ExtraWorkApprovalController extends BaseConfigController {

	@Autowired
	private ExtraWorkApprovalService extraWorkApprovalService;

	@GetMapping("/{status}")
	ResponseEntity<SuccessResponse> getAllExtraWorkDetails(@PathVariable String status) {
		List<ExtraWorkDTO> extraWorkDetailsList = extraWorkApprovalService.getAllExtraWorkDetails(getUserId(), status);
		if (!extraWorkDetailsList.isEmpty()) {
			return new ResponseEntity<>(
					SuccessResponse.builder().data(extraWorkDetailsList).error(false).message("List fetched").build(),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(SuccessResponse.builder().data(extraWorkDetailsList).error(false)
					.message("No Extra Work Details Found").build(), HttpStatus.OK);
		}

	}

	@PutMapping("/{extraWorkId}")
	ResponseEntity<SuccessResponse> getAllExtraWorkDetails(@PathVariable Long extraWorkId,
			@RequestBody AdminApprovedRejectDto adminApprovedRejectDTO) {
		Boolean updateStatus = extraWorkApprovalService.updateStatus(extraWorkId, adminApprovedRejectDTO);
		if (Boolean.TRUE.equals(updateStatus)) {
			return new ResponseEntity<>(SuccessResponse.builder()
					.data("Extra Work " + adminApprovedRejectDTO.getStatus() + " Successfuly").error(false)
					.message("Extra Work " + adminApprovedRejectDTO.getStatus() + " Successfuly").build(),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(
					SuccessResponse.builder().data("Operation Failed").error(false).message("Operation Failed").build(),
					HttpStatus.OK);
		}

	}

	@GetMapping("/by-id/{extraWorkId}")
	ResponseEntity<SuccessResponse> getExtraWorkById(@PathVariable Long extraWorkId) {
		ExtraWorkDTO extraWorkDetails = extraWorkApprovalService.getExtraWorkById(extraWorkId);
		if (extraWorkDetails != null) {
			return new ResponseEntity<>(
					SuccessResponse.builder().data(extraWorkDetails).error(false).message("List fetched").build(),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(SuccessResponse.builder().data(extraWorkDetails).error(false)
					.message("No Extra Work Details Found").build(), HttpStatus.OK);
		}

	}

}
