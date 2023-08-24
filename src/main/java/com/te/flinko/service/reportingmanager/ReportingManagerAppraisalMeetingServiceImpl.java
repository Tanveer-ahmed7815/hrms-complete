package com.te.flinko.service.reportingmanager;

import static com.te.flinko.common.employee.EmployeeLoginConstants.EMPLYOEE_DOES_NOT_EXIST;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.te.flinko.dto.employee.EmployeeReviseSalaryDTO;
import com.te.flinko.dto.reportingmanager.AppraisalMeetingFeedbackDTO;
import com.te.flinko.dto.reportingmanager.AppraisalMeetingListDto;
import com.te.flinko.dto.reportingmanager.EmployeeDetailsDTO;
import com.te.flinko.dto.reportingmanager.MeetingDetailsDTO;
import com.te.flinko.entity.employee.ApprisalMeetingInfo;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReviseSalary;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.exception.admin.EmployeeNotFoundException;
import com.te.flinko.exception.hr.CustomExceptionForHr;
import com.te.flinko.repository.employee.ApprisalMeetingInfoRepository;
import com.te.flinko.repository.employee.EmployeePersonalInfoRepository;
import com.te.flinko.repository.employee.EmployeeReviseSalaryRepository;
import com.te.flinko.service.notification.employee.InAppNotificationServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportingManagerAppraisalMeetingServiceImpl implements ReportingManagerAppraisalMeetingService {
	@Autowired
	private ApprisalMeetingInfoRepository apprisalMeetingInfoRepo;
	@Autowired
	private EmployeeReviseSalaryRepository employeeReviseSalaryRepo;
	@Autowired
	private EmployeePersonalInfoRepository employeePersonalInfoRepo;

	@Override
	public List<AppraisalMeetingListDto> teamAppraisalMeeting(Long userId, String date) {
		List<AppraisalMeetingListDto> apprisal = new ArrayList<>();
		EmployeePersonalInfo employeePersonalInfo = employeePersonalInfoRepo.findById(userId)
				.orElseThrow(() -> new EmployeeNotFoundException(EMPLYOEE_DOES_NOT_EXIST));

		List<ApprisalMeetingInfo> apprisalMeetingInfoList = employeePersonalInfo.getApprisalMeetingInfoList();
		if (apprisalMeetingInfoList == null || apprisalMeetingInfoList.isEmpty()) {
			return apprisal;
		}
		if (date.equalsIgnoreCase("today")) {
			apprisal.addAll(apprisalMeetingInfoList.stream()
					.filter(appraisalMeeting -> appraisalMeeting.getMeetingDate().equals(LocalDate.now()))
					.map(this::addData).collect(Collectors.toList()));
		} else if (date.equalsIgnoreCase("previous")) {
			apprisal.addAll(apprisalMeetingInfoList.stream()
					.filter(appraisalMeeting -> appraisalMeeting.getMeetingDate().isBefore(LocalDate.now()))
					.map(this::addData).collect(Collectors.toList()));
		} else if (date.equalsIgnoreCase("upcoming")) {
			apprisal.addAll(apprisalMeetingInfoList.stream()
					.filter(appraisalMeeting -> appraisalMeeting.getMeetingDate().isAfter(LocalDate.now()))
					.map(this::addData).collect(Collectors.toList()));
		}
		log.info("Appraisal details fetched successfully");
		return apprisal;
	}

	private AppraisalMeetingListDto addData(ApprisalMeetingInfo appraisalMeeting) {
		boolean isFeedback;
		LocalDate meetingDate = null;
		if (appraisalMeeting.getEmployeeReviseSalary().getReason() == (null)
				&& appraisalMeeting.getEmployeeReviseSalary().getAmount() == (null)) {
			isFeedback = false;

		} else {
			isFeedback = true;
		}
		if (appraisalMeeting.getMeetingDate().isAfter(LocalDate.now())
				|| appraisalMeeting.getMeetingDate().isBefore(LocalDate.now())) {
			meetingDate = appraisalMeeting.getMeetingDate();
		}
		return new AppraisalMeetingListDto(
				appraisalMeeting.getEmployeeReviseSalary().getEmployeePersonalInfo().getEmployeeOfficialInfo()
						.getEmployeeId(),
				appraisalMeeting.getEmployeeReviseSalary().getEmployeePersonalInfo().getEmployeeOfficialInfo()
						.getOfficialEmailId(),
				appraisalMeeting.getEmployeeReviseSalary().getEmployeePersonalInfo().getFirstName() + " "
						+ appraisalMeeting.getEmployeeReviseSalary().getEmployeePersonalInfo().getLastName(),
				appraisalMeeting.getEmployeeReviseSalary().getEmployeePersonalInfo().getEmployeeOfficialInfo()
						.getDesignation(),
				appraisalMeeting.getMeetingType(), appraisalMeeting.getMeetingId(), isFeedback, meetingDate,appraisalMeeting.getMeetingDetails());

	}

	@Override
	public EmployeeDetailsDTO employeeDetail(Long meetingId, Long userId) {
		ApprisalMeetingInfo meetingInfo = apprisalMeetingInfoRepo.findById(meetingId)
				.orElseThrow(() -> new CustomExceptionForHr("Apprisal meeting details not found"));
		EmployeePersonalInfo employeePersonalInfo = meetingInfo.getEmployeeReviseSalary().getEmployeePersonalInfo();
		EmployeeDetailsDTO employeeDetailsDTO = new EmployeeDetailsDTO();
		BeanUtils.copyProperties(meetingInfo, employeeDetailsDTO);
		employeeDetailsDTO.setEmployeeInfoId(employeePersonalInfo.getEmployeeInfoId());
		employeeDetailsDTO.setDepartment(employeePersonalInfo.getEmployeeOfficialInfo().getDepartment());
		employeeDetailsDTO.setDesignation(employeePersonalInfo.getEmployeeOfficialInfo().getDesignation());
		employeeDetailsDTO.setEmployeeId(employeePersonalInfo.getEmployeeOfficialInfo().getEmployeeId());
		employeeDetailsDTO.setFullName(employeePersonalInfo.getFirstName() + " " + employeePersonalInfo.getLastName());
		ArrayList<String> arrayList = new ArrayList<>();
		employeeDetailsDTO.setOfficialEmailId(employeePersonalInfo.getEmployeeOfficialInfo().getOfficialEmailId());
		employeeDetailsDTO.setMobileNumber(employeePersonalInfo.getMobileNumber());
		List<ProjectDetails> allocatedProjectList = employeePersonalInfo.getAllocatedProjectList();
		for (ProjectDetails projectDetails : allocatedProjectList) {
			arrayList.add(projectDetails.getProjectName());
		}
		if (meetingInfo.getEmployeeReviseSalary().getAmount() == null
				&& meetingInfo.getEmployeeReviseSalary().getReason() == null) {
			employeeDetailsDTO.setIsFeedbackGiven(false);
		}
		if ((meetingInfo.getEmployeeReviseSalary().getAmount() != null)
				&& (meetingInfo.getEmployeeReviseSalary().getReason() != null)) {
			employeeDetailsDTO.setIsFeedbackGiven(true);
		}

		employeeDetailsDTO.setProjectList(arrayList);
		employeeDetailsDTO.setDuration(meetingInfo.getDuration());
		employeeDetailsDTO.setReason(meetingInfo.getEmployeeReviseSalary().getReason());
		employeeDetailsDTO.setMeetingId(meetingInfo.getMeetingId());
		List<EmployeePersonalInfo> employeePersonalInfoList = meetingInfo.getEmployeePersonalInfoList();
		ArrayList<MeetingDetailsDTO> meetingList = new ArrayList<>();
		for (EmployeePersonalInfo projectDetails : employeePersonalInfoList) {
			MeetingDetailsDTO meetingDetailsDTO = new MeetingDetailsDTO();
			meetingDetailsDTO.setEmployeeId(projectDetails.getEmployeeOfficialInfo().getEmployeeId());
			meetingDetailsDTO.setEmployeeFullName(projectDetails.getFirstName() + " " + projectDetails.getLastName());
			meetingDetailsDTO.setEmployeeInfoId(projectDetails.getEmployeeInfoId());
			meetingList.add(meetingDetailsDTO);
		}
		employeeDetailsDTO.setEmployeeDetails(meetingList);
		log.info("Appraisal details fetched successfully");
		return employeeDetailsDTO;
	}

	@Transactional
	@Override
	public EmployeeReviseSalaryDTO appraisalMeetingFeedback(AppraisalMeetingFeedbackDTO feedbackDTO) {
		EmployeeReviseSalaryDTO employeeReviseSalaryDTO = new EmployeeReviseSalaryDTO();
		ApprisalMeetingInfo meetingInfo = apprisalMeetingInfoRepo.findById(feedbackDTO.getMeetingId())
				.orElseThrow(() -> new CustomExceptionForHr("Apprisal meeting details not found"));
		if ((meetingInfo.getEmployeeReviseSalary().getReason() != null)
				&& (meetingInfo.getEmployeeReviseSalary().getAmount() != null)) {
			log.info("Appraisal meeting Feedback already given");
			throw new CustomExceptionForHr("Appraisal meeting Feedback already given");
		}
		if ((meetingInfo.getMeetingDate().isBefore(LocalDate.now()))
				|| (meetingInfo.getMeetingDate().isEqual(LocalDate.now()))
						&& (!meetingInfo.getStartTime().isAfter(LocalTime.now()))) {
			EmployeeReviseSalary employeeReviseSalary = meetingInfo.getEmployeeReviseSalary();
			employeeReviseSalary.setReason(feedbackDTO.getReason());
			employeeReviseSalary.setAmount(feedbackDTO.getRevisedSalary());
			EmployeeReviseSalary reviseSalaryInfo = employeeReviseSalaryRepo.save(employeeReviseSalary);

			BeanUtils.copyProperties(reviseSalaryInfo, employeeReviseSalaryDTO);
		} else {
			log.info("Upcomming appraisal feedback is not possible");
			throw new CustomExceptionForHr("Appraisal meeting is not yet completed");
		}
		log.info("Appraisal meeting feedback details updated successfully");
		
		return employeeReviseSalaryDTO;
	}
}
