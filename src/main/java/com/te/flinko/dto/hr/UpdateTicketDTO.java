package com.te.flinko.dto.hr;

import lombok.Data;

@Data
public class UpdateTicketDTO {

	private String ticketObjectId;

	private String status;

	private String department;

	private String reason;

}
