package com.te.flinko.dto.account;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesInvoiceOrderDropdownDTO {

	private Long salesOrderId;

	private Long clientId;

	private String clientName;

	private String subject;

	private List<ClientContactPersonsDTO> contactPersons;
}
