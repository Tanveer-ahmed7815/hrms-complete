package com.te.flinko.entity.employee.mongo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.te.flinko.dto.employee.mongo.Timesheet;

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
@Document("fa_employee_letter_details")
public class EmployeeLetterDetails {
	
	@Id
	private String letterObjectId;

	@Field("eld_document_id")
	private Long letterId;

	@Field("eld_employee_info_id")
	private Long employeeInfoId;

	@Field("eld_company_id")
	private Long companyId;
	
	@Field("ets_letters")
    private List<LetterDetails> letters;

}
