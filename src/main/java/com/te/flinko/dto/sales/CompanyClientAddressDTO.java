package com.te.flinko.dto.sales;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Data;

@Data

public class CompanyClientAddressDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long clientAddressId;
	private String addressDetails;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String addressType;

}
