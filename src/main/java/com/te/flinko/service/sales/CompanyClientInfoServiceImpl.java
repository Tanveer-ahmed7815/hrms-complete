package com.te.flinko.service.sales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.sales.AllCompanyClientInfoResponseDTO;
import com.te.flinko.dto.sales.ClientContactPersonDetailsDTO;
import com.te.flinko.dto.sales.ClientProjectDTO;
import com.te.flinko.dto.sales.CompanyClientAddressDTO;
import com.te.flinko.dto.sales.CompanyClientInfoDTO;
import com.te.flinko.dto.sales.CompanyClientInfoResponseDTO;
import com.te.flinko.dto.sales.CompanyClientInfoUpdateDTO;
import com.te.flinko.dto.sales.LeadCategoryResponseDTO;
import com.te.flinko.dto.sales.ProjectDetailsDTO;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.admin.CompanyLeadCategories;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.sales.ClientContactPersonDetails;
import com.te.flinko.entity.sales.CompanyClientAddress;
import com.te.flinko.entity.sales.CompanyClientInfo;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.admin.NoCompanyPresentException;
import com.te.flinko.exception.sales.CompanyNamePresentException;
import com.te.flinko.exception.sales.DealAssociatedWithProjectException;
import com.te.flinko.exception.sales.EmailAlreadyPresentException;
import com.te.flinko.exception.sales.LeadInProgressException;
import com.te.flinko.exception.sales.MobileNumberAlreadyPresentException;
import com.te.flinko.exception.sales.NoClientPresentException;
import com.te.flinko.exception.sales.NoDealPresentException;
import com.te.flinko.exception.sales.NoLeadCategoryPresentException;
import com.te.flinko.exception.sales.NoLeadPresentException;
import com.te.flinko.repository.admin.CompanyInfoRepository;
import com.te.flinko.repository.admin.CompanyLeadCategoriesRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.project.ProjectDetailsRepository;
import com.te.flinko.repository.sales.ClientContactPersonDetailsRepository;
import com.te.flinko.repository.sales.CompanyClientInfoRepository;
import com.te.flinko.util.S3UploadFile;

@Valid
@Service
public class CompanyClientInfoServiceImpl implements CompanyClientInfoService {

	@Autowired
	CompanyClientInfoRepository companyClientInfoRepository;

	@Autowired
	CompanyInfoRepository companyInfoRepository;

	@Autowired
	CompanyLeadCategoriesRepository companyLeadCategoriesRepository;

	@Autowired
	ClientContactPersonDetailsRepository clientContactPersonDetailsRepository;

	@Autowired
	ProjectDetailsRepository projectDetailsRepository;

	@Autowired
	EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	S3UploadFile uploadFile;

	private static final String CONVERTED_TO_DEAL = "Converted to Deal";
	
	@Override
	public Boolean addCompanyClientInfo(CompanyClientInfoDTO companyClientInfoDTO, Long companyId,
			MultipartFile companyLogo) {

		CompanyInfo companyInfo = companyInfoRepository.findById(companyId)
				.orElseThrow(() -> new NoCompanyPresentException("No Company Present With This Id"));

		List<CompanyClientInfo> companyClientInfo = companyClientInfoRepository.findByCompanyInfoCompanyId(companyId);

		List<String> companyNameList = new ArrayList<>();

		companyClientInfo.forEach(x -> companyNameList.add(x.getClientName().toUpperCase())

		);

		if (companyNameList.contains(companyClientInfoDTO.getClientName().toUpperCase())) {
			throw new CompanyNamePresentException("company name already present");
		}

		CompanyLeadCategories companyLeadCategories = companyLeadCategoriesRepository
				.findByCompanyInfoCompanyIdAndLeadCategoryId(companyId, companyClientInfoDTO.getLeadCategoryId())
				.orElseThrow(() -> new NoLeadCategoryPresentException("no lead catagories present"));

		CompanyClientInfo companyClientInfo2 = BeanCopy.objectProperties(companyClientInfoDTO, CompanyClientInfo.class);

		List<ClientContactPersonDetails> listOfContactDetails = Lists.newArrayList();

		companyClientInfoDTO.getClientContactPersonDetailsList().forEach(i -> {

			ClientContactPersonDetails clientContactPersonDetails = new ClientContactPersonDetails();

			BeanUtils.copyProperties(i, clientContactPersonDetails);

			clientContactPersonDetails.setCompanyClientInfo(companyClientInfo2);

			listOfContactDetails.add(clientContactPersonDetails);
		});

		List<CompanyClientAddress> companyClientAddressList3 = companyClientInfo2.getCompanyClientAddressList();

		companyClientAddressList3.forEach(companyClientAddress ->

		companyClientAddress.setCompanyClientInfo(companyClientInfo2));

		companyClientInfo2.setClientContactPersonDetailsList(listOfContactDetails);

		companyClientInfo2.setCompanyInfo(companyInfo);

		if (companyLogo != null && !companyLogo.isEmpty()) {

			String uploadFile2 = uploadFile.uploadFile(companyLogo);

			companyClientInfo2.setLogoURL(uploadFile2);
		}

		companyClientInfo2.setCompanyLeadCategories(companyLeadCategories);
		return Optional.ofNullable(companyClientInfoRepository.save(companyClientInfo2)).isPresent();

	}

