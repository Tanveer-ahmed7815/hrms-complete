package com.te.flinko.service.it;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.constants.admindept.AdminDeptConstants;
import com.te.flinko.dto.admindept.CompanyHardwareItemsDTO;
import com.te.flinko.dto.admindept.CompanyPCLaptopDTO;
import com.te.flinko.dto.admindept.HistoryDTO;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.it.CompanyHardwareItems;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.exception.admin.EmployeeNotFoundException;
import com.te.flinko.exception.admin.NoEmployeeOfficialInfoException;
import com.te.flinko.exception.admindept.CompanyPCLaptopDetailsNotFoundException;
import com.te.flinko.exception.it.ITOtherItemsDetailsNotFoundException;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.it.ITOtherItemRepository;
import com.te.flinko.repository.it.ITPcLaptopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ITPcLaptopHardwareAlloctedServiceImpl implements ITPcLaptopHardwareAlloctedService {

	@Autowired
	ITPcLaptopRepository pcLaptopRepository;

	@Autowired
	EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	ITOtherItemRepository itOtherItemRepository;

	private static final String WORKING = "Working";
	private static final String NOT_WORKING = "Not Working";

	@Override
	public List<CompanyPCLaptopDTO> getITPCLaptopAlloctedDetails(Long companyId) {
		log.info("Get list of IT PC Laptop allocted details against company id::", companyId);

		List<CompanyPcLaptopDetails> pcLaptopAlloctedDetails = pcLaptopRepository.findByCompanyInfoCompanyId(companyId);
		if (pcLaptopAlloctedDetails.isEmpty()) {
			return Collections.emptyList();
		}

		return pcLaptopAlloctedDetails.stream()
				.filter(x -> x.getEmployeePersonalInfo() != null && Boolean.TRUE.equals(x.getCpldIsWorking()))
				.map(i -> {
					CompanyPCLaptopDTO companyPCLaptopDTO = new CompanyPCLaptopDTO();
					BeanUtils.copyProperties(i, companyPCLaptopDTO);
					companyPCLaptopDTO.setEmployeeName(
							i.getEmployeePersonalInfo().getFirstName()+" " + i.getEmployeePersonalInfo().getLastName());
					companyPCLaptopDTO.setStatus(Boolean.TRUE.equals(i.getCpldIsWorking()) ? WORKING : NOT_WORKING);
					return companyPCLaptopDTO;
				}).collect(Collectors.toList());
	}

	@Override
	public CompanyPCLaptopDTO getITPCLaptopAlloctedDetailsAndHistory(Long companyId, String serialNumber) {
		log.info("Get list of IT PC Laptop allocted details and History against company id::" + companyId
				+ " and serial number::" + serialNumber);

		CompanyPcLaptopDetails alloctedDetailsAndHistory = pcLaptopRepository
				.findBySerialNumberAndCompanyInfoCompanyId(serialNumber, companyId)
				.orElseThrow(() -> new CompanyPCLaptopDetailsNotFoundException(
						AdminDeptConstants.COMPANY_PC_LAPTOP_DETAILS_NOT_FOUND));
		CompanyPCLaptopDTO companyPCLaptopDTO = new CompanyPCLaptopDTO();
		BeanUtils.copyProperties(alloctedDetailsAndHistory, companyPCLaptopDTO);
		Map<String, Map<String, String>> cpldHistory = alloctedDetailsAndHistory.getCpldHistory();
		List<HistoryDTO> historyDTO = new ArrayList<>();
		if (cpldHistory == null) {
			companyPCLaptopDTO.setHistoryDTOs(null);
		} else {
			Set<Entry<String, Map<String, String>>> entrySet = cpldHistory.entrySet();
			for (Entry<String, Map<String, String>> entry : entrySet) {
				HistoryDTO historyDTO1 = new HistoryDTO();
				Set<Entry<String, String>> entrySet2 = entry.getValue().entrySet();
				for (Entry<String, String> entry2 : entrySet2) {
					historyDTO1.setDeAllocatedDate(entry2.getKey().equalsIgnoreCase("null") ? null : entry2.getKey());
					EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
							.findById(Long.parseLong(entry2.getValue()))
							.orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
					EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
					if (employeeOfficialInfo == null) {
						throw new NoEmployeeOfficialInfoException("Employee official details not found");
					}
					historyDTO1.setEmployeeId(employeeOfficialInfo.getEmployeeId());
					historyDTO1.setName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
				}
				historyDTO1.setAllocatedDate(entry.getKey());
				historyDTO.add(historyDTO1);
			}

			companyPCLaptopDTO.setHistoryDTOs(historyDTO);
		}

		return companyPCLaptopDTO;
	}

	@Override
	public List<CompanyHardwareItemsDTO> getAllocatedOtherItemsDetails(Long companyId) {
		log.info("Get list of IT PC Laptop allocted other items details against company id::", companyId);
		List<CompanyHardwareItems> otherItemsDetails = itOtherItemRepository.findByCompanyInfoCompanyId(companyId);
		if (otherItemsDetails.isEmpty()) {
			return Collections.emptyList();
		}

		return otherItemsDetails.stream()
				.filter(x -> x.getEmployeePersonalInfo() != null && Boolean.TRUE.equals(x.getIsWorking())).map(i -> {
					CompanyHardwareItemsDTO companyHardwareItemsDTO = new CompanyHardwareItemsDTO();
					BeanUtils.copyProperties(i, companyHardwareItemsDTO);
					companyHardwareItemsDTO.setEmployeeName(i.getEmployeePersonalInfo().getFirstName() + " "
							+ i.getEmployeePersonalInfo().getLastName());
					companyHardwareItemsDTO.setStatus(Boolean.TRUE.equals(i.getIsWorking()) ? WORKING : NOT_WORKING);

					return companyHardwareItemsDTO;
				}).collect(Collectors.toList());
	}

	@Override
	public CompanyHardwareItemsDTO getAllocatedOtherItemsDetailsAndHistory(Long companyId,
			String indentificationNumber) {
		log.info("Get list of IT allocated other items details and history against company id :: " + companyId
				+ " and indentification Number:: " + indentificationNumber);

		CompanyHardwareItems hardwareItems = itOtherItemRepository
				.findByIndentificationNumberAndCompanyInfoCompanyId(indentificationNumber, companyId)
				.orElseThrow(() -> new ITOtherItemsDetailsNotFoundException("IT other items details are not exist"));

		CompanyHardwareItemsDTO companyHardwareItemsDTO = new CompanyHardwareItemsDTO();
		BeanUtils.copyProperties(hardwareItems, companyHardwareItemsDTO);

		Map<String, Map<String, String>> chiHistory = hardwareItems.getChiHistory();
		List<HistoryDTO> historyDTO = new ArrayList<>();
		if (chiHistory == null) {
			companyHardwareItemsDTO.setHistoryDTOs(null);
		} else {
			Set<Entry<String, Map<String, String>>> entrySet = chiHistory.entrySet();

			for (Entry<String, Map<String, String>> entry : entrySet) {
				HistoryDTO historyDTO1 = new HistoryDTO();

				Set<Entry<String, String>> entrySet2 = entry.getValue().entrySet();
				for (Entry<String, String> entry2 : entrySet2) {
					historyDTO1.setDeAllocatedDate(entry2.getKey().equalsIgnoreCase("null") ? null : entry2.getKey());

					EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
							.findById(Long.parseLong(entry2.getValue()))
							.orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

					EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();

					if (employeeOfficialInfo == null) {
						throw new NoEmployeeOfficialInfoException("Employee official details not found");
					}
					historyDTO1.setEmployeeId(employeeOfficialInfo.getEmployeeId());
					historyDTO1.setName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
				}
				historyDTO1.setAllocatedDate(entry.getKey());
				historyDTO.add(historyDTO1);
			}
			companyHardwareItemsDTO.setHistoryDTOs(historyDTO);

		}

		return companyHardwareItemsDTO;
	}

}
