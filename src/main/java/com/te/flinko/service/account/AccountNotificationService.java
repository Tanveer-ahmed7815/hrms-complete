package com.te.flinko.service.account;

import java.util.ArrayList;
import java.util.List;

import com.te.flinko.dto.account.AccountMarkAsPaidDTO;
import com.te.flinko.dto.account.AccountPaySlipDTO;
import com.te.flinko.dto.account.AccountPaySlipListDTO;
import com.te.flinko.dto.account.AccountReimbursementMarkAsPaidDTO;
import com.te.flinko.dto.account.AccountSalaryDTO;
import com.te.flinko.dto.account.AccountSalaryInputDTO;
import com.te.flinko.dto.account.AdvanceSalaryDTO;
import com.te.flinko.dto.account.GeneratePayslipInputDTO;
import com.te.flinko.dto.account.MarkAsPaidInputDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryDTO;
import com.te.flinko.dto.account.MarkAsPaidSalaryListDTO;
import com.te.flinko.dto.account.MarkAsReimbursedDTO;
import com.te.flinko.dto.account.ReimbursementInfoByIdDTO;
import com.te.flinko.dto.account.ReimbursementListDTO;
import com.te.flinko.dto.employee.EmployeeReviseSalaryDTO;
import com.te.flinko.entity.employee.EmployeeReimbursementInfo;

public interface AccountNotificationService {

	ArrayList<ReimbursementListDTO> reimbursement(Long companyId);

	ReimbursementInfoByIdDTO reimbursementById(Long companyId, Long reimbursementId);

	List<AccountMarkAsPaidDTO> markAsPaidAdvanceSalary(Long companyId, MarkAsPaidInputDTO advanceSalaryId);

	ArrayList<AccountReimbursementMarkAsPaidDTO> markAsReimbursed(Long companyId, MarkAsReimbursedDTO reimbursementIdList);

	ArrayList<MarkAsPaidSalaryListDTO> markAsPaidSalary(MarkAsPaidSalaryDTO markAsPaidSalaryDTO, Long companyId);

	ArrayList<MarkAsPaidSalaryListDTO> generatePaySlip(Long companyId, GeneratePayslipInputDTO employeeSalaryIdList);

	AccountPaySlipDTO paySlipDetailsById(Long employeeSalaryId, Long companyId);
	
	List<AdvanceSalaryDTO> advanceSalary(Long companyId);
	
	List<AccountSalaryDTO> salaryDetailsList(Long companyId);
	
	List<AccountPaySlipListDTO> paySlip(Long companyId);
	
	List<EmployeeReviseSalaryDTO> reviseSalary(Long companyId);


}
