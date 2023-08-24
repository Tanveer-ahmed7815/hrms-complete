package com.te.flinko.service.admin;

import java.util.List;

import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.TicketDetailsDTO;
import com.te.flinko.dto.helpandsupport.mongo.CompanyTicketDto;
import com.te.flinko.dto.hr.EmployeeDisplayDetailsDTO;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;

public interface AdminDashboardService {
	
	DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	DashboardResponseDTO getSalaryDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	List<HRTicketsBasicDTO> getTicketDetailsByStatus(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	TicketDetailsDTO getTicketDetailsById(DashboardRequestDTO dashboardRequestDTO, String objectTicketId,
			Long companyId);

	public CompanyTicketDto addTicketsHardwareAllocatedDetails(CompanyTicketDto updateTicketDTO, Long companyId);

	public CompanyTicketDto updateTicketRemarks(CompanyTicketDto updateTicketDTO, Long companyId);
	
	DashboardResponseDTO getAttendanceDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	List<EmployeeDisplayDetailsDTO> getEmployeeDetailsByStatus(Long companyId, String type);

}
