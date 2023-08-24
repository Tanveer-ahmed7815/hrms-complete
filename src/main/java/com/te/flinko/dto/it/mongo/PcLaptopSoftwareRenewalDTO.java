package com.te.flinko.dto.it.mongo;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_DEFAULT)
public class PcLaptopSoftwareRenewalDTO implements Serializable {

	private Long softwareDetailsId;
	private LocalDate expirationDate;
	private LocalDate notificationDate;
	private Boolean isRenewed;
	private String serialNumber;

}
