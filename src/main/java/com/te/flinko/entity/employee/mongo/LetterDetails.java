package com.te.flinko.entity.employee.mongo;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterDetails {
	
	@Field("ld_id")
	private Long id;

	@Field("ld_type")
	private String type;

	@Field("ld_url")
	private String url;

	@Field("ld_is_approved")
	private Boolean isApproved;
	
	@Field("ld_rejection_reason")
	private String rejectionReason;
	
	@Field("ld_issued_date")
	private LocalDate issuedDate;
	
	@Field("ld_issued_by")
	private Long issuedBy;

}
