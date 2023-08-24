package com.te.flinko.dto.sales;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class EventDTO implements Serializable{
	
	private String eventName;
	private LocalDate eventDate;
	private LocalTime eventStartTime;
	private LocalTime eventEndTime;
	private String photoUrl;

}
