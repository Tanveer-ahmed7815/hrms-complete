package com.te.flinko.entity.admin.mongo;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Document("fa_company_letter_format")
public class CompanyLetterFormat {
	
	@Id
	private String id;

	@Field("clf_letter_id")
	private Long letterId;

	@Field("clf_company_id")
	private Long companyId;

	@Field("clf_letter")
	private Map<String, String> letters;

}
