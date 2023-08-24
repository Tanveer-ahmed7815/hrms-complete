package com.te.flinko.entity.employee.mongo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDetailsDTO implements Serializable {
	@JsonFormat(shape = Shape.STRING,pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime punchIn;
	
	@JsonFormat(shape = Shape.STRING,pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime punchOut;
}
