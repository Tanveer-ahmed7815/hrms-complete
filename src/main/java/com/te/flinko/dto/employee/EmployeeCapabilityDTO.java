package com.te.flinko.dto.employee;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(value = Include.NON_DEFAULT)
@JsonPropertyOrder(value = {"capabilityType","isEnable","childCapabilityNameList"})
public class EmployeeCapabilityDTO implements Serializable {
	private String capabilityType;
	private Boolean isEnable;
	private List<EmployeeCapabilityDTO> childCapabilityNameList;
}
