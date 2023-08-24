package com.te.flinko.dto.reportingmanager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceApprovalDTO {
	
	private String objectId;
	
	private Integer detailsId;
	
	private LocalDate date;
	
	private String status;
	
	private String comment;
	
	private LocalDateTime punchIn;
	
	private LocalDateTime punchOut;

}
