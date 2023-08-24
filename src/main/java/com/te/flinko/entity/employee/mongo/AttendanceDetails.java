package com.te.flinko.entity.employee.mongo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Convert;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.te.flinko.util.MapToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDetails implements Serializable {
	
	@Field("ld_details_id")
	private Integer detailsId;
	
	@Field("ld_punch_in")
	@JsonFormat(shape = Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss a",timezone = "Asia/Kolkata")
	private LocalDateTime punchIn;

	@Field("ld_punch_out")
	@JsonFormat(shape = Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss a", timezone = "Asia/Kolkata")
	private LocalDateTime punchOut;
	
	@Field("ld_inside_location")
	private Boolean isInsideLocation;
	
	@Field("ld_status")
	@Convert(converter = MapToStringConverter.class)
	private Map<String, String> status;
}
