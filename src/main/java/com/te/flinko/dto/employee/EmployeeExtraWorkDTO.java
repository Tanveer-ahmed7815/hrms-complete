package com.te.flinko.dto.employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Data;

@Data
public class EmployeeExtraWorkDTO {

	private Long extraWorkId;

	@JsonFormat(shape = Shape.STRING,pattern = "MM-dd-yyyy")
	private LocalDate date;

	@JsonFormat(shape = Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime loginTime;

	@JsonFormat(shape = Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime logoutTime;

	private Integer breakDuration;

	private Map<String, List<String>> projectTaskDetails;

	private String status;

}
