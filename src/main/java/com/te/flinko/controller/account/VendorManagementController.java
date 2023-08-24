package com.te.flinko.controller.account;

import static com.te.flinko.common.account.AccountConstants.COMPANY_VENDOR_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.COMPANY_VENDOR_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.VENDOR_CONTACT_DETAILS_FETCHED_SUCCESSFULLY;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.AccountClientDetailsDTO;
import com.te.flinko.dto.account.SendVendorLinkDTO;
import com.te.flinko.dto.account.VendorBasicDetailsDTO;
import com.te.flinko.dto.account.VendorContactDetailsDTO;
import com.te.flinko.dto.account.VendorDetailsDTO;
import com.te.flinko.dto.account.VendorFormDTO;
import com.te.flinko.dto.account.VendorListDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.VendorManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/vendor")
@RestController
public class VendorManagementController extends BaseConfigController {

	@Autowired
	private VendorManagementService vendorManagementService;

	@GetMapping("/dynamic-foctors")
	public ResponseEntity<SuccessResponse> getDynamicFactors() {
		List<VendorFormDTO> dynamicFactors = vendorManagementService.getDynamicFactors(getCompanyId());
		if (dynamicFactors.isEmpty()) {
			return ResponseEntity
					.ok(SuccessResponse.builder().error(false).data(dynamicFactors).message("No Recond Found").build());
		}
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("Data feteched Succesfully")
				.data(dynamicFactors).build());
	}

	@PostMapping
	public ResponseEntity<SuccessResponse> saveVendorDetails(@Valid @RequestBody VendorDetailsDTO vendorDetailsDTO) {
		VendorDetailsDTO vendorDetails = vendorManagementService.saveVendorDetails(vendorDetailsDTO);
		if (vendorDetails == null) {
			return ResponseEntity
					.ok(SuccessResponse.builder().error(false).data(vendorDetails).message("Operation Failed").build());
		}
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("Vendor Registered Successfully")
				.data(vendorDetails).build());
	}

	@PostMapping("/send-link")
	public ResponseEntity<SuccessResponse> sendLink(@RequestBody SendVendorLinkDTO sendVendorLinkDTO) {
		String message = vendorManagementService.sendLink(sendVendorLinkDTO, getUserId());
		if (message == null) {
			return ResponseEntity.ok(SuccessResponse.builder().error(false).data(message).message("Failed").build());
		}
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message(message).data(message).build());
	}

	@GetMapping
	public ResponseEntity<SuccessResponse> getVendorBasicDetails() {
		List<VendorBasicDetailsDTO> vendorBasicDetails = vendorManagementService.getVendorBasicDetails(getCompanyId());
		if (vendorBasicDetails.isEmpty()) {
			return ResponseEntity.ok(
					SuccessResponse.builder().error(false).data(vendorBasicDetails).message("No Recond Found").build());
		}
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("Data feteched Succesfully")
				.data(vendorBasicDetails).build());
	}

	@GetMapping("/{vendorId}")
	public ResponseEntity<SuccessResponse> getVendorDetailsById(@PathVariable String vendorId) {
		VendorDetailsDTO vendorDetails = vendorManagementService.getVendorDetailsById(vendorId);
		if (vendorDetails == null) {
			return ResponseEntity
					.ok(SuccessResponse.builder().error(false).data(vendorDetails).message("No Recond Found").build());
		}
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("Data feteched Succesfully")
				.data(vendorDetails).build());
	}

	@GetMapping("/vendor-list")
	public ResponseEntity<SuccessResponse> vendorList() {
		ArrayList<VendorListDTO> vendorList = vendorManagementService.vendorList(getCompanyId());
		if (vendorList == null || vendorList.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, COMPANY_VENDOR_DETAILS_NOT_FOUND, vendorList),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new SuccessResponse(false, COMPANY_VENDOR_DETAILS_FETCHED_SUCCESSFULLY, vendorList),
				HttpStatus.OK);
	}

	@GetMapping("/contact-details")
	public ResponseEntity<SuccessResponse> contactDetails(@RequestParam String id) {
		VendorContactDetailsDTO contactDetails = vendorManagementService.contactDetails(getCompanyId(), id);
		return new ResponseEntity<>(
				new SuccessResponse(false, VENDOR_CONTACT_DETAILS_FETCHED_SUCCESSFULLY, contactDetails), HttpStatus.OK);
	}

	@GetMapping("/client-details")
	public ResponseEntity<SuccessResponse> clientDetails() {
		ArrayList<AccountClientDetailsDTO> clientDetails = vendorManagementService.clientDetails(getCompanyId());
		if (clientDetails == null || clientDetails.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, "Company client's details not found", clientDetails),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, "Company client's details fetched successfully", clientDetails),
				HttpStatus.OK);
	}

	@PutMapping("/payment")
	public ResponseEntity<SuccessResponse> updatePaymentDetails(@RequestBody VendorDetailsDTO vendorDetailsDTO) {
		VendorDetailsDTO vendorDetails = vendorManagementService.updatePaymentDetails(vendorDetailsDTO);
		if (vendorDetails == null) {
			return ResponseEntity
					.ok(SuccessResponse.builder().error(false).data(vendorDetails).message("Operation Failed").build());
		}
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("Payment Details Updated Successfully")
				.data(vendorDetails).build());
	}

}
