package com.te.flinko.dto.reportingmanager;

import java.time.LocalDate;
import java.util.List;

import com.te.flinko.dto.employee.mongo.Timesheet;
import com.te.flinko.dto.employee.mongo.TimesheetDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeTimesheetDetailsApprovalDTO {

	private String id;
	private Long timesheetId;
	private String employeeId;
	private String fullName;	
	private String branch;	
	private String department;	
	private String designation;
	private LocalDate startDate;	
	private LocalDate endDate;
	private List<Timesheet> timesheets;	
	private List<TimesheetDTO> timesheetDTO;
	private Boolean isActionRequired;
	private Boolean isApproved;
	private String pendingAt;
	private String rejectedBy;
	private String rejectionReason;
	private String reportingManager;

}
