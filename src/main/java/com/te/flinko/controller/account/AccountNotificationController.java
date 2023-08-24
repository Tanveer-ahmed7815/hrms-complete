package com.te.flinko.controller.account;

import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_ADVANCE_SALARY_SUCCESSFULLY_FETCHED;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_REIMBURSEMENT_PAID_SUCCESSFULLY;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.ADVANCE_SALARY_PAID_SUCCESSFULLY;
import static com.te.flinko.common.hr.HrConstants.EMPLOYEES_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.hr.HrConstants.SALARY_RECORDS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_DETAILS_NOT_FOUND;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_PAID_SUCCESSFULLY;
import static com.te.flinko.common.account.AccountConstants.EMPLOYEE_SALARY_SLIP_GENERATED_SUCCESSFULLY;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.AccountMarkAsPaidDTO;
import com.te.flinko.dto.account.AccountPaySlipDTO;
import com.te.flinko.dto.account.AccountPaySlipInputDTO;
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
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountNotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
/**
 * 
 * @author Ravindra
 *
 */
public class AccountNotificationController extends BaseConfigController {
	@Autowired
	AccountNotificationService service;

	@GetMapping("/reimbursement")
	public ResponseEntity<SuccessResponse> reimbursement() {
		log.info("reimbursement method execution started");
		ArrayList<ReimbursementListDTO> reimbursement = service.reimbursement(getCompanyId());
		if(reimbursement.isEmpty()) {
			return new ResponseEntity<>(
					new SuccessResponse(false, "No Reimbursement data Found", reimbursement),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEES_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY, reimbursement),
				HttpStatus.OK);
	}

	@GetMapping("/reimbursement-by-id")
	public ResponseEntity<SuccessResponse> reimbursementById(@RequestParam Long reimbursementId) {
		log.info("reimbursementById method execution started");
		ReimbursementInfoByIdDTO reimbursementById = service.reimbursementById(getCompanyId(), reimbursementId);
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEE_REIMBURSEMENT_DETAILS_FETCHED_SUCCESSFULLY, reimbursementById),
				HttpStatus.OK);
	}

	@PutMapping("/mark-as-paid-AdvanceSalary")
	public ResponseEntity<SuccessResponse> markAsPaidAdvanceSalary(@RequestBody MarkAsPaidInputDTO advanceSalaryId) {
		log.info("markAsPaidAdvanceSalary method execution started");
		List<AccountMarkAsPaidDTO> markAsPaidAdvanceSalary = service.markAsPaidAdvanceSalary(getCompanyId(),
				advanceSalaryId);
		return new ResponseEntity<>(
				new SuccessResponse(false, ADVANCE_SALARY_PAID_SUCCESSFULLY, markAsPaidAdvanceSalary), HttpStatus.OK);
	}

	@PutMapping("/mark-as-reimbursed")
	public ResponseEntity<SuccessResponse> markAsReimbursed(@RequestBody MarkAsReimbursedDTO reimbursementIdList) {
		log.info("markAsReimbursed method execution started");
		ArrayList<AccountReimbursementMarkAsPaidDTO> markAsReimbursed = service.markAsReimbursed(getCompanyId(),
				reimbursementIdList);
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEE_REIMBURSEMENT_PAID_SUCCESSFULLY, markAsReimbursed), HttpStatus.OK);
	}

	@PutMapping("/mark-as-paid-salary")
	public ResponseEntity<SuccessResponse> markAsPaidSalary(@RequestBody MarkAsPaidSalaryDTO markAsPaidSalaryDTO) {
		log.info("markAsPaidSalary method execution started");
		ArrayList<MarkAsPaidSalaryListDTO> markAsPaidSalary = service.markAsPaidSalary(markAsPaidSalaryDTO,
				getCompanyId());
		return new ResponseEntity<>(new SuccessResponse(false, EMPLOYEE_SALARY_PAID_SUCCESSFULLY, markAsPaidSalary),
				HttpStatus.OK);
	}

	@PutMapping("/generate-pay-slip")
	public ResponseEntity<SuccessResponse> generatePaySlip(@RequestBody GeneratePayslipInputDTO employeeSalaryIdList) {
		log.info("generatePaySlip method execution started");
		ArrayList<MarkAsPaidSalaryListDTO> generatePaySlip = service.generatePaySlip(getCompanyId(),
				employeeSalaryIdList);
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEE_SALARY_SLIP_GENERATED_SUCCESSFULLY, generatePaySlip),
				HttpStatus.OK);
	}

	@GetMapping("/pay-slip-details-by-id")
	public ResponseEntity<SuccessResponse> paySlipDetailsById(@RequestParam Long employeeSalaryId) {
		log.info("paySlipDetailsById method execution started");
		AccountPaySlipDTO paySlipDetailsById = service.paySlipDetailsById(employeeSalaryId, getCompanyId());
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEE_SALARY_DETAILS_FETCHED_SUCCESSFULLY, paySlipDetailsById),
				HttpStatus.OK);
	}

	@GetMapping("/notification/advance-salary")
	public ResponseEntity<SuccessResponse> advanceSalary() {
		log.info("advance salary method execution started");
		List<AdvanceSalaryDTO> advanceSalary = service.advanceSalary(getCompanyId());
		if (advanceSalary == null || advanceSalary.isEmpty()) {
			return new ResponseEntity<>(
					new SuccessResponse(false, EMPLOYEE_ADVANCE_SALARY_DETAILS_NOT_FOUND, advanceSalary),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEES_ADVANCE_SALARY_SUCCESSFULLY_FETCHED, advanceSalary),
				HttpStatus.OK);
	}

	@GetMapping("/notification/pay-slip")
	public ResponseEntity<SuccessResponse> paySlip() {
		List<AccountPaySlipListDTO> paySlip = service.paySlip(getCompanyId());
		if (paySlip == null || paySlip.isEmpty()) {
			return new ResponseEntity<>(new SuccessResponse(false, SALARY_RECORDS_NOT_FOUND, paySlip),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new SuccessResponse(false, EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY, paySlip),
				HttpStatus.OK);
	}

	@GetMapping("/notification/salary")
	public ResponseEntity<SuccessResponse> salaryDetailsList() {
		List<AccountSalaryDTO> salaryDetailsList = service.salaryDetailsList(getCompanyId());

		if (salaryDetailsList == null || salaryDetailsList.isEmpty()) {
			return new ResponseEntity<>(
					new SuccessResponse(false, EMPLOYEE_SALARY_DETAILS_NOT_FOUND, salaryDetailsList),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				new SuccessResponse(false, EMPLOYEES_SALARY_DETAILS_FETCHED_SUCCESSFULLY, salaryDetailsList),
				HttpStatus.OK);
	}
	
	@GetMapping("/notification/revise-salary")
	public ResponseEntity<SuccessResponse> reviseSalary(@RequestParam Long companyId){
		List<EmployeeReviseSalaryDTO> reviseSalary = service.reviseSalary(companyId);
		return new ResponseEntity<>(new SuccessResponse(false,EMPLOYEES_FETCHED_SUCCESSFULLY, reviseSalary),
				HttpStatus.OK);

	}

}
