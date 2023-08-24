package com.te.flinko.dto.helpandsupport.mongo;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(value = Include.NON_DEFAULT)
public class CompanyTicketDto implements Serializable {
	private String id;
	private Long ticketId;
	private Long accountTicketId;
	private String category;
	private String subCategory;
	private String description;
	private String employeeId;
	private String department;
	private String attachmentsUrl;
	private String reportingManagerId;
	private List<TicketHistroy> ticketHistroys;
	private String feedback;
	private Integer rating;
	private Long companyId;
	private String objectTicketId;
	private Long adminTicketId;
	private String ticketObjectId;
	private Long hrTicketId;
	private Long createdBy;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
	private LocalDateTime createdDate;
	private Long itTicketId;
	private Long productId;
	private String productName;
	private String identificationNumber;
	private String ticketOwner;
	private String serialNumber;
	private LocalDate raisedDate;
	private String status;
	private Boolean isAuthorized;
	private String hardwareType;
	private String monitoringDepartment;
	private Boolean flag;
	private Map<String, String> questionAnswer;
}
