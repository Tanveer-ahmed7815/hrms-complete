package com.te.flinko.service.it.mongo;

import java.util.List;

import com.te.flinko.dto.helpandsupport.mongo.CompanyTicketDto;
import com.te.flinko.dto.it.mongo.ITTicketsDTO;
import com.te.flinko.dto.it.mongo.UpdateITTicketDTO;

public interface ITTicketsService {

	public List<CompanyTicketDto> getTicketsHardwareAllocatedDetails(Long companyId, Long employeeInfoId);

	public List<CompanyTicketDto> getTicketsSoftwareIssuesDetails(Long companyId, Long employeeInfoId);

	public List<CompanyTicketDto> getTicketsHardwareIssuesDetails(Long companyId, Long employeeInfoId);

	public List<CompanyTicketDto> getTicketsEmailDetails(Long companyId, Long employeeInfoId);

	public ITTicketsDTO getTicketsHardwareAllocatedDetailsAndHistory(Long companyId, String id, Long employeeInfoId);

	public ITTicketsDTO getTicketsSoftwareIssuesDetailsAndHistory(Long companyId, String id, Long employeeInfoId);

	public ITTicketsDTO getTicketsHardwareIssuesDetailsAndHistory(Long companyId, String id, Long employeeInfoId);

	public ITTicketsDTO getTicketsEmailAndIdCardDetailsAndHistory(Long companyId, String id, Long employeeInfoId);

	public CompanyTicketDto updateTicketHistory(UpdateITTicketDTO updateTicketDTO, Long employeeInfoId);

}
