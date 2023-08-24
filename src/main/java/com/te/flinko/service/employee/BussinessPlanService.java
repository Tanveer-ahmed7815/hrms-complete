package com.te.flinko.service.employee;

import com.te.flinko.dto.admin.BussinessPlanDTO;
import com.te.flinko.dto.admin.PlanDTO;

public interface BussinessPlanService {

	public String addBussinessPlan(BussinessPlanDTO bussinessPlanDTO, String terminalId, Long comapnyId);

	public String addUserPlan(BussinessPlanDTO bussinessPlanDTO, String terminalId);

	public BussinessPlanDTO bussinessRegistration(String terminalId);

	public String planDTO(String terminalId,PlanDTO planDTO);
}
