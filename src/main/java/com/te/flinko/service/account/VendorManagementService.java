package com.te.flinko.service.account;

import java.util.ArrayList;
import java.util.List;

import com.te.flinko.dto.account.AccountClientDetailsDTO;
import com.te.flinko.dto.account.SendVendorLinkDTO;
import com.te.flinko.dto.account.VendorBasicDetailsDTO;
import com.te.flinko.dto.account.VendorContactDetailsDTO;
import com.te.flinko.dto.account.VendorDetailsDTO;
import com.te.flinko.dto.account.VendorFormDTO;
import com.te.flinko.dto.account.VendorListDTO;

public interface VendorManagementService {
	
	List<VendorFormDTO> getDynamicFactors(Long companyId);
	
	VendorDetailsDTO saveVendorDetails(VendorDetailsDTO vendorDetailsDTO);
	
	List<VendorBasicDetailsDTO> getVendorBasicDetails(Long companyId);
	
	VendorDetailsDTO getVendorDetailsById(String vendorId);
	
	String sendLink(SendVendorLinkDTO sendVendorLinkDTO, Long userId);
	
	ArrayList<VendorListDTO> vendorList(Long companyId);

	VendorContactDetailsDTO contactDetails(Long companyId, String id);

	ArrayList<AccountClientDetailsDTO> clientDetails(Long companyId);
	
	VendorDetailsDTO updatePaymentDetails(VendorDetailsDTO vendorDetailsDTO);

}
