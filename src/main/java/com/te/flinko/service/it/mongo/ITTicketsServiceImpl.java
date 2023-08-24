package com.te.flinko.service.it.mongo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.helpandsupport.mongo.CompanyTicketDto;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.dto.it.mongo.ITTicketsDTO;
import com.te.flinko.dto.it.mongo.UpdateITTicketDTO;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAccountTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAdminDeptTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyHrTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyItTickets;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.exception.InavlidInputException;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.exception.employee.EmployeeNotFoundException;
import com.te.flinko.exception.it.ITTicketsDetailsNotFoundException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admindept.CompanyItTicketsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAccountTicketsRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAdminDeptTicketsRepo;
import com.te.flinko.repository.helpandsupport.mongo.CompanyHrTicketsRepository;
import com.te.flinko.repository.it.ITPcLaptopRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ITTicketsServiceImpl implements ITTicketsService {

	@Autowired
	CompanyItTicketsRepository companyTicketsRepository;

	@Autowired
	EmployeePersonalInfoRepository employeeInfoRepository;

	@Autowired
	private CompanyAccountTicketsRepository companyAccountTicketsRepository;

	@Autowired
	CompanyHrTicketsRepository hrTicketsRepository;

	@Autowired
	ITPcLaptopRepository pcLaptopRepository;

	@Autowired
	private CompanyAdminDeptTicketsRepo companyAdminDeptTicketsRepo;

	@Autowired
	CompanyInfoRepository infoRepository;

	private static final String HARDWARE_ALLOCATION = "hardWareAllocation";
	private static final String SOFTWARE_ISSUES = "Software Issues";
	private static final String HARDWARE_ISSUES = "hardWareIssues";
	private static final String E_MAIL = "E-Mail";
	private static final String ID_CARD = "ID Card";

	@Override
	public List<CompanyTicketDto> getTicketsHardwareAllocatedDetails(Long companyId, Long employeeInfoId) {
		log.info("Get the list of hardware allocated ticket details against comapnyId:: ", companyId);

		List<CompanyItTickets> comapanyItTickets = this.companyTicketsRepository.findByCompanyId(companyId);

		if (comapanyItTickets.isEmpty()) {
			return Collections.emptyList();
		}

		List<CompanyTicketDto> listOfTicketDto = new ArrayList<>();

		List<CompanyTicketDto> collect = comapanyItTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(HARDWARE_ALLOCATION))
				.map(x -> {

					CompanyTicketDto companyTicketDto = new CompanyTicketDto();
					BeanUtils.copyProperties(x, companyTicketDto);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);
					companyTicketDto.setItTicketId(x.getTicketId());
					companyTicketDto.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyTicketDto.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyTicketDto.setHardwareType(x.getSubCategory());
					companyTicketDto.setMonitoringDepartment(x.getMonitoringDepartment());
					companyTicketDto.setCategory("Hardware Allocation");

					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();

					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyTicketDto.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyTicketDto.setIsAuthorized(true);
						} else {
							companyTicketDto.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}
					}
					companyTicketDto.setSerialNumber(x.getIdentificationNumber());
					companyTicketDto.setItTicketId(x.getTicketId());
					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyTicketDto.setFlag(true);
					}
					return companyTicketDto;
				}).collect(Collectors.toList());

		List<CompanyTicketDto> collect2 = collect.stream().filter(x -> x.getFlag() != null && x.getFlag())
				.collect(Collectors.toList());
		List<CompanyTicketDto> collect3 = collect.stream().filter(x -> x.getFlag() == null || !x.getFlag())
				.collect(Collectors.toList());

		listOfTicketDto.addAll(collect2);
		listOfTicketDto.addAll(collect3);
		return listOfTicketDto;
	}

	@Override
	public List<CompanyTicketDto> getTicketsSoftwareIssuesDetails(Long companyId, Long employeeInfoId) {
		log.info("Get the list of software issues ticket details against comapnyId:: ", companyId);

		List<CompanyItTickets> comapanyItTickets = this.companyTicketsRepository.findByCompanyId(companyId);

		if (comapanyItTickets.isEmpty()) {
			return Collections.emptyList();
		}

		List<CompanyTicketDto> listOfTicketDto = new ArrayList<>();

		List<CompanyTicketDto> collectDTO = comapanyItTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(SOFTWARE_ISSUES)).map(x -> {

					CompanyTicketDto companyTicketDto = new CompanyTicketDto();
					BeanUtils.copyProperties(x, companyTicketDto);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);

					companyTicketDto.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyTicketDto.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyTicketDto.setHardwareType(x.getSubCategory());
					companyTicketDto.setMonitoringDepartment(x.getMonitoringDepartment());
					companyTicketDto.setCategory("Software Issues");
					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();

					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyTicketDto.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyTicketDto.setIsAuthorized(true);
						} else {
							companyTicketDto.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}
					}
					companyTicketDto.setSerialNumber(x.getIdentificationNumber());
					companyTicketDto.setItTicketId(x.getTicketId());

					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyTicketDto.setFlag(true);
					}
