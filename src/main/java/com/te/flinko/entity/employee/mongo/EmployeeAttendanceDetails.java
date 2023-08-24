package com.te.flinko.entity.employee.mongo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.te.flinko.dto.employee.mongo.EmployeeCalendarDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("fa_login_details")
public class EmployeeAttendanceDetails implements Serializable {

	@Id
	private String attendanceObjectId;
	
	@Field("ld_employee_info_id")
	private Long employeeInfoId;
	
	@Field("ld_month_no")
	private Integer monthNo;
	
	@Field("ld_year")
	private Integer year;
	
	private EmployeeCalendarDTO employeeCalendarDTO;
	
	@Field("ld_attendance_details")
	private List<AttendanceDetails> attendanceDetails;
	
	@Field("ld_company_id")
	private Long companyId;
	

}
