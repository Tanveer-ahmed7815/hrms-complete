package com.te.flinko.service.account.mongo;

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
import com.te.flinko.dto.account.mongo.CompanyAccountTicketsDTO;
import com.te.flinko.dto.account.mongo.UpdateAccountTicketDTO;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAccountTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyAdminDeptTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyHrTickets;
import com.te.flinko.entity.helpandsupport.mongo.CompanyItTickets;
import com.te.flinko.exception.InavlidInputException;
import com.te.flinko.exception.employee.DataNotFoundException;
import com.te.flinko.exception.employee.EmployeeNotFoundException;
import com.te.flinko.repository.admindept.CompanyItTicketsRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAccountTicketsRepository;
import com.te.flinko.repository.helpandsupport.mongo.CompanyAdminDeptTicketsRepo;
import com.te.flinko.repository.helpandsupport.mongo.CompanyHrTicketsRepository;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.service.notification.employee.PushNotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountTicketsServiceImpl implements AccountTicketsService {

	@Autowired
	CompanyAccountTicketsRepository accountTicketsRepository;

	@Autowired
	EmployeePersonalInfoRepository employeeInfoRepository;

	@Autowired
	CompanyHrTicketsRepository hrTicketsRepository;

	@Autowired
	CompanyItTicketsRepository companyTicketsRepository;

	@Autowired
	CompanyAdminDeptTicketsRepo companyAdminDeptTicketsRepo;

	@Autowired
	InAppNotificationServiceImpl notificationServiceImpl;

	@Autowired
	PushNotificationService pushNotificationService;

	private static final String SALES_PURCHASE = "salesPurchase";

	private static final String EMPLOYEE = "employee";

	private static final String OTHERS = "others";

	@Override
	public List<CompanyAccountTicketsDTO> getAccountSaleAndPurchaseTicketList(Long companyId, Long employeeInfoId) {
		log.info("Get the list of account sale and purchase tickets against companyId ::", companyId);
		List<CompanyAccountTickets> companyAccountTickets = accountTicketsRepository.findByCompanyId(companyId);
		if (companyAccountTickets.isEmpty()) {
			return Collections.emptyList();
		}

		List<CompanyAccountTicketsDTO> listOfTicketDto = new ArrayList<>();

		 List<CompanyAccountTicketsDTO> collect = companyAccountTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(SALES_PURCHASE)).map(x -> {
					CompanyAccountTicketsDTO companyAccountTicketsDTO = new CompanyAccountTicketsDTO();
					BeanUtils.copyProperties(x, companyAccountTicketsDTO);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);
					companyAccountTicketsDTO.setTicketId(x.getTicketId());
					companyAccountTicketsDTO
							.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyAccountTicketsDTO.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyAccountTicketsDTO.setType(x.getSubCategory());
					companyAccountTicketsDTO.setMonitoringDepartment(x.getMonitoringDepartment());
					companyAccountTicketsDTO.setRaisedBy(x.getCreatedBy().toString());

//					if (x.getQuestionAnswer().containsValue(null)) {
//						companyAccountTicketsDTO.setFlag(true);
////						need to add questionAnswer field in the companyAccountTicketsDTO to set the value/ to display in the list of data.. 
////						x.getQuestionAnswer();
//					}
					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();
					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyAccountTicketsDTO.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyAccountTicketsDTO.setIsAuthorized(true);
						} else {
							companyAccountTicketsDTO
									.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}

					}
					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyAccountTicketsDTO.setFlag(true);
					}
					return companyAccountTicketsDTO;
				}).collect(Collectors.toList());

		 List<CompanyAccountTicketsDTO> collect2 = collect.stream().filter(x->x.getFlag()!=null && x.getFlag()).collect(Collectors.toList());
		 List<CompanyAccountTicketsDTO> collect3 = collect.stream().filter(x-> x.getFlag()==null || !x.getFlag()).collect(Collectors.toList());
		 
		 listOfTicketDto.addAll(collect2);	
		 listOfTicketDto.addAll(collect3);
		 return listOfTicketDto;
	}

	@Override
	public CompanyAccountTicketsDTO getAccountSaleAndPurchaseDetailsAndHistory(Long companyId, String objectTicketId,
			Long employeeInfoId) {
		log.info("Get the details and history of account sale and purchase tickets against companyId ::" + companyId
				+ "and object ticket id:: " + objectTicketId);
		CompanyAccountTickets accountTickets = accountTicketsRepository
				.findByCompanyIdAndObjectTicketId(companyId, objectTicketId)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyAccountTicketsDTO companyAccountTicketsDTO = new CompanyAccountTicketsDTO();
		BeanUtils.copyProperties(accountTickets, companyAccountTicketsDTO);

		List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId = employeeInfoRepository
				.findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(accountTickets.getEmployeeId(), companyId);

		if (findByEmployeeOfficialInfoEmployeeId.isEmpty() && findByEmployeeOfficialInfoEmployeeId.get(0) == null) {
			throw new EmployeeNotFoundException("Employee not found");
		}
		EmployeePersonalInfo employeePersonalInfo = findByEmployeeOfficialInfoEmployeeId.get(0);
		companyAccountTicketsDTO
				.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		companyAccountTicketsDTO.setRaisedDate(accountTickets.getCreatedDate().toLocalDate());
		companyAccountTicketsDTO.setType(accountTickets.getSubCategory());
		companyAccountTicketsDTO.setRaisedBy(accountTickets.getCreatedBy().toString());
		companyAccountTicketsDTO.setQuestionAnswer(accountTickets.getQuestionAnswer());
		companyAccountTicketsDTO.setMonitoringDepartment(accountTickets.getMonitoringDepartment());
		
		List<TicketHistroy> ticketHistroys = accountTickets.getTicketHistroys();

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
		if (!ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			companyAccountTicketsDTO.setStatus(ticketHistroy.getStatus());

			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				companyAccountTicketsDTO.setIsAuthorized(true);
			} else {
				companyAccountTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}

		return companyAccountTicketsDTO;
	}

	@Override
	public List<CompanyAccountTicketsDTO> getEmployeeTicketList(Long companyId, Long employeeInfoId) {
		log.info("Get the list of account employee tickets against companyId ::", companyId);
		List<CompanyAccountTickets> companyAccountTickets = accountTicketsRepository.findByCompanyId(companyId);
		if (companyAccountTickets.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<CompanyAccountTicketsDTO> listOfTicketDto=new ArrayList<>();
		 List<CompanyAccountTicketsDTO> collect = companyAccountTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(EMPLOYEE)).map(x -> {
					CompanyAccountTicketsDTO companyAccountTicketsDTO = new CompanyAccountTicketsDTO();
					BeanUtils.copyProperties(x, companyAccountTicketsDTO);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);
					companyAccountTicketsDTO.setTicketId(x.getTicketId());
					companyAccountTicketsDTO
							.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyAccountTicketsDTO.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyAccountTicketsDTO.setType(x.getSubCategory());
					companyAccountTicketsDTO.setRaisedBy(x.getCreatedBy().toString());
					companyAccountTicketsDTO.setMonitoringDepartment(x.getMonitoringDepartment());

					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();
					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyAccountTicketsDTO.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyAccountTicketsDTO.setIsAuthorized(true);
						} else {
							companyAccountTicketsDTO
									.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}

					}
					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyAccountTicketsDTO.setFlag(true);
					}
					return companyAccountTicketsDTO;
				}).collect(Collectors.toList());

		 List<CompanyAccountTicketsDTO> collect2 = collect.stream().filter(x->x.getFlag()!=null && x.getFlag()).collect(Collectors.toList());
		 List<CompanyAccountTicketsDTO> collect3 = collect.stream().filter(x-> x.getFlag()==null || !x.getFlag()).collect(Collectors.toList());
		 
		 listOfTicketDto.addAll(collect2);	
		 listOfTicketDto.addAll(collect3);
		 return listOfTicketDto;
	}

	@Override
	public CompanyAccountTicketsDTO getAccountEmployeeDetailsAndHistory(Long companyId, String objectTicketId,
			Long employeeInfoId) {

		log.info("Get the details and history of account employee tickets against companyId ::" + companyId
				+ "and object ticket id:: " + objectTicketId);

		CompanyAccountTickets accountTickets = accountTicketsRepository
				.findByCompanyIdAndObjectTicketId(companyId, objectTicketId)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyAccountTicketsDTO companyAccountTicketsDTO = new CompanyAccountTicketsDTO();
		BeanUtils.copyProperties(accountTickets, companyAccountTicketsDTO);

		EmployeePersonalInfo employeePersonalInfo = employeeInfoRepository
				.findById(accountTickets.getCreatedBy()).orElseThrow(()-> new DataNotFoundException("Ticket Owner Not Found"));

		companyAccountTicketsDTO
				.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		companyAccountTicketsDTO.setRaisedDate(accountTickets.getCreatedDate().toLocalDate());
		companyAccountTicketsDTO.setType(accountTickets.getSubCategory());
		companyAccountTicketsDTO.setRaisedBy(accountTickets.getCreatedBy().toString());
		companyAccountTicketsDTO.setQuestionAnswer(accountTickets.getQuestionAnswer());
		companyAccountTicketsDTO.setMonitoringDepartment(accountTickets.getMonitoringDepartment());
		
		List<TicketHistroy> ticketHistroys = accountTickets.getTicketHistroys();

		List<EmployeePersonalInfo> employeeInfo = employeeInfoRepository
				.findAllById(ticketHistroys.stream().map(TicketHistroy::getBy).collect(Collectors.toList()));

		for (TicketHistroy ticketHistroy : ticketHistroys) {
			for (EmployeePersonalInfo employeePersonalInfo2 : employeeInfo) {
				if (employeePersonalInfo2.getEmployeeInfoId().equals(ticketHistroy.getBy())) {
					ticketHistroy.setEmployeeId(employeePersonalInfo2.getEmployeeOfficialInfo().getEmployeeId());
					ticketHistroy.setEmployeeName(
							employeePersonalInfo2.getFirstName() + " " + employeePersonalInfo2.getLastName());
				}
			}
		}
		if (!ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			companyAccountTicketsDTO.setStatus(ticketHistroy.getStatus());

			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				companyAccountTicketsDTO.setIsAuthorized(true);
			} else {
				companyAccountTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}

		return companyAccountTicketsDTO;
	}

	@Override
	public List<CompanyAccountTicketsDTO> getOthersTicketList(Long companyId, Long employeeInfoId) {
		log.info("Get the list of account others tickets against companyId ::", companyId);
		List<CompanyAccountTickets> companyAccountTickets = accountTicketsRepository.findByCompanyId(companyId);
		if (companyAccountTickets.isEmpty()) {
			return Collections.emptyList();
		}
		List<CompanyAccountTicketsDTO> listOfTicketDto=new ArrayList<>();
		
		 List<CompanyAccountTicketsDTO> collect = companyAccountTickets.stream()
				.filter(i -> i.getCreatedBy() != null && i.getCategory().equalsIgnoreCase(OTHERS)).map(x -> {
					CompanyAccountTicketsDTO companyAccountTicketsDTO = new CompanyAccountTicketsDTO();
					BeanUtils.copyProperties(x, companyAccountTicketsDTO);
					EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(x.getCreatedBy())
							.orElseGet(EmployeePersonalInfo::new);
					companyAccountTicketsDTO.setTicketId(x.getTicketId());
					companyAccountTicketsDTO
							.setTicketOwner(employeeInfo.getFirstName() + " " + employeeInfo.getLastName());
					companyAccountTicketsDTO.setRaisedDate(x.getCreatedDate().toLocalDate());
					companyAccountTicketsDTO.setType(x.getSubCategory());
					companyAccountTicketsDTO.setRaisedBy(x.getCreatedBy().toString());
					companyAccountTicketsDTO.setMonitoringDepartment(x.getMonitoringDepartment());
					
//					if (x.getQuestionAnswer().containsValue(null)) {
//					companyAccountTicketsDTO.setFlag(true);
////					need to add questionAnswer field in the companyAccountTicketsDTO to set the value/ to display in the list of data.. 
////					x.getQuestionAnswer();
//				}
					List<TicketHistroy> ticketHistroys = x.getTicketHistroys();
					if (ticketHistroys != null && !ticketHistroys.isEmpty()) {
						TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
						companyAccountTicketsDTO.setStatus(ticketHistroy.getStatus());
						if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
								|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
							companyAccountTicketsDTO.setIsAuthorized(true);
						} else {
							companyAccountTicketsDTO
									.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
						}

					}
					if (x.getQuestionAnswer() != null && x.getQuestionAnswer().containsValue("")) {
						companyAccountTicketsDTO.setFlag(true);
					}
					return companyAccountTicketsDTO;
				}).collect(Collectors.toList());

		 List<CompanyAccountTicketsDTO> collect2 = collect.stream().filter(x->x.getFlag()!=null && x.getFlag()).collect(Collectors.toList());
		 List<CompanyAccountTicketsDTO> collect3 = collect.stream().filter(x-> x.getFlag()==null || !x.getFlag()).collect(Collectors.toList());
		 
		 listOfTicketDto.addAll(collect2);	
		 listOfTicketDto.addAll(collect3);
		 return listOfTicketDto;
	}

	@Override
	public CompanyAccountTicketsDTO getAccountOthersDetailsAndHistory(Long companyId, String objectTicketId,
			Long employeeInfoId) {

		log.info("Get the details and history of account employee tickets against companyId ::" + companyId
				+ "and object ticket id:: " + objectTicketId);

		CompanyAccountTickets accountTickets = accountTicketsRepository
				.findByCompanyIdAndObjectTicketId(companyId, objectTicketId)
				.orElseThrow(() -> new DataNotFoundException("Account tickets details are not found"));

		CompanyAccountTicketsDTO companyAccountTicketsDTO = new CompanyAccountTicketsDTO();
		BeanUtils.copyProperties(accountTickets, companyAccountTicketsDTO);

		List<EmployeePersonalInfo> findByEmployeeOfficialInfoEmployeeId = employeeInfoRepository
				.findByEmployeeOfficialInfoEmployeeIdAndCompanyInfoCompanyId(accountTickets.getEmployeeId(), companyId);

		if (findByEmployeeOfficialInfoEmployeeId.isEmpty() && findByEmployeeOfficialInfoEmployeeId.get(0) == null) {
			throw new EmployeeNotFoundException("Employee not found");
		}
		EmployeePersonalInfo employeePersonalInfo = findByEmployeeOfficialInfoEmployeeId.get(0);
		companyAccountTicketsDTO
				.setTicketOwner(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		companyAccountTicketsDTO.setRaisedDate(accountTickets.getCreatedDate().toLocalDate());
		companyAccountTicketsDTO.setType(accountTickets.getSubCategory());
		companyAccountTicketsDTO.setRaisedBy(accountTickets.getCreatedBy().toString());
		companyAccountTicketsDTO.setQuestionAnswer(accountTickets.getQuestionAnswer());
		companyAccountTicketsDTO.setMonitoringDepartment(accountTickets.getMonitoringDepartment());
		
		List<TicketHistroy> ticketHistroys = accountTickets.getTicketHistroys();

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
		if (!ticketHistroys.isEmpty()) {
			TicketHistroy ticketHistroy = ticketHistroys.get(ticketHistroys.size() - 1);
			companyAccountTicketsDTO.setStatus(ticketHistroy.getStatus());

			if (ticketHistroy.getStatus().equalsIgnoreCase("Created")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Delegated")
					|| ticketHistroy.getStatus().equalsIgnoreCase("Released")) {
				companyAccountTicketsDTO.setIsAuthorized(true);
			} else {
				companyAccountTicketsDTO.setIsAuthorized(Objects.equals(employeeInfoId, ticketHistroy.getBy()));
			}
		}

		return companyAccountTicketsDTO;
	}

	@Override
	public CompanyAccountTicketsDTO updateAccountTicketHistory(UpdateAccountTicketDTO updateTicketDTO,
			Long employeeInfoId) {
		log.info("update the ticket history against employee Id:: ", employeeInfoId);

		Optional<CompanyAccountTickets> accountTickets = accountTicketsRepository
				.findById(updateTicketDTO.getTicketObjectId());

		if (accountTickets.isEmpty()) {
			throw new DataNotFoundException("Ticket Not Found");
		}
		CompanyAccountTickets companyAccountTickets = accountTickets.get();
		TicketHistroy ticketHistroy = new TicketHistroy();

		BeanUtils.copyProperties(updateTicketDTO, ticketHistroy);
		EmployeePersonalInfo employeeInfo = employeeInfoRepository.findById(employeeInfoId)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));

		ticketHistroy.setDepartment(employeeInfo.getEmployeeOfficialInfo() == null ? "--"
				: employeeInfo.getEmployeeOfficialInfo().getDepartment());
		ticketHistroy.setDate(LocalDate.now());
		ticketHistroy.setBy(employeeInfoId);
		companyAccountTickets.getTicketHistroys().add(ticketHistroy);
		CompanyAccountTicketsDTO companyTicketDto = new CompanyAccountTicketsDTO();
		BeanUtils.copyProperties(accountTicketsRepository.save(companyAccountTickets), companyTicketDto);

		if (updateTicketDTO.getStatus().equalsIgnoreCase("Delegated")) {
			switch (updateTicketDTO.getDepartment()) {
			case "IT": {
				companyTicketsRepository.save(BeanCopy.objectProperties(companyAccountTickets, CompanyItTickets.class));
				break;
			}

			case "HR": {
				hrTicketsRepository.save(BeanCopy.objectProperties(companyAccountTickets, CompanyHrTickets.class));
				break;
			}

			case "ADMIN": {
				companyAdminDeptTicketsRepo
						.save(BeanCopy.objectProperties(companyAccountTickets, CompanyAdminDeptTickets.class));
				break;
			}

			default:
				throw new InavlidInputException("Department does not Exists");

			}
			accountTicketsRepository.deleteById(updateTicketDTO.getTicketObjectId());
		}

		if (updateTicketDTO.getStatus().equalsIgnoreCase("Resolved")) {
			notificationServiceImpl.saveNotification("Ticket: " + companyAccountTickets.getTicketId() + " is Resolved",
					employeeInfoId);

			if (employeeInfo.getExpoToken() != null) {
				pushNotificationService.pushMessage("Flinko",
						"Ticket: " + companyAccountTickets.getTicketId() + " is Resolved", employeeInfo.getExpoToken());
			}
		}

		return companyTicketDto;
	}

}
