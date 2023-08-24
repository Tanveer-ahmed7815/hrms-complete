package com.te.flinko.service.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.te.flinko.dto.account.OfficeExpensesDTO;
import com.te.flinko.dto.account.OfficeExpensesTotalCostDTO;
import com.te.flinko.entity.admin.CompanyExpenseCategories;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReimbursementInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.admin.CompanyExpenseCategoriesRepository;
import com.te.flinko.repository.employee.EmployeePersonelInfoRepository;
import com.te.flinko.repository.employee.EmployeeReimbursementInfoRepository;
import com.te.flinko.util.S3UploadFile;

@Service
public class OfficeExpensesServiceImpl implements OfficeExpenesesService {

	@Autowired
	EmployeeReimbursementInfoRepository employeeReimbursementInfoRepository;

	@Autowired
	EmployeePersonelInfoRepository employeePersonelInfoRepository;

	@Autowired
	CompanyExpenseCategoriesRepository categoriesRepository;

	@Autowired
	S3UploadFile uploadFile;

	@Override
	public List<OfficeExpensesTotalCostDTO> getOfficeExpenseDetails(Long companyId) {
		List<EmployeeReimbursementInfo> employeeReimbursementInfos = employeeReimbursementInfoRepository
				.findByCompanyExpenseCategoriesCompanyInfoCompanyId(companyId);
		List<CompanyExpenseCategories> listOfCompanyExpensesCategories = categoriesRepository
				.findByCompanyInfoCompanyId(companyId)
				.orElseThrow(() -> new DataNotFoundException("Company expense Categories details not found"));
		if (listOfCompanyExpensesCategories.isEmpty())
			throw new DataNotFoundException("No details found");
		Map<String, List<EmployeeReimbursementInfo>> groupedCategories = new HashMap<>();
		listOfCompanyExpensesCategories.stream().forEach((i) -> {
			List<EmployeeReimbursementInfo> listOfReibursement = new ArrayList<>();
			employeeReimbursementInfos.forEach((r) -> {
				if (i.getExpenseCategoryName().equals(r.getCompanyExpenseCategories().getExpenseCategoryName())) {
					listOfReibursement.add(r);
				}
			});
			groupedCategories.put(i.getExpenseCategoryName(), listOfReibursement);
		});

		List<OfficeExpensesTotalCostDTO> response = new ArrayList<>();
		groupedCategories.forEach((n, l) -> {
			OfficeExpensesTotalCostDTO officeExpensesTotalCostDTO = new OfficeExpensesTotalCostDTO();
			officeExpensesTotalCostDTO.setType(n);
			if (!l.isEmpty()) {
				CompanyExpenseCategories companyExpenseCategories = l.get(0).getCompanyExpenseCategories();
				if (companyExpenseCategories != null) {
					officeExpensesTotalCostDTO.setExpenseCategoryId(companyExpenseCategories.getExpenseCategoryId());
				}
			}
			officeExpensesTotalCostDTO.setTotalCost(l.stream().mapToDouble((i) -> i.getAmount().doubleValue()).sum());
			response.add(officeExpensesTotalCostDTO);
		});
		return response;
	}

