package com.te.flinko.entity.helpandsupport.mongo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.te.flinko.audit.AuditMetadata;
import com.te.flinko.dto.helpandsupport.mongo.Conversation;

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
@Document("fa_company_chat_details")
public class CompanyChatDetails implements Serializable{
	
	@Id
	private String chatObjectId;
	
	@Field("chd_chat_id")
	private Long chatId;
	
	@Field("chd_company_id")
	private Long companyId;
	
	@Field("chd_room")
	private String room;
	
	@Field("chd_employee_one")
	private String employeeOne;
	
	@Field("chd_employee_two")
	private String employeeTwo;
	
	@Field("chd_conversation")
    private List<Conversation> conversations;
	
	@Field("chd_created_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss", timezone = "Asia/Kolkata")
	private LocalDateTime createdDate=LocalDateTime.now();
}
