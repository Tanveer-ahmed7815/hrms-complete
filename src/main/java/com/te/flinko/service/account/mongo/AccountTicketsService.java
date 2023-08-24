package com.te.flinko.service.account.mongo;

import java.util.List;

import com.te.flinko.dto.account.mongo.CompanyAccountTicketsDTO;
import com.te.flinko.dto.account.mongo.UpdateAccountTicketDTO;

public interface AccountTicketsService {

	public List<CompanyAccountTicketsDTO> getAccountSaleAndPurchaseTicketList(Long companyId, Long employeeInfoId);

	public CompanyAccountTicketsDTO getAccountSaleAndPurchaseDetailsAndHistory(Long companyId, String objectTicketId,
			Long employeeInfoId);

	public List<CompanyAccountTicketsDTO> getEmployeeTicketList(Long companyId, Long employeeInfoId);

	public CompanyAccountTicketsDTO getAccountEmployeeDetailsAndHistory(Long companyId, String objectTicketId,
			Long employeeInfoId);
	
	public List<CompanyAccountTicketsDTO> getOthersTicketList(Long companyId, Long employeeInfoId);
	
	public CompanyAccountTicketsDTO getAccountOthersDetailsAndHistory(Long companyId, String objectTicketId,
			Long employeeInfoId);

	public CompanyAccountTicketsDTO updateAccountTicketHistory(UpdateAccountTicketDTO updateTicketDTO, Long employeeInfoId);
}
