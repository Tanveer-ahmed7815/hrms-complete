package com.te.flinko.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRequestDTO {

	private String type;

	private LocalDate startDate;

	private LocalDate endDate;

	private String filterValue;

	private Integer month;

	private Integer year;
	
	private String department;

}
