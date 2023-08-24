package com.te.flinko.service.reportingmanager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.te.flinko.dto.reportingmanager.AttendanceApprovalDTO;
import com.te.flinko.dto.reportingmanager.EmployeeAttendanceDetailsDTO;
import com.te.flinko.entity.admin.CompanyBranchInfo;
import com.te.flinko.entity.employee.EmployeeOfficialInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.mongo.AttendanceDetails;
import com.te.flinko.entity.employee.mongo.EmployeeAttendanceDetails;
import com.te.flinko.exception.DataNotFoundException;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeReportingInfoRepository;
import com.te.flinko.repository.employee.mongo.EmployeeAttendanceDetailsRepository;

@Service
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

	@Autowired
	private EmployeeAttendanceDetailsRepository employeeAttendanceDetailsRepository;

	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepository;

	@Autowired
	private EmployeeReportingInfoRepository reportingInfoRepository;

	private static final String ATTENDANCE_NOT_FOUND = "Attendance Not Found";

	private static final String ATTENDANCE_DETAILS_NOT_FOUND = "Attendance Details Not Found";

	@Override
	public List<EmployeeAttendanceDetailsDTO> getAllAttendanceDetails(Long companyId, Long employeeInfoId) {
		List<Long> employeeInfoIList = reportingInfoRepository
				.findByReportingManagerEmployeeInfoIdAndReportingManagerCompanyInfoCompanyId(employeeInfoId, companyId)
				.stream().map(employee -> employee.getEmployeePersonalInfo().getEmployeeInfoId())
				.collect(Collectors.toList());
		List<EmployeeAttendanceDetailsDTO> employeeAttendanceDetailsDTOs = new ArrayList<>();
		List<EmployeeAttendanceDetails> employeeAttendanceDetailsList = employeeAttendanceDetailsRepository
				.findByCompanyIdAndEmployeeInfoIdInAndAttendanceDetailsIsInsideLocationAndAttendanceDetailsPunchOutIsNotNullAndAttendanceDetailsPunchInIsNotNull(
						companyId, employeeInfoIList, false);
		List<EmployeePersonalInfo> employeeList = employeePersonalInfoRepository
				.findAllById(employeeAttendanceDetailsList.stream().map(EmployeeAttendanceDetails::getEmployeeInfoId)
						.collect(Collectors.toList()));
		for (EmployeeAttendanceDetails employeeAttendanceDetails : employeeAttendanceDetailsList) {
			getAttendanceDetails(employeeAttendanceDetails, employeeList, employeeAttendanceDetailsDTOs);
		}

		return employeeAttendanceDetailsDTOs;
	}

	private void getAttendanceDetails(EmployeeAttendanceDetails employeeAttendanceDetails,
			List<EmployeePersonalInfo> employeeList, List<EmployeeAttendanceDetailsDTO> employeeAttendanceDetailsDTOs) {
		List<EmployeePersonalInfo> attendanceEmployeeList = employeeList.stream()
				.filter(employee -> employeeAttendanceDetails.getEmployeeInfoId().equals(employee.getEmployeeInfoId()))
				.collect(Collectors.toList());
		EmployeePersonalInfo employeePersonalInfo = attendanceEmployeeList.get(0);
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		employeeAttendanceDetails.getAttendanceDetails().stream()
				.filter(attendance -> attendance.getPunchOut() != null && attendance.getPunchIn() != null
						&& attendance.getIsInsideLocation().equals(Boolean.FALSE)
						&& (attendance.getStatus() == null || (!attendance.getStatus().containsKey("Present")
								&& !attendance.getStatus().containsKey("Absent"))))
				.forEach(attendance -> {
					if (!attendanceEmployeeList.isEmpty()) {
						employeeAttendanceDetailsDTOs.add(EmployeeAttendanceDetailsDTO.builder()
								.objectId(employeeAttendanceDetails.getAttendanceObjectId())
								.employeeName(
										employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
								.employeeInfoId(employeePersonalInfo.getEmployeeInfoId())
								.employeeId(employeeOfficialInfo != null ? employeeOfficialInfo.getEmployeeId() : null)
								.department(employeeOfficialInfo != null ? employeeOfficialInfo.getDepartment() : null)
								.designation(
										employeeOfficialInfo != null ? employeeOfficialInfo.getDesignation() : null)
								.detailsId(attendance.getDetailsId()).date(attendance.getPunchIn().toLocalDate())
								.build());
					}
				});
	}

	@Override
	public EmployeeAttendanceDetailsDTO getAttendanceDetailsById(AttendanceApprovalDTO attendanceApprovalDTO) {
		EmployeeAttendanceDetails employeeAttendanceDetails = employeeAttendanceDetailsRepository
				.findById(attendanceApprovalDTO.getObjectId())
				.orElseThrow(() -> new DataNotFoundException(ATTENDANCE_NOT_FOUND));
		EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepository
				.findById(employeeAttendanceDetails.getEmployeeInfoId())
				.orElseThrow(() -> new DataNotFoundException("Employee Not Found"));
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		CompanyBranchInfo companyBranchInfo = employeeOfficialInfo != null ? employeeOfficialInfo.getCompanyBranchInfo()
				: null;

		AttendanceDetails attendanceDetails = employeeAttendanceDetails.getAttendanceDetails().stream()
				.filter(attendance -> attendance.getDetailsId().equals(attendanceApprovalDTO.getDetailsId())).findAny()
				.orElseThrow(() -> new DataNotFoundException(ATTENDANCE_DETAILS_NOT_FOUND));
		return EmployeeAttendanceDetailsDTO.builder().objectId(employeeAttendanceDetails.getAttendanceObjectId())
				.detailsId(attendanceDetails.getDetailsId())
				.employeeName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
				.employeeInfoId(employeePersonalInfo.getEmployeeInfoId())
				.employeeId(employeeOfficialInfo != null ? employeeOfficialInfo.getEmployeeId() : null)
				.department(employeeOfficialInfo != null ? employeeOfficialInfo.getDepartment() : null)
				.designation(employeeOfficialInfo != null ? employeeOfficialInfo.getDesignation() : null)
				.branch(companyBranchInfo != null ? companyBranchInfo.getBranchName() : null)
				.loginTime(attendanceDetails.getPunchIn()).logoutTime(attendanceDetails.getPunchOut())
				.date(attendanceDetails.getPunchIn() != null ? attendanceDetails.getPunchIn().toLocalDate()
						: attendanceDetails.getPunchOut().toLocalDate())
				.build();

	}

	@Override
	@Transactional
	public AttendanceApprovalDTO updateStatus(AttendanceApprovalDTO attendanceApprovalDTO) {
		EmployeeAttendanceDetails employeeAttendanceDetails = employeeAttendanceDetailsRepository
				.findById(attendanceApprovalDTO.getObjectId())
				.orElseThrow(() -> new DataNotFoundException(ATTENDANCE_NOT_FOUND));
		List<AttendanceDetails> attendanceDetailsList = employeeAttendanceDetails.getAttendanceDetails();
		AttendanceDetails attendanceDetails = attendanceDetailsList.stream()
				.filter(attendance -> attendance.getPunchIn().toLocalDate().equals(attendanceApprovalDTO.getDate())
						&& attendance.getDetailsId().equals(attendanceApprovalDTO.getDetailsId()))
				.findAny().orElseThrow(() -> new DataNotFoundException(ATTENDANCE_DETAILS_NOT_FOUND));
		Map<String, String> status = new LinkedHashMap<>();
		status.put(attendanceApprovalDTO.getStatus(), attendanceApprovalDTO.getComment());
		attendanceDetails.setStatus(status);
		attendanceDetailsList.remove(attendanceApprovalDTO.getDetailsId().intValue() - 1);
		attendanceDetailsList.add(attendanceApprovalDTO.getDetailsId().intValue() - 1, attendanceDetails);
		employeeAttendanceDetails.setAttendanceDetails(attendanceDetailsList);
		employeeAttendanceDetailsRepository.save(employeeAttendanceDetails);
		return attendanceApprovalDTO;

	}

	@Override
	public List<EmployeeAttendanceDetailsDTO> getMissedPunchOut(Long companyId, Long employeeInfoId) {
		List<Long> employeeInfoIList = reportingInfoRepository
				.findByReportingManagerEmployeeInfoIdAndReportingManagerCompanyInfoCompanyId(employeeInfoId, companyId)
				.stream().map(employee -> employee.getEmployeePersonalInfo().getEmployeeInfoId())
				.collect(Collectors.toList());
		List<EmployeeAttendanceDetailsDTO> employeeAttendanceDetailsDTOs = new ArrayList<>();
		List<EmployeeAttendanceDetails> employeeAttendanceDetailsList = employeeAttendanceDetailsRepository
				.findByCompanyIdAndEmployeeInfoIdInAndAttendanceDetailsPunchOutIsNull(companyId, employeeInfoIList);
		List<EmployeePersonalInfo> employeeList = employeePersonalInfoRepository
				.findAllById(employeeAttendanceDetailsList.stream().map(EmployeeAttendanceDetails::getEmployeeInfoId)
						.collect(Collectors.toList()));
		for (EmployeeAttendanceDetails employeeAttendanceDetails : employeeAttendanceDetailsList) {
			getMissedPunchOutDetails(employeeAttendanceDetails, employeeList, employeeAttendanceDetailsDTOs);
		}

		return employeeAttendanceDetailsDTOs;
	}

	private void getMissedPunchOutDetails(EmployeeAttendanceDetails employeeAttendanceDetails,
			List<EmployeePersonalInfo> employeeList, List<EmployeeAttendanceDetailsDTO> employeeAttendanceDetailsDTOs) {
		List<EmployeePersonalInfo> attendanceEmployeeList = employeeList.stream()
				.filter(employee -> employeeAttendanceDetails.getEmployeeInfoId().equals(employee.getEmployeeInfoId()))
				.collect(Collectors.toList());
		EmployeePersonalInfo employeePersonalInfo = attendanceEmployeeList.get(0);
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		employeeAttendanceDetails.getAttendanceDetails().stream()
				.filter(attendance -> attendance.getPunchIn() != null && attendance.getPunchOut() == null
						&& LocalDate.now().isAfter(attendance.getPunchIn().toLocalDate().plusDays(1)))
				.forEach(attendance -> {
					if (!attendanceEmployeeList.isEmpty()) {
						employeeAttendanceDetailsDTOs.add(EmployeeAttendanceDetailsDTO.builder()
								.objectId(employeeAttendanceDetails.getAttendanceObjectId())
								.employeeName(
										employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
								.employeeInfoId(employeePersonalInfo.getEmployeeInfoId())
								.employeeId(employeeOfficialInfo != null ? employeeOfficialInfo.getEmployeeId() : null)
								.department(employeeOfficialInfo != null ? employeeOfficialInfo.getDepartment() : null)
								.designation(
										employeeOfficialInfo != null ? employeeOfficialInfo.getDesignation() : null)
								.detailsId(attendance.getDetailsId()).date(attendance.getPunchIn().toLocalDate())
								.build());
					}
				});
	}

	@Override
	public List<EmployeeAttendanceDetailsDTO> getMissedPunchIn(Long companyId, Long employeeInfoId) {
		List<Long> employeeInfoIList = reportingInfoRepository
				.findByReportingManagerEmployeeInfoIdAndReportingManagerCompanyInfoCompanyId(employeeInfoId, companyId)
				.stream().map(employee -> employee.getEmployeePersonalInfo().getEmployeeInfoId())
				.collect(Collectors.toList());
		List<EmployeeAttendanceDetailsDTO> employeeAttendanceDetailsDTOs = new ArrayList<>();
		List<EmployeeAttendanceDetails> employeeAttendanceDetailsList = employeeAttendanceDetailsRepository
				.findByCompanyIdAndEmployeeInfoIdInAndAttendanceDetailsPunchInIsNull(companyId, employeeInfoIList);
		List<EmployeePersonalInfo> employeeList = employeePersonalInfoRepository
				.findAllById(employeeAttendanceDetailsList.stream().map(EmployeeAttendanceDetails::getEmployeeInfoId)
						.collect(Collectors.toList()));
		for (EmployeeAttendanceDetails employeeAttendanceDetails : employeeAttendanceDetailsList) {
			getMissedPunchInDetails(employeeAttendanceDetails, employeeList, employeeAttendanceDetailsDTOs);
		}

		return employeeAttendanceDetailsDTOs;
	}

	private void getMissedPunchInDetails(EmployeeAttendanceDetails employeeAttendanceDetails,
			List<EmployeePersonalInfo> employeeList, List<EmployeeAttendanceDetailsDTO> employeeAttendanceDetailsDTOs) {
		List<EmployeePersonalInfo> attendanceEmployeeList = employeeList.stream()
				.filter(employee -> employeeAttendanceDetails.getEmployeeInfoId().equals(employee.getEmployeeInfoId()))
				.collect(Collectors.toList());
		EmployeePersonalInfo employeePersonalInfo = attendanceEmployeeList.get(0);
		EmployeeOfficialInfo employeeOfficialInfo = employeePersonalInfo.getEmployeeOfficialInfo();
		employeeAttendanceDetails.getAttendanceDetails().stream()
				.filter(attendance -> attendance.getPunchIn() == null && attendance.getPunchOut() != null)
				.forEach(attendance -> {
					if (!attendanceEmployeeList.isEmpty()) {
						employeeAttendanceDetailsDTOs.add(EmployeeAttendanceDetailsDTO.builder()
								.objectId(employeeAttendanceDetails.getAttendanceObjectId())
								.employeeName(
										employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName())
								.employeeInfoId(employeePersonalInfo.getEmployeeInfoId())
								.employeeId(employeeOfficialInfo != null ? employeeOfficialInfo.getEmployeeId() : null)
								.department(employeeOfficialInfo != null ? employeeOfficialInfo.getDepartment() : null)
								.designation(
										employeeOfficialInfo != null ? employeeOfficialInfo.getDesignation() : null)
								.detailsId(attendance.getDetailsId()).date(attendance.getPunchOut().toLocalDate())
								.build());
					}
				});
	}

	@Override
	@Transactional
	public AttendanceApprovalDTO updatePunchInOut(AttendanceApprovalDTO attendanceApprovalDTO) {
		EmployeeAttendanceDetails employeeAttendanceDetails = employeeAttendanceDetailsRepository
				.findById(attendanceApprovalDTO.getObjectId())
				.orElseThrow(() -> new DataNotFoundException(ATTENDANCE_NOT_FOUND));
		List<AttendanceDetails> attendanceDetailsList = employeeAttendanceDetails.getAttendanceDetails();
		AttendanceDetails attendanceDetails = attendanceDetailsList.stream()
				.filter(attendance -> attendance.getDetailsId().equals(attendanceApprovalDTO.getDetailsId()))
				.findAny().orElseThrow(() -> new DataNotFoundException(ATTENDANCE_DETAILS_NOT_FOUND));
		Map<String, String> status = new LinkedHashMap<>();
		status.put(attendanceApprovalDTO.getStatus(), attendanceApprovalDTO.getComment());
		attendanceDetails.setStatus(status);
		attendanceDetails.setPunchOut(attendanceDetails.getPunchOut() == null ? attendanceApprovalDTO.getPunchOut()
				: attendanceDetails.getPunchOut());
		attendanceDetails.setPunchIn(attendanceDetails.getPunchIn() == null ? attendanceApprovalDTO.getPunchIn()
				: attendanceDetails.getPunchIn());
		attendanceDetailsList.remove(attendanceApprovalDTO.getDetailsId().intValue() - 1);
		attendanceDetailsList.add(attendanceApprovalDTO.getDetailsId().intValue() - 1, attendanceDetails);
		employeeAttendanceDetails.setAttendanceDetails(attendanceDetailsList);
		employeeAttendanceDetailsRepository.save(employeeAttendanceDetails);
		return attendanceApprovalDTO;

	}

}
