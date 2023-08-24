package com.te.flinko.dto.helpandsupport.mongo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@SuppressWarnings("serial")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
public class RaiseTicketDto implements Serializable {
	
	private String department;
	private List<CompanyTicketDto> companyTicketDto;
}