	@Override
	public List<LeadCategoryResponseDTO> getLeadCategory(Long companyId) {
		List<CompanyLeadCategories> companyLeadCategoriesList = companyLeadCategoriesRepository
				.findByCompanyInfoCompanyId(companyId)
				.orElseThrow(() -> new NoLeadCategoryPresentException("no lead categories present"));

		List<LeadCategoryResponseDTO> leadCategoryResponseDTOList = new ArrayList<>();

		companyLeadCategoriesList.forEach(x -> {

			LeadCategoryResponseDTO leadCategoryResponseDTO = new LeadCategoryResponseDTO();
			leadCategoryResponseDTO.setLeadCategoryId(x.getLeadCategoryId());
			leadCategoryResponseDTO.setLeadCategoryName(x.getLeadCategoryName());
			leadCategoryResponseDTO.setColor(x.getColor());
			leadCategoryResponseDTOList.add(leadCategoryResponseDTO);
		});
		return leadCategoryResponseDTOList;
	}

	@Override
	public List<AllCompanyClientInfoResponseDTO> getAllLeads(Long companyId, String clientType) {

		List<CompanyClientInfo> companyClientInfoList1 = null;
		if (clientType.equalsIgnoreCase("deal")) {

			
			companyClientInfoList1 = companyClientInfoRepository
					.findByCompanyInfoCompanyIdAndCompanyLeadCategoriesLeadCategoryName(companyId, CONVERTED_TO_DEAL)
					.filter(x -> !x.isEmpty()).orElseGet(ArrayList::new);
		} else {
			companyClientInfoList1 = companyClientInfoRepository
					.findByCompanyInfoCompanyIdAndCompanyLeadCategoriesLeadCategoryNameNot(companyId,CONVERTED_TO_DEAL)
					.filter(x -> !x.isEmpty()).orElseGet(ArrayList::new);
		}

		List<AllCompanyClientInfoResponseDTO> allCompanyClientInfoResponseDTOList = new ArrayList<>();
		companyClientInfoList1.forEach(companyClientInfo -> {

			String name = " ";

			Long createdBy = companyClientInfo.getCreatedBy();

			Optional<EmployeePersonalInfo> findById = employeePersonalInfoRepository.findById(createdBy);
			if (findById.isPresent()) {
				EmployeePersonalInfo employeePersonalInfo = findById.get();

				name = employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName();
			}

			AllCompanyClientInfoResponseDTO allCompanyClientInfoResponseDTO = new AllCompanyClientInfoResponseDTO();

			allCompanyClientInfoResponseDTO.setClientId(companyClientInfo.getClientId());
			allCompanyClientInfoResponseDTO.setOwnerName(name);
			allCompanyClientInfoResponseDTO.setColor(companyClientInfo.getCompanyLeadCategories().getColor());
			allCompanyClientInfoResponseDTO.setClientName(companyClientInfo.getClientName());

			allCompanyClientInfoResponseDTO.setEmailId(companyClientInfo.getEmailId());
			allCompanyClientInfoResponseDTO
					.setLeadCategoryId(companyClientInfo.getCompanyLeadCategories().getLeadCategoryId());
			allCompanyClientInfoResponseDTO
					.setLeadStatus(companyClientInfo.getCompanyLeadCategories().getLeadCategoryName());
			allCompanyClientInfoResponseDTO.setWebsiteUrl(companyClientInfo.getWebsiteUrl());

			allCompanyClientInfoResponseDTOList.add(allCompanyClientInfoResponseDTO);
		});

		return allCompanyClientInfoResponseDTOList;
	}

