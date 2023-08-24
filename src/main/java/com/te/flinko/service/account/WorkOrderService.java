package com.te.flinko.service.account;

import java.util.List;

import com.te.flinko.dto.account.CreatWorkOrderDealDropdownDto;
import com.te.flinko.dto.account.WorkOrderDTO;
import com.te.flinko.dto.account.WorkOrderListDto;

public interface WorkOrderService {

	List<WorkOrderListDto> getWorkOrderList(Long companyId);

	List<CreatWorkOrderDealDropdownDto> getCompanyDeals(Long companyId);
	
	public WorkOrderDTO getWorkOrderDetails(Long companyId,Long workOrderId,Long employeeInfoId);
}
