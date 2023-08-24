package com.te.flinko.dto.it.mongo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(value = Include.NON_DEFAULT)
public class ITTicketsDTO implements Serializable {
	private String id;
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
	private Long createdBy;
	private LocalDate createdDate;
	private Long itTicketId;
	private String employeeName;
	private String status;
	private String acceptedBy;
	private LocalDate acceptedDate;
	private String hardwareType;
	private String ticketOwner;
	private Long productId;
	private String productName;
	private String identificationNumber;
	private String serialNumber;
	private LocalDate raisedDate;
	private Boolean isAuthorized;
	private String monitoringDepartment;
	private Boolean flag;
	private Map<String, String> questionAnswer;
	

}
