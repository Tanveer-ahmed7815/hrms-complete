package com.te.flinko.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.USE_DEFAULTS)
public class EmployeePunctualPerformanceDTO implements Serializable {
	private String label;
	private String addressDetails;
	private String employeeId;
	private String companyName;
	private String companyUrl;
	private Long pincode;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate date;
	private String employeeName;
	private String reportingManager;
	private String reportingHR;
	private String department;
	private String designation;
	private Integer noOfAbs;
	private BigDecimal noOfLeaveInMonth;
	private BigDecimal unapprovedLeave;
	private List<LocalDate> holidays;
	private List<LocalDate> absdays;
	private List<LocalDate> leaveDetails;
	
	public List<LocalDate> getHolidays() {
		return holidays==null?List.of():holidays;
	}
	public void setHolidays(List<LocalDate> holidays) {
		this.holidays = holidays;
	}
	public List<LocalDate> getAbsdays() {
		return absdays==null?List.of():absdays;
	}
	public void setAbsdays(List<LocalDate> absdays) {
		this.absdays = absdays;
	}
	public List<LocalDate> getLeaveDetails() {
		return leaveDetails==null?List.of():leaveDetails;
	}
	public void setLeaveDetails(List<LocalDate> leaveDetails) {
		this.leaveDetails = leaveDetails;
	}
	public Integer getNoOfAbs() {
		return noOfAbs==null?0:noOfAbs;
	}
	public void setNoOfAbs(Integer noOfAbs) {
		this.noOfAbs = noOfAbs;
	}
	public BigDecimal getNoOfLeaveInMonth() {
		return noOfLeaveInMonth==null?BigDecimal.ZERO:noOfLeaveInMonth;
	}
	public void setNoOfLeaveInMonth(BigDecimal noOfLeaveInMonth) {
		this.noOfLeaveInMonth = noOfLeaveInMonth;
	}
	public BigDecimal getUnapprovedLeave() {
		return unapprovedLeave==null?BigDecimal.ZERO:unapprovedLeave;
	}
	public void setUnapprovedLeave(BigDecimal unapprovedLeave) {
		this.unapprovedLeave = unapprovedLeave;
	}
}
