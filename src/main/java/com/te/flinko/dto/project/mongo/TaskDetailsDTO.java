package com.te.flinko.dto.project.mongo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDetailsDTO {

	private String id;
	private String mileStoneId;
	private String mileStoneName;
	private Long subMilestoneId;
	private String subMilestoneName;
	private String taskName;
	private String taskDescription;
	private Long createdBy;
	private String createdByName;
	@JsonFormat(timezone = "Asia/Kolkata")
	private LocalDateTime createdDate;
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Kolkata")
	private LocalDateTime startDate;
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Kolkata")
	private LocalDateTime endDate;
	private LocalDate assignedDate;
	private String status;
	private String remarks;

	private LocalDate completedDate;
	private Long companyId;
	private Long projectId;
	private String assignedEmployeeName;
	private String comment;
	

}
