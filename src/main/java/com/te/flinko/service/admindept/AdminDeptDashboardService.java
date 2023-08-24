package com.te.flinko.service.admindept;

import java.util.List;

import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.admindept.GetOtherStockGroupItemDto;
import com.te.flinko.dto.hr.mongo.HRTicketsBasicDTO;

public interface AdminDeptDashboardService {
	
	DashboardResponseDTO getTicketDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	DashboardResponseDTO getStockGroupDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	List<HRTicketsBasicDTO> getTicketDetailsByStatus(Long companyId, String type, String filterValue);
	
	List<GetOtherStockGroupItemDto> getStockItemDetailsByStatus(Long companyId, String type);

}