	@Override
	public CompanyClientInfoResponseDTO getCompanyClientinfoById(Long companyId, Long clientId) {

		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(clientId, companyId).orElseThrow();

		Long createdBy = companyClientInfo.getCreatedBy();

		CompanyClientInfoResponseDTO companyClientInfoResponseDTO = new CompanyClientInfoResponseDTO();

		employeePersonalInfoRepository.findById(createdBy).ifPresent(i -> 
			companyClientInfoResponseDTO.setLeadOwnerName(i.getFirstName() + " " + i.getLastName())
		);

		BeanUtils.copyProperties(companyClientInfo, companyClientInfoResponseDTO);

		companyClientInfoResponseDTO.setLeadStatus(companyClientInfo.getCompanyLeadCategories().getLeadCategoryName());

		companyClientInfoResponseDTO
				.setLeadCategoryId(companyClientInfo.getCompanyLeadCategories().getLeadCategoryId());

		List<CompanyClientAddress> companyClientAddressList = companyClientInfo.getCompanyClientAddressList();

		List<CompanyClientAddressDTO> companyClientAddressDTOList = new ArrayList<>();
		companyClientAddressList.forEach(y -> {
			CompanyClientAddressDTO companyClientAddressDTO = new CompanyClientAddressDTO();
			BeanUtils.copyProperties(y, companyClientAddressDTO);
			companyClientAddressDTOList.add(companyClientAddressDTO);
		});

		List<ClientContactPersonDetailsDTO> listOfContactDetails = Lists.newArrayList();
		companyClientInfo.getClientContactPersonDetailsList().forEach(i -> {
			ClientContactPersonDetailsDTO clientContactPersonDetailsDTO = new ClientContactPersonDetailsDTO();
			BeanUtils.copyProperties(i, clientContactPersonDetailsDTO);
			listOfContactDetails.add(clientContactPersonDetailsDTO);
		});
		
		List<ProjectDetails> projectDetailsList = companyClientInfo.getProjectDetailsList();
		
		List<ClientProjectDTO> clientProjectDTOList = projectDetailsList.stream().map(project -> { 
			String status = null;
			if(project.getProjectEstimationDetails()==null) {
				status = "Not Estimated";
			}else if(project.getProjectEstimationDetails().getStatus().equalsIgnoreCase("In-Progress")) {
				status = "Estimation is in Progress";
			}else if(project.getProjectEstimationDetails().getStatus().equalsIgnoreCase("Rejected")) {
				status = "Estimation Rejected";
			}else if(project.getProjectEstimationDetails().getEndDate().isBefore(LocalDate.now()) || project.getProjectEstimationDetails().getEndDate().isEqual(LocalDate.now())){
				status = "Completed";
			}else {
				status = "On Going";
			}
			return new ClientProjectDTO(project.getProjectName(), status);
		}).collect(Collectors.toList());

		companyClientInfoResponseDTO.setClientProjectList(clientProjectDTOList);
		companyClientInfoResponseDTO.setClientContactPersonDetailsList(listOfContactDetails);
		companyClientInfoResponseDTO.setCompanyClientAddressList(companyClientAddressDTOList);
		companyClientInfoResponseDTO.setAttachments(companyClientInfo.getAttachments());

		return companyClientInfoResponseDTO;
	}

