package com.te.flinko.dto.account.mongo;

import java.io.Serializable;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.te.flinko.audit.Audit;

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
public class ContactPerson extends Audit implements Serializable {

	private String designation;

	@Id
	@Field("email_id")
	private String emailId;
//	 @Digits(integer = 10, fraction = 0, message ="must accept 10 digit only")
	@Range(max = 9999999999L,min = 999999999L,message ="must accept 10 digit only")

	@Field("mobile_number")
	private Long mobileNumber;

	@Field("contact_person_name")
	private String contactPersonName;
}