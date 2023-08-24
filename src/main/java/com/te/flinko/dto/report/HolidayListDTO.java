package com.te.flinko.dto.report;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayListDTO implements Serializable{
	private String label;
	private String addressDetails;
	private String employeeId;
	private String companyName;
	private String companyUrl;
	private Long pincode;
	private String cin;
	private String dayOfWeek;
	private String date;
	private String holidayType;
	private String state;
	private Long holidayId;
	private String holidayName;
	private LocalDate holidayDate;
	private Boolean isOptional;
}
