package com.te.flinko.dto.sales;

import java.io.Serializable;

import lombok.Data;

@Data
public class ClientContactPersonDetailsDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Long contactPersonId;
	private String firstName;
	private String lastName;
	private String designation;
	private String emailId;
	private String mobileNumber;
}
