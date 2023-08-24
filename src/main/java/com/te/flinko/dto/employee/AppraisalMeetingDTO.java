package com.te.flinko.dto.employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class AppraisalMeetingDTO {

	private Long meetingId;
	private String meetingType;
	private LocalDate meetingDate;
	private LocalTime startTime;
	private Integer duration;
	private String meetingDetails;
	private String status;
	private String apprisalMonth;
	private List<EmployeeDropdownDTO> organizers;

}
