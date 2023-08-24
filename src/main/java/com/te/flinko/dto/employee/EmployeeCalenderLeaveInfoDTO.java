package com.te.flinko.dto.employee;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCalenderLeaveInfoDTO {

	private Long leaveAppliedId;

	private String leaveOfType;

	private String leaveType;

	private Double leaveDuration;

	private LocalDate startDate;

	private String reason;

	private String status;
}
