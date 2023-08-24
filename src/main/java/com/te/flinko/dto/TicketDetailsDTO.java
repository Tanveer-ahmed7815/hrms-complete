package com.te.flinko.dto;

import java.util.List;
import java.util.Map;

import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;

import lombok.Data;

@Data
public class TicketDetailsDTO {

	private String objectTicketId;
	private Long ticketId;
	private String category;
	private String description;
	private String employeeId;
	private String employeeName;
	private String attachmentsUrl;
	private String reportingManagerName;
	private List<TicketHistroy> ticketHistroys;
	private String feedback;
	private Integer rating;
	private String ticketOwner;
	private String type;
	private String uniqueNumber;
	private Map<String, String> questionAnswer;
	private String department;

}