	@Override
	public Boolean addClientContactPersonDetails(Long companyId, Long clientId,
			ClientContactPersonDetailsDTO clientContactPersonDetailsDTO) {

		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(clientId, companyId)
				.orElseThrow(() -> new NoClientPresentException("no client present with this id "));

		List<ClientContactPersonDetails> clientContactPersonDetailsList = companyClientInfo
				.getClientContactPersonDetailsList();

		List<String> emailIdList = new ArrayList<>();
		List<String> mobileNumberList = new ArrayList<>();
		clientContactPersonDetailsList.forEach(clientContactPersonDetails -> {
			emailIdList.add(clientContactPersonDetails.getEmailId());

			mobileNumberList.add(clientContactPersonDetails.getMobileNumber());
		});

		if (emailIdList.contains(clientContactPersonDetailsDTO.getEmailId())) {
			throw new EmailAlreadyPresentException("emailId already exist");
		}
		if (mobileNumberList.contains(clientContactPersonDetailsDTO.getMobileNumber())) {
			throw new MobileNumberAlreadyPresentException("mobile number already exist");
		}
		ClientContactPersonDetails clientContactPersonDetails = new ClientContactPersonDetails();
		BeanUtils.copyProperties(clientContactPersonDetailsDTO, clientContactPersonDetails);

		clientContactPersonDetails.setCompanyClientInfo(companyClientInfo);

		return Optional.ofNullable(clientContactPersonDetailsRepository.save(clientContactPersonDetails)).isPresent();
	}

	@Override
	public Boolean updateCompanyClientInfo(CompanyClientInfoUpdateDTO companyClientInfoDTO, Long companyId,
			MultipartFile companyLogo) {

		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(companyClientInfoDTO.getClientId(), companyId).orElseThrow();

		BeanUtils.copyProperties(companyClientInfoDTO, companyClientInfo);

		if (companyLogo != null && !companyLogo.isEmpty()) {

			String uploadFile2 = uploadFile.uploadFile(companyLogo);
			if (companyClientInfo.getLogoURL() != null) {
				uploadFile.deleteS3Folder(companyClientInfo.getLogoURL());
			}
			companyClientInfo.setLogoURL(uploadFile2);
		}

		CompanyLeadCategories companyLeadCategories = companyLeadCategoriesRepository
				.findByCompanyInfoCompanyIdAndLeadCategoryId(companyId, companyClientInfoDTO.getLeadCategoryId())
				.orElseThrow(() -> new NoLeadCategoryPresentException("no lead catagories present"));

		companyClientInfo.setCompanyLeadCategories(companyLeadCategories);
		Set<Long> listOfIdsFromDto = companyClientInfoDTO.getClientContactPersonDetailsList().stream()
				.map(ClientContactPersonDetailsDTO::getContactPersonId).collect(Collectors.toSet());

		Set<Long> listOfIdsFromDb = companyClientInfo.getClientContactPersonDetailsList().stream()
				.map(ClientContactPersonDetails::getContactPersonId).collect(Collectors.toSet());

		listOfIdsFromDb.remove(listOfIdsFromDto);

		clientContactPersonDetailsRepository.deleteAllById(listOfIdsFromDb);

		List<ClientContactPersonDetails> listOfContactDetailsToUpdate = Lists.newArrayList();

		companyClientInfoDTO.getClientContactPersonDetailsList().forEach(i -> {
			ClientContactPersonDetails clientContactPersonDetails = new ClientContactPersonDetails();
			BeanUtils.copyProperties(i, clientContactPersonDetails);
			clientContactPersonDetails.setContactPersonId(i.getContactPersonId());
			clientContactPersonDetails.setCompanyClientInfo(companyClientInfo);
			listOfContactDetailsToUpdate.add(clientContactPersonDetails);
		});

		companyClientInfo.setClientContactPersonDetailsList(listOfContactDetailsToUpdate);

		List<CompanyClientAddress> companyClientAddressList = companyClientInfo.getCompanyClientAddressList();
		Map<Long, List<CompanyClientAddress>> companyClientAddressMap = companyClientAddressList.stream()
				.collect(Collectors.groupingBy(CompanyClientAddress::getClientAddressId));

		companyClientInfoDTO.getCompanyClientAddressList().forEach(companyClientAddressDto -> {
			if (companyClientAddressDto.getClientAddressId() == null) {

				CompanyClientAddress companyClientAddress = new CompanyClientAddress();
				BeanUtils.copyProperties(companyClientAddressDto, companyClientAddress);
				companyClientAddress.setCompanyClientInfo(companyClientInfo);
				companyClientAddressList.add(companyClientAddress);
			} else {

				Long clientAddressId = companyClientAddressDto.getClientAddressId();

				CompanyClientAddress companyClientAddress = companyClientAddressMap.get(clientAddressId).get(0);

				BeanUtils.copyProperties(companyClientAddressDto, companyClientAddress);
			}

		});

		return Optional.ofNullable(companyClientInfoRepository.save(companyClientInfo)).isPresent();

	}

