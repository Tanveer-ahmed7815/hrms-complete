package com.te.flinko.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
@Builder
public class DashboardResponseDTO {

	private List<DashboardDTO> cardValues;

	private List<DashboardDTO> graphValues;
	
	private List<DashboardDTO> tableValues;

}
