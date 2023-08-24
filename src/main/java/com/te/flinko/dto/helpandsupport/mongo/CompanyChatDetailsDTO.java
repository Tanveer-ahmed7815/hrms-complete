package com.te.flinko.dto.helpandsupport.mongo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
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
public class CompanyChatDetailsDTO implements Serializable {
	private String employeeId;
	private String contant;
	@JsonFormat(shape = Shape.STRING,pattern = "hh:mm a" , timezone = "Asia/Kolkata" )
	private LocalTime sendTime;
	@JsonFormat(shape = Shape.STRING,pattern = "dd-MM-YYYY" , timezone = "Asia/Kolkata")
	private LocalDate sendDate;
	public Boolean read;
	private String compnayId;
	private String senderEmployeeId;
	private String senderEmployeeInfoId;
	private String receiverEmployeeId;
	private String receiverEmployeeInfoId;
	private String room;
	
}

