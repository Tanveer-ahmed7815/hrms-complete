package com.te.flinko.service.reportingmanager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.reportingmanager.EmployeePersonalInfoDTO;
import com.te.flinko.dto.reportingmanager.EmployeeTaskDetailsDTO;
import com.te.flinko.dto.reportingmanager.ReportingmanagerMyTeamDTO;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReportingInfo;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.project.mongo.ProjectTaskDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.exception.InvalidInputException;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.ReportingManagerRepository;
import com.te.flinko.repository.project.mongo.ProjectTaskDetailsRepository;

@Service
public class ReportingManagerMyteamServiceImpl implements ReportingManagerMyteamService {

	@Autowired
	ReportingManagerRepository reportingManagerRepo;

	@Autowired
	EmployeePersonalInfoRepository personalInfoRepo;

	@Autowired
	ProjectTaskDetailsRepository taskRepository;

	@Override
	public List<ReportingmanagerMyTeamDTO> getEmployeeList(Long employeeInfoId, Long companyId) {

		List<ReportingmanagerMyTeamDTO> employeesListDTO = new ArrayList<>();
		List<EmployeeReportingInfo> employeeList = reportingManagerRepo
				.findByReportingManagerEmployeeInfoIdAndReportingManagerCompanyInfoCompanyId(employeeInfoId, companyId);
		if (employeeList.isEmpty()) {
			throw new DataNotFoundException("No one is Reporting");
		}
		for (EmployeeReportingInfo employeeReportingInfo : employeeList) {

			if (employeeReportingInfo.getEmployeePersonalInfo() != null) {

				if (employeeReportingInfo.getEmployeePersonalInfo().getEmployeeOfficialInfo() != null) {

					ReportingmanagerMyTeamDTO reportingmanagerMyTeamDTO = new ReportingmanagerMyTeamDTO();
					reportingmanagerMyTeamDTO.setEmployeeId(
							employeeReportingInfo.getEmployeePersonalInfo().getEmployeeOfficialInfo().getEmployeeId());
					reportingmanagerMyTeamDTO.setFullName(employeeReportingInfo.getEmployeePersonalInfo().getFirstName()
							+ " " + employeeReportingInfo.getEmployeePersonalInfo().getLastName());
					reportingmanagerMyTeamDTO.setDesignation(
							employeeReportingInfo.getEmployeePersonalInfo().getEmployeeOfficialInfo().getDesignation());
					reportingmanagerMyTeamDTO
							.setEmployeeInfoId(employeeReportingInfo.getEmployeePersonalInfo().getEmployeeInfoId());
					List<ProjectDetails> allocatedProjectList = employeeReportingInfo.getEmployeePersonalInfo()
							.getAllocatedProjectList();
					List<String> projectNameList = new ArrayList<>();
					for (ProjectDetails projectDetail : allocatedProjectList) {
						projectNameList.add(projectDetail.getProjectName());
					}
					reportingmanagerMyTeamDTO.setProjectName(projectNameList);
					reportingmanagerMyTeamDTO
							.setOfficialEmailId(employeeReportingInfo.getEmployeePersonalInfo().getEmailId());

					employeesListDTO.add(reportingmanagerMyTeamDTO);
				}
			}
		}
		return employeesListDTO;
	}

