package com.te.flinko.dto.account;

import java.util.List;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.te.flinko.dto.account.mongo.ContactPerson;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SalesOrderDropdownDTO {
	private String vendorId;
	private String vendorName;
	private List<ContactPerson> contactPersons;
	private String subject;
	private Long purchaseOrderId;

}
