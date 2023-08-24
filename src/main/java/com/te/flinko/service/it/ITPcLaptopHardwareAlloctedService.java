package com.te.flinko.service.it;

import java.util.List;

import com.te.flinko.dto.admindept.CompanyHardwareItemsDTO;
import com.te.flinko.dto.admindept.CompanyPCLaptopDTO;

public interface ITPcLaptopHardwareAlloctedService {
	
	public List<CompanyPCLaptopDTO> getITPCLaptopAlloctedDetails(Long companyId); 
	
	public CompanyPCLaptopDTO getITPCLaptopAlloctedDetailsAndHistory(Long companyId,String serialNumber); 
	
	public List<CompanyHardwareItemsDTO> getAllocatedOtherItemsDetails(Long companyId);
	
	public CompanyHardwareItemsDTO getAllocatedOtherItemsDetailsAndHistory(Long companyId, String indentificationNumber);


}
