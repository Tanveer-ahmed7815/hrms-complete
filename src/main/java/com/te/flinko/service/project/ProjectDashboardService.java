package com.te.flinko.service.project;

import java.util.List;

import com.te.flinko.dto.DashboardRequestDTO;
import com.te.flinko.dto.DashboardResponseDTO;
import com.te.flinko.dto.project.ProjectCompleteDetailsDTO;
import com.te.flinko.dto.project.ProjectDetailsBasicDTO;
import com.te.flinko.dto.project.mongo.ProjectListDTO;

public interface ProjectDashboardService {

	DashboardResponseDTO getProjectDetails(DashboardRequestDTO dashboardRequestDTO, Long companyId);
	
	List<ProjectListDTO> getAllProjectNames(Long companyId);
	
	ProjectCompleteDetailsDTO getProjectDetailsById(Long projectId, Long companyId);
	
	List<ProjectDetailsBasicDTO> getProjectDetailsByStatus(Long companyId, String type);

}
