package com.te.flinko.entity.helpandsupport.mongo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Convert;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.te.flinko.audit.Audit;
import com.te.flinko.dto.helpandsupport.mongo.TicketHistroy;
import com.te.flinko.util.JsonToStringConverter;
import com.te.flinko.util.MapToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("fa_company_account_tickets")
public class CompanyAccountTickets extends Audit implements Serializable {

	@Id
	private String objectTicketId;

	@Field("cat_account_ticket_id")
	private Long ticketId;

	@Field("cat_category")
	private String category;

	@Field("cat_sub_category")
	private String subCategory;

	@Field("cat_description")
	private String description;

	@Field("cat_employee_id")
	private String employeeId;

	@Field("cat_attachments_url")
	private String attachmentsUrl;

	@Field("cat_reporting_manager_id")
	private String reportingManagerId;

	@Field("cat_identification_number")
	private String identificationNumber;

	@Field("cat_histroy")
	private List<TicketHistroy> ticketHistroys;

	@Field("cat_feedback")
	private String feedback;

	@Field("cat_rating")
	private Integer rating;

	@Field("cat_company_id")
	private Long companyId;

	@Field("cat_monitoring_department")
	private String monitoringDepartment;

	@Convert(converter = MapToStringConverter.class)
	@Field("cat_question_answer")
	private Map<String, String> questionAnswer;
}
