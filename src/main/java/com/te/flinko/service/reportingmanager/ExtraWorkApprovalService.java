package com.te.flinko.service.reportingmanager;

import java.util.List;

import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.dto.reportingmanager.ExtraWorkDTO;

public interface ExtraWorkApprovalService {
	
	ExtraWorkDTO getExtraWorkById(Long extraWorkId);
	
	List<ExtraWorkDTO> getAllExtraWorkDetails(Long employeeInfoId, String status);
	
	Boolean updateStatus(Long extraWorkId, AdminApprovedRejectDto adminApprovedRejectDTO);

}