	@Override
	public List<OfficeExpensesDTO> getReimbursementByCategory(Long expenseCategoryId, Long companyId) {
		List<OfficeExpensesDTO> officeExpensesDTOList = new ArrayList<>();
		List<EmployeeReimbursementInfo> employeeReimbursementInfoList = employeeReimbursementInfoRepository
				.findByCompanyExpenseCategoriesCompanyInfoCompanyIdAndCompanyExpenseCategoriesExpenseCategoryId(
						companyId, expenseCategoryId);
		for (EmployeeReimbursementInfo employeeReimbursementInfo : employeeReimbursementInfoList) {
			OfficeExpensesDTO officeExpensesDTO = new OfficeExpensesDTO();
			BeanUtils.copyProperties(employeeReimbursementInfo, officeExpensesDTO);
			CompanyExpenseCategories companyExpenseCategories = employeeReimbursementInfo.getCompanyExpenseCategories();
			if (companyExpenseCategories != null) {
				officeExpensesDTO.setExpenseCategoryId(companyExpenseCategories.getExpenseCategoryId());
				officeExpensesDTO.setExpenseCategoryName(companyExpenseCategories.getExpenseCategoryName());
			}
			EmployeePersonalInfo employeePersonalInfo = employeeReimbursementInfo.getEmployeePersonalInfo();
			officeExpensesDTO.setEmployeeFullName(employeePersonalInfo == null ? null
					: employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
			officeExpensesDTO.setExpenseDate(employeeReimbursementInfo.getExpenseDate().toString());
			officeExpensesDTO
					.setStatus(employeeReimbursementInfo.getStatus().equalsIgnoreCase("Accepted") ? "Not Reimbursed"
							: employeeReimbursementInfo.getStatus());
			officeExpensesDTOList.add(officeExpensesDTO);
		}
		return officeExpensesDTOList;
	}

	@Override
	@Transactional
	public OfficeExpensesDTO addOfficeExpenses(OfficeExpensesDTO addOfficeExpensesDTO, MultipartFile multipartFile,
			Long companyId) {
		EmployeePersonalInfo employeePersonalInfo = employeePersonelInfoRepository
				.findById(addOfficeExpensesDTO.getEmployeeInfoId())
				.orElseThrow(() -> new DataNotFoundException("Employee Details Not Found"));

		CompanyExpenseCategories companyExpenseCategories = categoriesRepository
				.findByExpenseCategoryIdAndCompanyInfoCompanyId(addOfficeExpensesDTO.getExpenseCategoryId(), companyId)
				.orElseThrow(() -> new DataNotFoundException("Category not found"));

		EmployeeReimbursementInfo employeeReimbursementInfo = new EmployeeReimbursementInfo();

		BeanUtils.copyProperties(addOfficeExpensesDTO, employeeReimbursementInfo);
		employeeReimbursementInfo.setEmployeePersonalInfo(employeePersonalInfo);
		employeeReimbursementInfo.setCompanyExpenseCategories(companyExpenseCategories);
		employeeReimbursementInfo.setExpenseDate(LocalDate.parse(addOfficeExpensesDTO.getExpenseDate()));

		employeeReimbursementInfo.setAttachmentUrl(uploadFile.uploadFile(multipartFile));

		EmployeeReimbursementInfo saved = employeeReimbursementInfoRepository.save(employeeReimbursementInfo);

		OfficeExpensesDTO responseDTO = new OfficeExpensesDTO();
		BeanUtils.copyProperties(saved, responseDTO);
		return responseDTO;
	}

	@Transactional
	@Override
	public OfficeExpensesDTO updateOfficeExpenses(Long reimbursementId, String status) {
		EmployeeReimbursementInfo employeeReimbursementInfo = employeeReimbursementInfoRepository
				.findById(reimbursementId).orElseThrow(() -> new DataNotFoundException("Reimbursement Not Found"));
		employeeReimbursementInfo.setStatus(status);
		if (status.equalsIgnoreCase("Reimbursed")) {
			employeeReimbursementInfo.setIsPaid(true);
		}
		employeeReimbursementInfo = employeeReimbursementInfoRepository.save(employeeReimbursementInfo);

		OfficeExpensesDTO responseDTO = new OfficeExpensesDTO();
		BeanUtils.copyProperties(employeeReimbursementInfo, responseDTO);
		return responseDTO;
	}

	@Override
	public OfficeExpensesDTO getReimbursementById(Long reimbursementId) {
		EmployeeReimbursementInfo reimbursementInfo = employeeReimbursementInfoRepository.findById(reimbursementId)
				.orElseThrow(() -> new DataNotFoundException("Reimbursment Info Not Found"));
		OfficeExpensesDTO officeExpensesDTO = new OfficeExpensesDTO();
		BeanUtils.copyProperties(reimbursementInfo, officeExpensesDTO);
		CompanyExpenseCategories companyExpenseCategories = reimbursementInfo.getCompanyExpenseCategories();
		if (companyExpenseCategories != null) {
			officeExpensesDTO.setExpenseCategoryId(companyExpenseCategories.getExpenseCategoryId());
			officeExpensesDTO.setExpenseCategoryName(companyExpenseCategories.getExpenseCategoryName());
		}
		EmployeePersonalInfo employeePersonalInfo = reimbursementInfo.getEmployeePersonalInfo();
		officeExpensesDTO.setEmployeeFullName(employeePersonalInfo == null ? null
				: employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		officeExpensesDTO.setExpenseDate(reimbursementInfo.getExpenseDate().toString());
		officeExpensesDTO
		.setStatus(reimbursementInfo.getStatus().equalsIgnoreCase("Accepted") ? "Not Reimbursed"
				: reimbursementInfo.getStatus());
		return officeExpensesDTO;
	}

}
