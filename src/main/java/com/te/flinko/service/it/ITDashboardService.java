package com.te.flinko.service.it;

import java.util.List;

import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.admindept.CompanyHardwareItemsDTO;
import com.te.flinko.dto.admindept.CompanyPCLaptopDTO;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;

public interface ITDashboardService {
	
	DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	DashboardResponseDTO getPCLaptopDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);

	DashboardResponseDTO getHardwareItemDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);

	public List<CompanyPCLaptopDTO> typeBasedPCLaptopDetails(Long companyId, String type);

	public List<CompanyHardwareItemsDTO> typeBasedHardwareItemDetails(Long companyId, String type);
	
	List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue);
	
}