	@Override
	public EmployeePersonalInfoDTO getEmployeeInfo(Long reportingManagerId, Long employeeInfoId, Long companyId) {

		List<EmployeeReportingInfo> employeeReportingInfo = reportingManagerRepo
				.findByEmployeePersonalInfoEmployeeInfoId(employeeInfoId);

		EmployeePersonalInfoDTO employeePersonalInfoDTO = new EmployeePersonalInfoDTO();
		EmployeePersonalInfo employeePersonalInfo = personalInfoRepo
				.findByEmployeeInfoIdAndCompanyInfoCompanyId(employeeInfoId, companyId);


		if (!employeeReportingInfo.get(0).getReportingManager().getEmployeeInfoId().equals(reportingManagerId) ) {
			throw new DataNotFoundException("Employee Not Reporting to This Manager");
		}
		if (employeePersonalInfo==null) {
			throw new DataNotFoundException("PersonalInfo Not Found");
		}
		if (employeePersonalInfo.getEmployeeOfficialInfo() == null) {
			throw new DataNotFoundException("OfficialInfo No Found");
		}
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		employeePersonalInfoDTO.setEmployeeId(employeeOfficialInfo.getEmployeeId());
		employeePersonalInfoDTO
				.setFullName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		employeePersonalInfoDTO.setOfficialEmailId(employeeOfficialInfo.getOfficialEmailId());
		employeePersonalInfoDTO.setMobileNumber(employeePersonalInfo.getMobileNumber());
		employeePersonalInfoDTO.setBranchName(employeeOfficialInfo.getCompanyBranchInfo().getBranchName());
		employeePersonalInfoDTO.setDepartment(employeeOfficialInfo.getDepartment());
		employeePersonalInfoDTO.setDesignation(employeeOfficialInfo.getDesignation());
		List<ProjectDetails> allocatedProjectList = employeePersonalInfo.getAllocatedProjectList();
		List<String> projectList = new ArrayList<String>();
		for (ProjectDetails projectDetails : allocatedProjectList) {
			projectList.add(projectDetails.getProjectName());
		}
		employeePersonalInfoDTO.setProjectName(projectList);

		return employeePersonalInfoDTO;
	}

	@Override
	public List<EmployeeTaskDetailsDTO> getEmployeeTaskList(Long reportingManagerId, Long employeeInfoId,
			Long companyId, String status) {

		List<EmployeeReportingInfo> employeeReportingInfo = reportingManagerRepo
				.findByEmployeePersonalInfoEmployeeInfoId(employeeInfoId);
		
		if (!employeeReportingInfo.get(0).getReportingManager().getEmployeeInfoId().equals(reportingManagerId) ) {
			throw new DataNotFoundException("Employee Not Reporting to This Manager");
		}
		ArrayList<EmployeeTaskDetailsDTO> employeeTaskDTOList = new ArrayList<>();

		
		EmployeePersonalInfo employeePersonalInfos = personalInfoRepo
				.findByEmployeeInfoIdAndCompanyInfoCompanyId(employeeInfoId, companyId);
		if (employeePersonalInfos==null) {
			throw new DataNotFoundException("PersonalInfo Not Found");
		}
		if (employeePersonalInfos.getEmployeeOfficialInfo() == null) {
			throw new DataNotFoundException("OfficialInfo Not Found");
		}
		List<ProjectDetails> allocatedProjectList = employeePersonalInfos.getAllocatedProjectList();
		if (allocatedProjectList.isEmpty()) {
			throw new DataNotFoundException("Project Not Alloted");
		}
		for (ProjectDetails projectDetails : allocatedProjectList) {
			List<ProjectTaskDetails> projectTaskDetailsList = new ArrayList<>();
						
			if (status.equalsIgnoreCase("All")) {
				projectTaskDetailsList = taskRepository.findByProjectId(projectDetails.getProjectId());
			} else if (status.equalsIgnoreCase("In-progress") ) {
				projectTaskDetailsList = taskRepository.findByProjectIdAndStatus(projectDetails.getProjectId(), "in-progress");
			}else if (status.equalsIgnoreCase("Completed")){
				projectTaskDetailsList = taskRepository.findByProjectIdAndStatus(projectDetails.getProjectId(), "completed");

			}else {
				throw new InvalidInputException("Enter a valid status");
			}

			for (ProjectTaskDetails projectTaskDetails : projectTaskDetailsList) {
				
				EmployeeTaskDetailsDTO employeeTaskDetailsDTO = new EmployeeTaskDetailsDTO();
				
				if (projectTaskDetails.getAssignedEmployee() != null) {
					if ((projectTaskDetails.getAssignedEmployee())
							.equals((employeePersonalInfos.getEmployeeOfficialInfo().getEmployeeId()))) {

						BeanUtils.copyProperties(projectTaskDetails, employeeTaskDetailsDTO);
						employeeTaskDTOList.add(employeeTaskDetailsDTO);
					}
				}
			}
		}
		return employeeTaskDTOList;
	}
}
