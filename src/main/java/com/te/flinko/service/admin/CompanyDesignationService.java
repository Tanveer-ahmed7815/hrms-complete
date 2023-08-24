package com.te.flinko.service.admin;

import java.util.List;

import com.te.flinko.dto.admin.CompanyDesignationInfoDto;
import com.te.flinko.dto.admin.DeleteCompanyDesignationDto;
import com.te.flinko.dto.admin.RoleDTO;

public interface CompanyDesignationService {

	CompanyDesignationInfoDto addCompanyDesignation(long companyId, long parentDesignationId, CompanyDesignationInfoDto companyDesignationInfoDto);

	CompanyDesignationInfoDto updateCompanyDesignation(long companyId, CompanyDesignationInfoDto companyDesignationInfoDto);

	List<CompanyDesignationInfoDto> getAllDepartmentDesignation(long companyId, String departmentName);

	String deleteCompanyDesignation(DeleteCompanyDesignationDto deleteCompanyDesignationDto);
	
	Object getRoleForDesinagtion(RoleDTO roleDTO);

}
 