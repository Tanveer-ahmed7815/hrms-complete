package com.te.flinko.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.hr.EventManagementDepartmentNameDTO;
import com.te.flinko.entity.Department;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.superadmin.PaymentDetails;
import com.te.flinko.entity.superadmin.PlanDetails;
import com.te.flinko.exception.EmployeeNotFoundException;
import com.te.flinko.repository.DepartmentRepository;
import com.te.flinko.repository.admin.CompanyInfoRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService{

	@Autowired
	private CompanyInfoRepository companyInfoRepository;

	@Autowired
	private DepartmentRepository departmentInfoRepository;

	@Override
	public List<EventManagementDepartmentNameDTO> fetchDepartmentFromPlan(Long companyId) {

		CompanyInfo company = companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new EmployeeNotFoundException("Company Not Found"));
		List<Department> departmentsList = departmentInfoRepository.findAll();
		List<EventManagementDepartmentNameDTO> eventManagementDepartmentList = new ArrayList<>();
		List<PaymentDetails> paymentDetailsList = company.getPaymentDetailsList();
		if (!paymentDetailsList.isEmpty()) {
			PaymentDetails paymentDetails = paymentDetailsList.get(paymentDetailsList.size() - 1);
			PlanDetails planDetails = paymentDetails.getPlanDetails();
			if (planDetails != null) {
				List<String> departments = planDetails.getDepartments();
				departmentsList.stream().forEach(department -> {
					if (departments.contains(department.getDepartmentName())) {
						eventManagementDepartmentList.add(new EventManagementDepartmentNameDTO(
								department.getDepartmentId(), department.getDepartmentName()));
					}
				});
			}
		}
		return eventManagementDepartmentList;
	}

}
