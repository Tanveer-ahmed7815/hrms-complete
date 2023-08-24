package com.te.flinko.dto.sales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CompanyClientInfoResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;
	 
	private String clientName;
	
	private String emailId;
	
	private String mobileNumber;
	
	private String leadOwnerName;

	private Long fax;

	private String websiteUrl;

	private String typeOfIndustry;

	private Integer noOfEmp;

	private BigDecimal annualRevenue;

	private String secondaryEmailId;

	private String twitter;

	private String skypeId;

	private String leadSource;

	private Long leadCategoryId;
	private String leadStatus;

	private String description;

	private String logoURL;

	private List<ClientContactPersonDetailsDTO> clientContactPersonDetailsList;

	private List<CompanyClientAddressDTO> companyClientAddressList;
	
	private List<String> attachments;
	
	private List<ClientProjectDTO> clientProjectList;

}
