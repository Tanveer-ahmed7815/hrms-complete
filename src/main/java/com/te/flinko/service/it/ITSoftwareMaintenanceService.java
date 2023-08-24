package com.te.flinko.service.it;

import java.util.List;

import com.te.flinko.dto.admindept.CompanyPCLaptopDTO;
import com.te.flinko.dto.admindept.PcLaptopSoftwareDetailsDTO;
import com.te.flinko.dto.it.mongo.PcLaptopSoftwareRenewalDTO;

public interface ITSoftwareMaintenanceService {

	public List<CompanyPCLaptopDTO> getITSoftwareMaintenanceDetails(Long companyId);

	public List<PcLaptopSoftwareDetailsDTO> getITSoftwareMaintenanceDetailsList(Long companyId, String serialNumber);

	public PcLaptopSoftwareDetailsDTO createOrUpdateNewSoftwares(PcLaptopSoftwareDetailsDTO laptopSoftwareDetailsDTO,
			Long companyId, Long employeeInfoId,String serialNumber);

	public PcLaptopSoftwareRenewalDTO updateRenewalStatus(PcLaptopSoftwareRenewalDTO pcLaptopSoftwareRenewalDTO,
			Long companyId, Long employeeInfoId);
}
