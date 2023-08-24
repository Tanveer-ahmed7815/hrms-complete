package com.te.flinko.service.reportingmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.te.flinko.dto.admin.AdminApprovedRejectDto;
import com.te.flinko.dto.reportingmanager.ResignationRequestApprovalDTO;
import com.te.flinko.entity.admin.LevelsOfApproval;
import com.te.flinko.entity.employee.CompanyEmployeeResignationDetails;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReportingInfo;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.employee.CompanyEmployeeResignationDetailsRepository;
import com.te.flinko.repository.employee.EmployeeReportingInfoRepository;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;
import com.te.flinko.service.notification.employee.PushNotificationService;

import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.APPROVED;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.PENDING;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.REJECTED;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.STATUS_IS_NOT_VALIED;
import static com.te.flinko.common.admin.EmployeeAdvanceSalaryConstants.RM;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class ReportingManagerResignationApprovalServiceImpl implements ReportingManagerResignationApprovalService {

	private final CompanyEmployeeResignationDetailsRepository resignationRepository;

	private final EmployeeReportingInfoRepository reportingRepository;
	
	private final InAppNotificationServiceImpl notificationServiceImpl;
	
	private final PushNotificationService pushNotificationService;
	
	private Optional<String> optional = Optional.of("optional");

	@Override
	public List<ResignationRequestApprovalDTO> getResignationRequestList(String status, Long employeeInfoId,
			Long companyId) {

		List<EmployeeReportingInfo> employeeslist = reportingRepository
				.findByReportingManagerEmployeeInfoIdAndReportingManagerCompanyInfoCompanyId(employeeInfoId, companyId);

		List<Long> employeeInfoIdList = (employeeslist.stream().map(EmployeeReportingInfo::getEmployeePersonalInfo)
				.collect(Collectors.toList())).stream().map(EmployeePersonalInfo::getEmployeeInfoId)
				.collect(Collectors.toList());

		List<CompanyEmployeeResignationDetails> filteredList = resignationRepository
				.findByStatusAndCompanyInfoCompanyIdAndEmployeePersonalInfoEmployeeInfoIdIn(status, companyId,
						employeeInfoIdList);

		List<ResignationRequestApprovalDTO> DTOList = new ArrayList<>();
		
				for (CompanyEmployeeResignationDetails resignationRequest : filteredList) {

					EmployeePersonalInfo employeePersonalInfo = resignationRequest.getEmployeePersonalInfo();

					if (employeePersonalInfo != null) {
						EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
						if (employeeOfficialInfo != null) {

							List<EmployeeReportingInfo> employeeInfoList = employeePersonalInfo.getEmployeeInfoList();
							EmployeePersonalInfo reportingManagerDetails = null;
							if(!employeeInfoList.isEmpty()) {
								reportingManagerDetails =employeeInfoList.get(employeeInfoList.size()-1).getReportingManager();
							}
							String reportingManager = (reportingManagerDetails == null) ? null
									: reportingManagerDetails.getFirstName() + " " + reportingManagerDetails.getLastName();
							
							ResignationRequestApprovalDTO DTO = new ResignationRequestApprovalDTO();
							BeanUtils.copyProperties(employeeOfficialInfo, DTO);
							DTO.setFullName(
									employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
							DTO.setMobileNumber(employeePersonalInfo.getMobileNumber());
							List<ProjectDetails> allocatedProjectList = employeePersonalInfo.getAllocatedProjectList();
							List<String> projectNameList = new ArrayList<>();
							for (ProjectDetails projectDetails : allocatedProjectList) {
								projectNameList.add(projectDetails.getProjectName());
							}
							DTO.setStatus(resignationRequest.getStatus());
							DTO.setEmployeeInfoId(resignationRequest.getEmployeePersonalInfo().getEmployeeInfoId());
							DTO.setAllocatedProjectList(projectNameList);
							DTO.setResignationReason(resignationRequest.getResignationReason());
							DTO.setResignationId(resignationRequest.getResignationId());
							DTO.setReportingManager(reportingManager);
							DTO.setPendingAt(RM);
							if(resignationRequest.getStatus().equalsIgnoreCase(APPROVED) || resignationRequest.getStatus().equalsIgnoreCase(REJECTED)) {
								DTO.setIsActionRequired(Boolean.FALSE);
							}else {
								DTO.setIsActionRequired(Boolean.TRUE);
							}
							
							
							DTOList.add(DTO);
						}
					}
				}

		return DTOList;
	}

	@Override
	public ResignationRequestApprovalDTO getResignationRequest(Long resignationId, Long companyId) {
		Optional<CompanyEmployeeResignationDetails> optionalResigantionDetails = resignationRepository
				.findByResignationIdAndCompanyInfoCompanyId(resignationId, companyId);
		CompanyEmployeeResignationDetails resignationDetails = optionalResigantionDetails.get();
		if (resignationDetails == null) {
			throw new DataNotFoundException("Resignation Request Not Found");
		}
		EmployeePersonalInfo employeePersonalInfo = resignationDetails.getEmployeePersonalInfo();
		if (employeePersonalInfo == null) {
			throw new DataNotFoundException("PersonalInfo Not Found");
		}
		if (employeePersonalInfo.getEmployeeOfficialInfo() == null) {
			throw new DataNotFoundException("OfficialInfo Not Found");
		}
		List<EmployeeReportingInfo> employeeInfoList = employeePersonalInfo.getEmployeeInfoList();
		EmployeePersonalInfo reportingManagerDetails = null;
		if(!employeeInfoList.isEmpty()) {
			reportingManagerDetails =employeeInfoList.get(employeeInfoList.size()-1).getReportingManager();
		}
		String reportingManager = (reportingManagerDetails == null) ? null
				: reportingManagerDetails.getFirstName() + " " + reportingManagerDetails.getLastName();
		
		ResignationRequestApprovalDTO DTO = new ResignationRequestApprovalDTO();
		BeanUtils.copyProperties(employeePersonalInfo.getEmployeeOfficialInfo(), DTO);
		DTO.setFullName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		DTO.setMobileNumber(employeePersonalInfo.getMobileNumber());
		List<ProjectDetails> allocatedProjectList = employeePersonalInfo.getAllocatedProjectList();
		List<String> projectNameList = new ArrayList<>();
		for (ProjectDetails projectDetails : allocatedProjectList) {
			projectNameList.add(projectDetails.getProjectName());
		}
		DTO.setStatus(resignationDetails.getStatus());
		DTO.setEmployeeInfoId(resignationDetails.getEmployeePersonalInfo().getEmployeeInfoId());
		DTO.setAllocatedProjectList(projectNameList);
		DTO.setResignationReason(resignationDetails.getResignationReason());
		DTO.setResignationId(resignationDetails.getResignationId());
		DTO.setReportingManager(reportingManager);
		DTO.setPendingAt(RM);
		if(resignationDetails.getStatus().equalsIgnoreCase(APPROVED) || resignationDetails.getStatus().equalsIgnoreCase(REJECTED)) {
			DTO.setIsActionRequired(Boolean.FALSE);
		}else {
			DTO.setIsActionRequired(Boolean.TRUE);
		}
		
		return DTO;
	}

	@Transactional
	@Override
	public String addResignationResponse(Long companyId, Long resignationId, String employeeId,
			AdminApprovedRejectDto adminApprovedRejectDto) {

		return resignationRepository.findByResignationIdAndCompanyInfoCompanyId(resignationId, companyId)
				.filter(resignationRequest -> resignationRequest.getStatus().equalsIgnoreCase(PENDING))
				.map(resignationRequest -> {

					return optional
							.filter(rejectStatus -> adminApprovedRejectDto.getStatus().equalsIgnoreCase(REJECTED))
							.map(response -> {
								resignationRequest.setStatus(adminApprovedRejectDto.getStatus());
								notificationServiceImpl.saveNotification("Resignation request is Rejected by Reporting Manager",
										resignationRequest.getEmployeePersonalInfo().getEmployeeInfoId());
							
								if (resignationRequest.getEmployeePersonalInfo().getExpoToken() != null) {
									pushNotificationService.pushMessage("Flinko", "Resignation request is Rejected by Reporting Manager",
											resignationRequest.getEmployeePersonalInfo().getExpoToken());
									}
								return "Resignation rejected successfully";
							})
							.orElseGet(() -> optional.filter(
									selectStatus -> adminApprovedRejectDto.getStatus().equalsIgnoreCase(APPROVED))
									.map(response -> {
										resignationRequest.setStatus(adminApprovedRejectDto.getStatus());
										notificationServiceImpl.saveNotification("Resignation request is Approved by Reporting Manager",
												resignationRequest.getEmployeePersonalInfo().getEmployeeInfoId());
										
										if (resignationRequest.getEmployeePersonalInfo().getExpoToken() != null) {
											pushNotificationService.pushMessage("Flinko", "Resignation request is Approved by Reporting Manager",
													resignationRequest.getEmployeePersonalInfo().getExpoToken());
											}
										
										return "Resignation approved successfully";
									}).orElseGet(() -> STATUS_IS_NOT_VALIED));

				}).orElseThrow(() -> new DataNotFoundException("Data Not Found or Request Got Resposed Already"));

	}

}
