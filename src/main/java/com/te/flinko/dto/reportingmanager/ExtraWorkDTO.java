package com.te.flinko.dto.reportingmanager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.te.flinko.dto.employee.mongo.EmployeeProjectTaskDetailsDTO;

import lombok.Data;

@Data
public class ExtraWorkDTO {

	private Long extraWorkId;

	private LocalDate date;

	private String employeeId;

	private String fullName;

	private String designation;

	private String department;

	private String branch;

	private LocalTime loginTime;

	private LocalTime logoutTime;

	private Integer breakDuration;

	private List<EmployeeProjectTaskDetailsDTO> projectTaskDetails;

}
