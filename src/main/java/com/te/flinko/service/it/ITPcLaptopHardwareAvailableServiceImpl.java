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
public class ITPcLaptopHardwareAvailableServiceImpl implements ITPcLaptopHardwareAvailableService {

	@Autowired
	ITPcLaptopRepository pcLaptopRepository;

	@Autowired
	EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	ITOtherItemRepository itOtherItemRepository;

	private static final String WORKING = "Working";
	private static final String NOT_WORKING = "Not Working";

	/*
	 * Working PC Laptop API's
	 */

	@Override
	public List<CompanyPCLaptopDTO> getITPCLaptopDetails(Long companyId) {

		log.info("Get list of IT PC Laptop details against company id::", companyId);
		List<CompanyPcLaptopDetails> companyPcLaptopDetail = pcLaptopRepository.findByCompanyInfoCompanyId(companyId);
		if (companyPcLaptopDetail.isEmpty()) {
			return Collections.emptyList();
		}

		return companyPcLaptopDetail.stream()
				.filter(x -> x.getEmployeePersonalInfo() == null && Boolean.TRUE.equals(x.getCpldIsWorking()))
				.map(x -> {
					CompanyPCLaptopDTO companyPCLaptopDTO = new CompanyPCLaptopDTO();
					BeanUtils.copyProperties(x, companyPCLaptopDTO);
					companyPCLaptopDTO.setStatus(Boolean.TRUE.equals(x.getCpldIsWorking()) ? WORKING : NOT_WORKING);
					return companyPCLaptopDTO;
				}).filter(x -> x.getStatus().equalsIgnoreCase(WORKING)).collect(Collectors.toList());

	}

	@Override
	public CompanyPCLaptopDTO getITPCLaptopDetailsAndHistory(Long companyId, String serialNumber) {

		log.info("Get list of IT PC Laptop details against company id :: " + companyId + " and Serial Number:: "
				+ serialNumber);
		CompanyPcLaptopDetails companyPcLaptopDetails = pcLaptopRepository
				.findBySerialNumberAndCompanyInfoCompanyId(serialNumber, companyId)
				.orElseThrow(() -> new CompanyPCLaptopDetailsNotFoundException(
						AdminDeptConstants.COMPANY_PC_LAPTOP_DETAILS_NOT_FOUND));
		CompanyPCLaptopDTO companyPCLaptopDTO = new CompanyPCLaptopDTO();
		BeanUtils.copyProperties(companyPcLaptopDetails, companyPCLaptopDTO);

		Map<String, Map<String, String>> cpldHistory = companyPcLaptopDetails.getCpldHistory();
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
		log.info("IT PC/Laptop details fetched with respect to serial number");
		return companyPCLaptopDTO;
	}

	/*
	 * IT Working Other items
	 */
	@Override
	public List<CompanyHardwareItemsDTO> getOtherItems(Long companyId) {

		log.info("Get list of IT other items details against company id :: " + companyId);
		List<CompanyHardwareItems> itOtherItems = itOtherItemRepository.findByCompanyInfoCompanyId(companyId);

		if (itOtherItems.isEmpty()) {
			return Collections.emptyList();
		}

		return itOtherItems.stream()
				.filter(x -> x.getEmployeePersonalInfo() == null && Boolean.TRUE.equals(x.getIsWorking())).map(x -> {
					CompanyHardwareItemsDTO companyHardwareItemsDTO = new CompanyHardwareItemsDTO();
					BeanUtils.copyProperties(x, companyHardwareItemsDTO);
					companyHardwareItemsDTO.setStatus(Boolean.TRUE.equals(x.getIsWorking()) ? WORKING : NOT_WORKING);
					return companyHardwareItemsDTO;
				}).collect(Collectors.toList());
	}

	@Override
	public CompanyHardwareItemsDTO getOtherItemsDetailsAndHistory(Long companyId, String indentificationNumber) {
		log.info("Get list of IT other items details and history against company id :: " + companyId
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
					historyDTO1.setDeAllocatedDate(entry2.getKey());

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

	/*
	 * Not Working PC Laptop API's
	 */

}
