package com.te.flinko.service.sales;

import java.util.List;

import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.sales.AllCompanyClientInfoResponseDTO;

public interface SalesDashboardService {
	
	DashboardResponseDTO getClientDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	List<AllCompanyClientInfoResponseDTO> getClientDetailsByStatus(Long companyId, String type);

}