	@Override
	public Boolean addProject(ProjectDetailsDTO projectDetailsDTO, Long companyId, Long clientId) {

		List<CompanyClientInfo> companyClientInfo = companyClientInfoRepository
				.findByCompanyInfoCompanyIdAndCompanyLeadCategoriesLeadCategoryName(companyId, CONVERTED_TO_DEAL)
				.orElseThrow(() -> new NoClientPresentException("no client present"));

		List<CompanyClientInfo> collect = companyClientInfo.stream().filter(x -> Objects.equals(x.getClientId(), clientId))
				.collect(Collectors.toList());
		if (collect.isEmpty()) {
			throw new NoClientPresentException("not a client");
		}

		Map<Long, List<CompanyClientInfo>> collect2 = companyClientInfo.stream()
				.collect(Collectors.groupingBy(CompanyClientInfo::getClientId));

		ProjectDetails projectDetails1 = new ProjectDetails();
		BeanUtils.copyProperties(projectDetailsDTO, projectDetails1);
		CompanyInfo companyInfo = companyInfoRepository.findById(companyId).orElse(null);
		projectDetails1.setCompanyInfo(companyInfo);
		projectDetails1.setCompanyClientInfo(collect2.get(clientId).get(0));

		return Optional.ofNullable(projectDetailsRepository.save(projectDetails1)).isPresent();

	}

	@Override
	public Boolean deleteLead(Long clientId, Long companyId) {

		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(clientId, companyId)
				.orElseThrow(() -> new NoLeadPresentException("no lead present"));
		String leadCategoryName = companyClientInfo.getCompanyLeadCategories().getLeadCategoryName();
		if (leadCategoryName.equalsIgnoreCase("in-progress")) {
			throw new LeadInProgressException("lead in progress, can not be deleted");
		} else {
			companyClientInfoRepository.deleteById(clientId);
			return true;
		}

	}

	@Override
	public Boolean deleteDeal(Long clientId, Long companyId) {

		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(clientId, companyId)
				.orElseThrow(() -> new NoDealPresentException("no deal present"));

		List<ProjectDetails> projectDetailsList = companyClientInfo.getProjectDetailsList();
		if (!projectDetailsList.isEmpty()) {
			throw new DealAssociatedWithProjectException("Deal Associated with Project, Can Not be Deleted");
		} else {
			companyClientInfoRepository.deleteById(clientId);
			return true;
		}

	}

	@Override
	public Boolean updateLeadCategory(Long companyId, Long clientId, Long leadCategoryId) {

		CompanyClientInfo companyClientInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(clientId, companyId).orElseThrow();

		CompanyLeadCategories companyLeadCategories = companyLeadCategoriesRepository
				.findByCompanyInfoCompanyIdAndLeadCategoryId(companyId, leadCategoryId)
				.orElseThrow(() -> new NoLeadCategoryPresentException("no lead category present"));

		companyClientInfo.setCompanyLeadCategories(companyLeadCategories);

		return Optional.ofNullable(companyClientInfoRepository.save(companyClientInfo)).isPresent();
	}

	@Override
	@Transactional
	public Boolean addAttachments(Long companyId, Long clientId, MultipartFile multipartFile) {
		CompanyClientInfo companyInfo = companyClientInfoRepository
				.findByClientIdAndCompanyInfoCompanyId(clientId, companyId)
				.orElseThrow(() -> new DataNotFoundException("Company Info Details not Present"));
		String url = uploadFile.uploadFile(multipartFile);
		List<String> attachments = new ArrayList<>(companyInfo.getAttachments());
		attachments.add(url);
		companyInfo.setAttachments(attachments);
		return true;
	}

}