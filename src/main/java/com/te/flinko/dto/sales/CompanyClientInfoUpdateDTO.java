package com.te.flinko.dto.sales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;


@Data
public class CompanyClientInfoUpdateDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long clientId;
	private String clientName;
	
	private Long contactPersonId;
	private String firstName;
	private String lastName;
	private String designation;
	private String emailId;
	private String mobileNumber;
	
	//private String telephoneNumber;
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
	
	private String description;
	
	private String logoURL;
	
//	private Long clientAddressId;
//	
//	private String addressDetails;
//	
//	private String city;
//	
//	private String state;
//	
//	private String country;
//	
//	private String pinCode;
//	
//	private String addressType;
	
	private List<ClientContactPersonDetailsDTO> clientContactPersonDetailsList;
	
	private List<CompanyClientAddressDTO> companyClientAddressList;

}
