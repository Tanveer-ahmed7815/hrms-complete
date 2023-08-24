package com.te.flinko.dto.hr.mongo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HRTicketsBasicDTO {
	
	private String ticketObjectId;
	
	private Long hrTicketId;

	private String category;
	
	private LocalDate raisedDate;
	
	private String ticketOwner;

	private String status;
}
