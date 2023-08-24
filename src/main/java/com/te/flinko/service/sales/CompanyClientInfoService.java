package com.te.flinko.service.sales;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.dto.sales.AllCompanyClientInfoResponseDTO;
import com.te.flinko.dto.sales.ClientContactPersonDetailsDTO;
import com.te.flinko.dto.sales.CompanyClientInfoDTO;
import com.te.flinko.dto.sales.CompanyClientInfoResponseDTO;
import com.te.flinko.dto.sales.CompanyClientInfoUpdateDTO;
import com.te.flinko.dto.sales.LeadCategoryResponseDTO;
import com.te.flinko.dto.sales.ProjectDetailsDTO;

public interface CompanyClientInfoService {

	public Boolean addCompanyClientInfo(CompanyClientInfoDTO companyClientInfoDTO,Long companyId,MultipartFile companyLogo);
	
	public List<LeadCategoryResponseDTO> getLeadCategory(Long companyId);
	public List<AllCompanyClientInfoResponseDTO> getAllLeads(Long companyId,String clientType);
	public CompanyClientInfoResponseDTO getCompanyClientinfoById(Long companyId,Long clientId);
	
	public Boolean addClientContactPersonDetails(Long companyId,Long clientId,ClientContactPersonDetailsDTO clientContactPersonDetailsDTO);
	
	public Boolean updateCompanyClientInfo(CompanyClientInfoUpdateDTO companyClientInfoDTO,Long companyId,MultipartFile companyLogo);
	
	public Boolean addProject(ProjectDetailsDTO projectDetailsDTO,Long companyId,Long clientId);
	
	public Boolean deleteLead(Long clientId,Long companyId);
	
	public Boolean deleteDeal(Long clientId,Long companyId);
	
	public Boolean updateLeadCategory(Long companyId,Long clientId,Long leadCategoryId);

	public Boolean addAttachments(Long companyId, Long clientId, MultipartFile multipartFile);
	
}
