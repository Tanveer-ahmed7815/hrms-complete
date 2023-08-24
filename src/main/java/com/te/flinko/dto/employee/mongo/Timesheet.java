package com.te.flinko.dto.employee.mongo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.te.flinko.util.MapOfListToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timesheet implements Serializable{
	

	private String id;
	
	@JsonFormat(shape = Shape.STRING,pattern = "MM-dd-yyyy",timezone = "Asia/Kolkata")
    private LocalDate date;
    
    @Field("login_time")
    @JsonFormat(shape = Shape.STRING,pattern = "HH:mm:ss" ,timezone = "Asia/Kolkata" )
    private LocalTime loginTime;
    
    @Field("logout_time")
    @JsonFormat(shape = Shape.STRING,pattern = "HH:mm:ss",timezone = "Asia/Kolkata")
    private LocalTime logoutTime;
    
    @Field("break_duration")
    private int breakDuration;
        
    @Field("ets_project_task_details")
	@Convert(converter = MapOfListToStringConverter.class)
	private Map<String, List<String>> projectTaskDetails;
       
}