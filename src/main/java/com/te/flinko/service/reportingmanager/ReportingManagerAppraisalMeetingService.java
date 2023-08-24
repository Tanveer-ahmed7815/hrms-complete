package com.te.flinko.service.reportingmanager;

import java.util.List;

import com.te.flinko.dto.employee.EmployeeReviseSalaryDTO;
import com.te.flinko.dto.reportingmanager.AppraisalMeetingFeedbackDTO;
import com.te.flinko.dto.reportingmanager.AppraisalMeetingListDto;
import com.te.flinko.dto.reportingmanager.EmployeeDetailsDTO;

public interface ReportingManagerAppraisalMeetingService {

	List<AppraisalMeetingListDto> teamAppraisalMeeting(Long userId, String date);

	EmployeeDetailsDTO employeeDetail(Long meetingId, Long userId);

	EmployeeReviseSalaryDTO appraisalMeetingFeedback(AppraisalMeetingFeedbackDTO feedbackDTO);

}
