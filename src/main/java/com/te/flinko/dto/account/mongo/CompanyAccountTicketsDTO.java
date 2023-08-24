package com.te.flinko.dto.account.mongo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@Data
@Builder
@Document("fa_contact_person")
public class CompanyAccountTicketsDTO implements Serializable {

	private String objectTicketId;
	private Long ticketId;
	private String category;
	private String description;
	private String employeeId;
	private String attachmentsUrl;
	private String reportingManagerId;
	private String identificationNumber;
	private List<TicketHistroy> ticketHistroys;
	private String feedback;
	private Integer rating;
	private Long companyId;
	private String ticketOwner;
	private LocalDate raisedDate;
	private String raisedBy;
	private String type;
	private String uniqueNumber;
	private String status;
	private Boolean isAuthorized;
	private String monitoringDepartment;
	private Boolean flag;
	private Map<String, String> questionAnswer;

}
