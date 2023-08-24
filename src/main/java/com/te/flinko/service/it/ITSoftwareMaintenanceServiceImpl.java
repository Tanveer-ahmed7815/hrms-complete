package com.te.flinko.service.it;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.constants.admindept.AdminDeptConstants;
import com.te.flinko.dto.admindept.CompanyPCLaptopDTO;
import com.te.flinko.dto.admindept.PcLaptopSoftwareDetailsDTO;
import com.te.flinko.dto.it.mongo.PcLaptopSoftwareRenewalDTO;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.entity.it.PcLaptopSoftwareDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.admindept.CompanyPCLaptopDetailsNotFoundException;
import com.te.flinko.repository.it.ITPcLaptopRepository;
import com.te.flinko.repository.it.ITSoftwareMaintenanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ITSoftwareMaintenanceServiceImpl implements ITSoftwareMaintenanceService {

	@Autowired
	ITPcLaptopRepository pcLaptopRepository;

	@Autowired
	ITSoftwareMaintenanceRepository itSoftwareMaintainanceRepository;

	private static final String WORKING = "Working";
	private static final String NOT_WORKING = "Not Working";

	/* Software Maintenance */

	@Override
	public List<CompanyPCLaptopDTO> getITSoftwareMaintenanceDetails(Long companyId) {
		log.info("Get the list of software maintenance details against comapnyId:: ", companyId);
		List<CompanyPcLaptopDetails> softwareMaintenanceDetails = pcLaptopRepository
				.findByCompanyInfoCompanyId(companyId);
		if (softwareMaintenanceDetails.isEmpty()) {
			return Collections.emptyList();
		}

		return softwareMaintenanceDetails.stream().filter(x -> Boolean.TRUE.equals(x.getCpldIsWorking())).map(x -> {
			CompanyPCLaptopDTO companyPCLaptopDTO = new CompanyPCLaptopDTO();
			BeanUtils.copyProperties(x, companyPCLaptopDTO);
			companyPCLaptopDTO.setStatus(Boolean.TRUE.equals(x.getCpldIsWorking()) ? WORKING : NOT_WORKING);

			List<PcLaptopSoftwareDetails> renewalSoftware = x.getPcLaptopSoftwareDetailsList().stream()
					.filter(i -> i.getUninstalledDate() == null && (i.getNotificationDate().isBefore(LocalDate.now())
							|| i.getNotificationDate().isEqual(LocalDate.now())))
					.collect(Collectors.toList());

			companyPCLaptopDTO.setIsRenewalPending(!renewalSoftware.isEmpty());

			long count = x.getPcLaptopSoftwareDetailsList().stream().count();
			companyPCLaptopDTO.setNoOfSoftwareInstalled(count);

			return companyPCLaptopDTO;
		}).filter(x -> x.getStatus().equalsIgnoreCase(WORKING)).collect(Collectors.toList());
	}

	@Override
	public List<PcLaptopSoftwareDetailsDTO> getITSoftwareMaintenanceDetailsList(Long companyId, String serialNumber) {
		log.info("Get the list of software maintenance details against comapnyId:: ",
				companyId + " and Serial Number::" + serialNumber);
		CompanyPcLaptopDetails companyPcLaptopDetails = pcLaptopRepository
				.findBySerialNumberAndCompanyInfoCompanyId(serialNumber, companyId)
				.orElseThrow(() -> new CompanyPCLaptopDetailsNotFoundException(
						AdminDeptConstants.COMPANY_PC_LAPTOP_DETAILS_NOT_FOUND));

		List<PcLaptopSoftwareDetails> pcLaptopSoftwareDetailsList = companyPcLaptopDetails
				.getPcLaptopSoftwareDetailsList();

		return pcLaptopSoftwareDetailsList.stream().map(x -> {
			PcLaptopSoftwareDetailsDTO pcLaptopSoftwareDetailsDTO = new PcLaptopSoftwareDetailsDTO();
			BeanUtils.copyProperties(x, pcLaptopSoftwareDetailsDTO);

			pcLaptopSoftwareDetailsDTO.setIsRenewalPending(
					x.getUninstalledDate() == null && (x.getNotificationDate().isBefore(LocalDate.now())
							|| x.getNotificationDate().isEqual(LocalDate.now())));

			return pcLaptopSoftwareDetailsDTO;
		}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public PcLaptopSoftwareDetailsDTO createOrUpdateNewSoftwares(PcLaptopSoftwareDetailsDTO laptopSoftwareDetailsDTO,
			Long companyId, Long employeeInfoId, String serialNumber) {
		log.info("Create are Update new Software  against softwareDeatilsId:: ",
				laptopSoftwareDetailsDTO.getSoftwareDetailsId());
		CompanyPcLaptopDetails companyPcLaptopDetails = pcLaptopRepository.findById(serialNumber).get();

		Optional<PcLaptopSoftwareDetails> softwareDetails = itSoftwareMaintainanceRepository
				.findById(laptopSoftwareDetailsDTO.getSoftwareDetailsId());
		if (softwareDetails.isPresent()) {

			PcLaptopSoftwareDetails pcLaptopSoftwareDetails = softwareDetails.get();
			companyPcLaptopDetails.setPcLaptopSoftwareDetailsList(List.of(pcLaptopSoftwareDetails));
			pcLaptopSoftwareDetails.setCompanyPcLaptopDetails(companyPcLaptopDetails);
			BeanUtils.copyProperties(laptopSoftwareDetailsDTO, pcLaptopSoftwareDetails);
			PcLaptopSoftwareDetails softwareDataDetails = itSoftwareMaintainanceRepository
					.save(pcLaptopSoftwareDetails);
			PcLaptopSoftwareDetailsDTO pcLaptopSoftwareDetailsDTO = new PcLaptopSoftwareDetailsDTO();
			BeanUtils.copyProperties(softwareDataDetails, pcLaptopSoftwareDetailsDTO);
			return pcLaptopSoftwareDetailsDTO;

		} else {
			PcLaptopSoftwareDetails pcLaptopSoftwareDetails = new PcLaptopSoftwareDetails();
			BeanUtils.copyProperties(laptopSoftwareDetailsDTO, pcLaptopSoftwareDetails);
			PcLaptopSoftwareDetails softwareDataDetails = itSoftwareMaintainanceRepository
					.save(pcLaptopSoftwareDetails);
			companyPcLaptopDetails.setPcLaptopSoftwareDetailsList(List.of(pcLaptopSoftwareDetails));
			softwareDataDetails.setCompanyPcLaptopDetails(companyPcLaptopDetails);
			PcLaptopSoftwareDetailsDTO pcLaptopSoftwareDetailsDTO = new PcLaptopSoftwareDetailsDTO();
			BeanUtils.copyProperties(softwareDataDetails, pcLaptopSoftwareDetailsDTO);
			return pcLaptopSoftwareDetailsDTO;
		}

	}

	@Override
	public PcLaptopSoftwareRenewalDTO updateRenewalStatus(PcLaptopSoftwareRenewalDTO pcLaptopSoftwareRenewalDTO,
			Long companyId, Long employeeInfoId) {
		log.info(" Update renewal status  against softwareDeatilsId:: ",
				pcLaptopSoftwareRenewalDTO.getSoftwareDetailsId());

		Optional<PcLaptopSoftwareDetails> softwareDetails = itSoftwareMaintainanceRepository
				.findById(pcLaptopSoftwareRenewalDTO.getSoftwareDetailsId());
		if (!softwareDetails.isPresent()) {
			throw new DataNotFoundException("PcLaptopSoftwareDetails data not found");
		}
		PcLaptopSoftwareDetails pcLaptopSoftwareDetails = softwareDetails.get();

		pcLaptopSoftwareDetails.setExpirationDate(pcLaptopSoftwareRenewalDTO.getExpirationDate());
		pcLaptopSoftwareDetails.setNotificationDate(pcLaptopSoftwareRenewalDTO.getNotificationDate());
		pcLaptopSoftwareDetails.setIsRenewed(false);
		PcLaptopSoftwareDetails pcLaptopDetails = itSoftwareMaintainanceRepository.save(pcLaptopSoftwareDetails);
		PcLaptopSoftwareRenewalDTO pcLaptopSoftwareRenewalDTO2 = new PcLaptopSoftwareRenewalDTO();
		BeanUtils.copyProperties(pcLaptopDetails, pcLaptopSoftwareRenewalDTO2);
		return pcLaptopSoftwareRenewalDTO2;

	}

}
