package com.te.flinko.service.hr;

import java.util.List;

import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.hr.CandidateListDTO;
import com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO;
import com.te.flinko.dto.hr.EventManagementDisplayEventDTO;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;

public interface HRDashboardService {
	
	DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	DashboardResponseDTO getCandidateDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	DashboardResponseDTO getEventDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	DashboardResponseDTO getEmployeeDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	List<CandidateListDTO> getCandidateDetailsByStatus(Long companyId, String type);
	
	List<EventManagementDisplayEventDTO> getEventDetailsByStatus(Long companyId, String type);
	
	List<EmployeeDisplayDetailsDTO> getEmployeeDetailsByStatus(Long companyId, String type);
	
	List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue);

}
