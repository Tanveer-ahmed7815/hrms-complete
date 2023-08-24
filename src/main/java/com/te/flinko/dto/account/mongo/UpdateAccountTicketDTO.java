package com.te.flinko.dto.account.mongo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
public class UpdateAccountTicketDTO implements Serializable {
	
	private String ticketObjectId;

	private String status;

	private String department;

	private String reason;
}