////					
					return companyTicketDto;
				}).collect(Collectors.toList());

		List<CompanyTicketDto> collect2 = collectDTO.stream().filter(x -> x.getFlag() != null && x.getFlag())
				.collect(Collectors.toList());
		List<CompanyTicketDto> collect3 = collectDTO.stream().filter(x -> x.getFlag() == null || !x.getFlag())
				.collect(Collectors.toList());

		listOfTicketDto.addAll(collect2);
		listOfTicketDto.addAll(collect3);

		return listOfTicketDto;

	}

	@Override
	public List<CompanyTicketDto> getTicketsHardwareIssuesDetails(Long companyId, Long employeeInfoId) {
		log.info("Get the list of software issues ticket details against comapnyId:: ", companyId);

		List<CompanyItTickets> comapanyItTickets = this.companyTicketsRepository.findByCompanyId(companyId);

		if (comapanyItTickets.isEmpty()) {
			return Collections.emptyList();
		}

		List<CompanyTicketDto> listOfTicketDto = new ArrayList<>();

		List<CompanyTicketDto> collect = comapanyItTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(HARDWARE_ISSUES)).map(x -> {

					CompanyTicketDto companyTicketDto = new CompanyTicketDto();
					BeanUtils.copyProperties(x, companyTicketDto);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);

					companyTicketDto.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyTicketDto.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyTicketDto.setHardwareType(x.getSubCategory());
					companyTicketDto.setCategory("Hardware Issues");
					companyTicketDto.setMonitoringDepartment(x.getMonitoringDepartment());
//					List<CompanyPcLaptopDetails> companyPcLaptopDetailsList = employeeInfo
//							.getCompanyPcLaptopDetailsList();
//					if (companyPcLaptopDetailsList != null) {
//
//						CompanyPcLaptopDetails companyPcLaptopDetails = new CompanyPcLaptopDetails();
//						companyPcLaptopDetailsList.add(companyPcLaptopDetails);
//						companyTicketDto.setSerialNumber(companyPcLaptopDetails.getSerialNumber());
//					}
					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();

					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyTicketDto.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyTicketDto.setIsAuthorized(true);
						} else {
							companyTicketDto.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}
					}
					companyTicketDto.setSerialNumber(x.getIdentificationNumber());
					companyTicketDto.setItTicketId(x.getTicketId());
					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyTicketDto.setFlag(true);
					}
					return companyTicketDto;
				}).collect(Collectors.toList());
		
		List<CompanyTicketDto> collect2 = collect.stream().filter(x -> x.getFlag() != null && x.getFlag())
				.collect(Collectors.toList());
		List<CompanyTicketDto> collect3 = collect.stream().filter(x -> x.getFlag() == null || !x.getFlag())
				.collect(Collectors.toList());

		listOfTicketDto.addAll(collect2);
		listOfTicketDto.addAll(collect3);
		return listOfTicketDto;
	}

	@Override
	public List<CompanyTicketDto> getTicketsEmailDetails(Long companyId, Long employeeInfoId) {
		log.info("Get the list of software issues ticket details against comapnyId:: ", companyId);

		List<CompanyItTickets> comapanyItTickets = this.companyTicketsRepository.findByCompanyId(companyId);

		if (comapanyItTickets.isEmpty()) {
			return Collections.emptyList();
		}
		List<CompanyTicketDto> listOfTicketDto=new ArrayList<>();
		 List<CompanyTicketDto> collect = comapanyItTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(E_MAIL)
						|| i.getCategory().equalsIgnoreCase(ID_CARD))
				.map(x -> {

					CompanyTicketDto companyTicketDto = new CompanyTicketDto();
					BeanUtils.copyProperties(x, companyTicketDto);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);

					companyTicketDto.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyTicketDto.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyTicketDto.setMonitoringDepartment(x.getMonitoringDepartment());
					companyTicketDto.setHardwareType(x.getSubCategory());
//					List<CompanyPcLaptopDetails> companyPcLaptopDetailsList = employeeInfo
//							.getCompanyPcLaptopDetailsList();
//					if (companyPcLaptopDetailsList != null) {
//
//						CompanyPcLaptopDetails companyPcLaptopDetails = new CompanyPcLaptopDetails();
//						companyPcLaptopDetailsList.add(companyPcLaptopDetails);
//						companyTicketDto.setSerialNumber(companyPcLaptopDetails.getSerialNumber());
//					}
					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();

					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyTicketDto.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyTicketDto.setIsAuthorized(true);
						} else {
							companyTicketDto.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}
					}
					companyTicketDto.setItTicketId(x.getTicketId());
					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyTicketDto.setFlag(true);
					}
					return companyTicketDto;
				}).collect(Collectors.toList());

		 List<CompanyTicketDto> collect2 = collect.stream().filter(x->x.getFlag()!=null && x.getFlag()).collect(Collectors.toList());
		 List<CompanyTicketDto> collect3 = collect.stream().filter(x-> x.getFlag()==null || !x.getFlag()).collect(Collectors.toList());
		 
		 listOfTicketDto.addAll(collect2);	
		 listOfTicketDto.addAll(collect3);
		 return listOfTicketDto;
	}

	@Override
	public ITTicketsDTO getTicketsHardwareAllocatedDetailsAndHistory(Long companyId, String id, Long employeeInfoId) {

		log.info("Get the hardware allocated ticket details and history against comapnyId:: ", companyId);

		CompanyItTickets companyTicketsdetails = companyTicketsRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(
						() -> new ITTicketsDetailsNotFoundException("Details are not found against companyId and id"));

		ITTicketsDTO itTicketsDTO = new ITTicketsDTO();

		BeanUtils.copyProperties(companyTicketsdetails, itTicketsDTO);

		itTicketsDTO.setQuestionAnswer(companyTicketsdetails.getQuestionAnswer());

		List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId = employeeInfoRepository
				.findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(companyTicketsdetails.getEmployeeId(),
						companyId);

		if (findByEmployeeOfficialInfoEmployeeId.isEmpty() && findByEmployeeOfficialInfoEmployeeId.get(0) == null) {
			throw new EmployeeNotFoundException("Employee not found");
		}
		EmployeePersonalInfo employeePersonalInfo = findByEmployeeOfficialInfoEmployeeId.get(0);
		itTicketsDTO.setEmployeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setHardwareType(companyTicketsdetails.getSubCategory());
		itTicketsDTO.setMonitoringDepartment(companyTicketsdetails.getMonitoringDepartment());
		itTicketsDTO.setProductName(
				companyTicketsdetails.getProductName() == null ? "--" : companyTicketsdetails.getProductName());

		itTicketsDTO.setRaisedDate(companyTicketsdetails.getCreatedDate().toLocalDate());
		itTicketsDTO.setQuestionAnswer(companyTicketsdetails.getQuestionAnswer());
		itTicketsDTO.setItTicketId(companyTicketsdetails.getTicketId());

		List<CompanyPcLaptopDetails> companyPcLaptopDetailsList = employeePersonalInfo.getCompanyPcLaptopDetailsList();
		if (!companyPcLaptopDetailsList.isEmpty()) {
			CompanyPcLaptopDetails companyPcLaptopDetails = new CompanyPcLaptopDetails();
			companyPcLaptopDetailsList.add(companyPcLaptopDetails);
			itTicketsDTO.setSerialNumber(companyPcLaptopDetails.getSerialNumber());
		}
		List<TicketHistroy> ticketHistroys = companyTicketsdetails.getTicketHistroys();

		List<EmployeePersonalInfo> employeeInfo = employeeInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {

			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());
				}
			}
		}

		if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			itTicketsDTO.setStatus(ticketHistroy.getStatus());

			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				itTicketsDTO.setIsAuthorized(true);
			} else {
				itTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}

		return itTicketsDTO;
	}

	@Override
	public ITTicketsDTO getTicketsSoftwareIssuesDetailsAndHistory(Long companyId, String id, Long employeeInfoId) {

		log.info("Get the software issues ticket details and history against comapnyId:: ", companyId);

		CompanyItTickets companyTicketsdetails = companyTicketsRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(
						() -> new ITTicketsDetailsNotFoundException("Details are not found against companyId and id"));

		ITTicketsDTO itTicketsDTO = new ITTicketsDTO();

		BeanUtils.copyProperties(companyTicketsdetails, itTicketsDTO);

		List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId = employeeInfoRepository
				.findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(companyTicketsdetails.getEmployeeId(),
						companyId);

		if (findByEmployeeOfficialInfoEmployeeId.isEmpty() && findByEmployeeOfficialInfoEmployeeId.get(0) == null) {
			throw new EmployeeNotFoundException("Employee not found");
		}
		EmployeePersonalInfo employeePersonalInfo = findByEmployeeOfficialInfoEmployeeId.get(0);
		itTicketsDTO.setEmployeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setHardwareType(companyTicketsdetails.getSubCategory());
		itTicketsDTO.setProductName(
				companyTicketsdetails.getProductName() == null ? "--" : companyTicketsdetails.getProductName());

		itTicketsDTO.setRaisedDate(companyTicketsdetails.getCreatedDate().toLocalDate());
		List<CompanyPcLaptopDetails> companyPcLaptopDetailsList = employeePersonalInfo.getCompanyPcLaptopDetailsList();
		if (companyPcLaptopDetailsList != null) {
			CompanyPcLaptopDetails companyPcLaptopDetails = new CompanyPcLaptopDetails();
			companyPcLaptopDetailsList.add(companyPcLaptopDetails);
			itTicketsDTO.setSerialNumber(companyPcLaptopDetails.getSerialNumber());
		}
		List<TicketHistroy> ticketHistroys = companyTicketsdetails.getTicketHistroys();
		List<EmployeePersonalInfo> employeeInfo = employeeInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {

			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {
					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());

				}
			}
		}

		if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			itTicketsDTO.setStatus(ticketHistroy.getStatus());
			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				itTicketsDTO.setIsAuthorized(true);
			} else {
				itTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}
		itTicketsDTO.setSerialNumber(companyTicketsdetails.getIdentificationNumber());
		itTicketsDTO.setItTicketId(companyTicketsdetails.getTicketId());
		itTicketsDTO.setQuestionAnswer(companyTicketsdetails.getQuestionAnswer());
		itTicketsDTO.setMonitoringDepartment(companyTicketsdetails.getMonitoringDepartment());
		return itTicketsDTO;
	}

	@Override
	public ITTicketsDTO getTicketsHardwareIssuesDetailsAndHistory(Long companyId, String id, Long employeeInfoId) {

		log.info("Get the hardware issues ticket details and history against comapnyId:: ", companyId);

		CompanyItTickets companyTicketsdetails = companyTicketsRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(
						() -> new ITTicketsDetailsNotFoundException("Details are not found against companyId and id"));

		ITTicketsDTO itTicketsDTO = new ITTicketsDTO();

		BeanUtils.copyProperties(companyTicketsdetails, itTicketsDTO);

		List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId = employeeInfoRepository
				.findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(companyTicketsdetails.getEmployeeId(),
						companyId);

		if (findByEmployeeOfficialInfoEmployeeId.isEmpty() && findByEmployeeOfficialInfoEmployeeId.get(0) == null) {
			throw new EmployeeNotFoundException("Employee not found");
		}
		EmployeePersonalInfo employeePersonalInfo = findByEmployeeOfficialInfoEmployeeId.get(0);
		itTicketsDTO.setEmployeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setHardwareType(companyTicketsdetails.getSubCategory());
		itTicketsDTO.setRaisedDate(companyTicketsdetails.getCreatedDate().toLocalDate());
		itTicketsDTO.setProductName(
				companyTicketsdetails.getProductName() == null ? "--" : companyTicketsdetails.getProductName());
		List<CompanyPcLaptopDetails> companyPcLaptopDetailsList = employeePersonalInfo.getCompanyPcLaptopDetailsList();
		if (companyPcLaptopDetailsList != null) {
			CompanyPcLaptopDetails companyPcLaptopDetails = new CompanyPcLaptopDetails();
			companyPcLaptopDetailsList.add(companyPcLaptopDetails);
			itTicketsDTO.setSerialNumber(companyPcLaptopDetails.getSerialNumber());
		}
		List<TicketHistroy> ticketHistroys = companyTicketsdetails.getTicketHistroys();
		List<EmployeePersonalInfo> employeeInfo = employeeInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));

		for (TicketHistroy ticketHistroy : ticketHistroys) {

			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());
				}
			}
		}

		if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			itTicketsDTO.setStatus(ticketHistroy.getStatus());
			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				itTicketsDTO.setIsAuthorized(true);
			} else {
				itTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}
		itTicketsDTO.setItTicketId(companyTicketsdetails.getTicketId());
		itTicketsDTO.setQuestionAnswer(companyTicketsdetails.getQuestionAnswer());
		itTicketsDTO.setMonitoringDepartment(companyTicketsdetails.getMonitoringDepartment());
		return itTicketsDTO;
	}

	@Override
	public ITTicketsDTO getTicketsEmailAndIdCardDetailsAndHistory(Long companyId, String id, Long employeeInfoId) {

		log.info("Get the email ticket details and history against comapnyId:: ", companyId);

		CompanyItTickets companyTicketsdetails = companyTicketsRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(
						() -> new ITTicketsDetailsNotFoundException("Details are not found against companyId and id"));

		ITTicketsDTO itTicketsDTO = new ITTicketsDTO();
		BeanUtils.copyProperties(companyTicketsdetails, itTicketsDTO);

		List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId = employeeInfoRepository
				.findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(companyTicketsdetails.getEmployeeId(),
						companyId);

		if (findByEmployeeOfficialInfoEmployeeId.isEmpty() && findByEmployeeOfficialInfoEmployeeId.get(0) == null) {
			throw new EmployeeNotFoundException("Employee not found");
		}
		EmployeePersonalInfo employeePersonalInfo = findByEmployeeOfficialInfoEmployeeId.get(0);
		itTicketsDTO.setEmployeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		itTicketsDTO.setHardwareType(companyTicketsdetails.getSubCategory());
		itTicketsDTO.setProductName(
				companyTicketsdetails.getProductName() == null ? "--" : companyTicketsdetails.getProductName());

		itTicketsDTO.setRaisedDate(companyTicketsdetails.getCreatedDate().toLocalDate());
		List<CompanyPcLaptopDetails> companyPcLaptopDetailsList = employeePersonalInfo.getCompanyPcLaptopDetailsList();
		if (companyPcLaptopDetailsList != null) {
			CompanyPcLaptopDetails companyPcLaptopDetails = new CompanyPcLaptopDetails();
			companyPcLaptopDetailsList.add(companyPcLaptopDetails);
			itTicketsDTO.setSerialNumber(companyPcLaptopDetails.getSerialNumber());
		}
		List<TicketHistroy> ticketHistroys = companyTicketsdetails.getTicketHistroys();
		List<EmployeePersonalInfo> employeeInfo = employeeInfoRepository
				.findAllById(ticketHistroys.stream().map(x -> x.getBy()).collect(Collectors.toList()));
		for (TicketHistroy ticketHistroy : ticketHistroys) {

			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {

					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());
				}
			}
		}
		if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			itTicketsDTO.setStatus(ticketHistroy.getStatus());
			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				itTicketsDTO.setIsAuthorized(true);
			} else {
				itTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}
		itTicketsDTO.setItTicketId(companyTicketsdetails.getTicketId());
		itTicketsDTO.setQuestionAnswer(companyTicketsdetails.getQuestionAnswer());
		itTicketsDTO.setMonitoringDepartment(companyTicketsdetails.getMonitoringDepartment());
		return itTicketsDTO;
	}

	@Override
	public CompanyTicketDto updateTicketHistory(UpdateITTicketDTO updateTicketDTO, Long employeeInfoId) {
		log.info("update the ticket history against employee Id:: ", employeeInfoId);

		Optional<CompanyItTickets> companyTickets = companyTicketsRepository
				.findById(updateTicketDTO.getTicketObjectId());
		if (companyTickets.isEmpty()) {
			throw new DataNotFoundException("Ticket Not Found");
		}
		CompanyItTickets companyItTickets = companyTickets.get();
		TicketHistroy ticketHistroy = new TicketHistroy();

		BeanUtils.copyProperties(updateTicketDTO, ticketHistroy);
		EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(employeeInfoId)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));

		ticketHistroy.setDepartment(employeeInfo.getEmployeeOfficialInfo() == null ? null
				: employeeInfo.getEmployeeOfficialInfo().getDepartment());
		ticketHistroy.setDate(LocalDate.now());
		ticketHistroy.setBy(employeeInfoId);
		companyItTickets.getTicketHistroys().add(ticketHistroy);
		CompanyTicketDto companyTicketDto = new CompanyTicketDto();
		BeanUtils.copyProperties(companyTicketsRepository.save(companyItTickets), companyTicketDto);
		if (updateTicketDTO.getStatus().equalsIgnoreCase("Delegated")) {
			switch (updateTicketDTO.getDepartment()) {
			case "ACCOUNTS": {
				companyAccountTicketsRepository
						.save(BeanCopy.objectProperties(companyItTickets, CompanyAccountTickets.class));
				break;
			}

			case "HR": {
				hrTicketsRepository.save(BeanCopy.objectProperties(companyItTickets, CompanyHrTickets.class));
				break;
			}

			case "ADMIN": {
				companyAdminDeptTicketsRepo
						.save(BeanCopy.objectProperties(companyItTickets, CompanyAdminDeptTickets.class));
				break;
			}

			default:
				throw new InavlidInputException("Department does not Exists");

			}
			companyTicketsRepository.deleteById(updateTicketDTO.getTicketObjectId());
		}

		return companyTicketDto;
	}

}
