package com.te.flinko.dto.helpandsupport.mongo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = Include.NON_DEFAULT)
@Data
public class ITProductNameDTO {

	private List<ProductNameDTO> distinctProductName;
	private List<ProductNameDTO> productName;
}
