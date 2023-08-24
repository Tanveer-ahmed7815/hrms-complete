package com.te.flinko.dto.sales;

import java.io.Serializable;

import lombok.Data;

@Data
public class AllCompanyClientInfoResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Long clientId;
	private String clientName;
	private String emailId;
	private String websiteUrl;
	private Long leadCategoryId;
	private String leadStatus;
	private String color;
	private String ownerName;
}
