package com.te.flinko.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.te.flinko.entity.report.mongo.ProjectTargetPerformance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePerformanceDTO implements Serializable {
	private String label;
	private String addressDetails;
	private List< ProjectTargetPerformance> projectTargetPerformances;
	private String employeeId;
	private String companyName;
	private String companyUrl;
	private Long pincode;
	private String logoStar;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate date;
	private String employeeName;
	private String reportingManager;
	private String reportingHR;
	private String department;
	private String designation;
	private BigDecimal noOfLeaveInMonth;
	private BigDecimal unapprovedLeave;
	private BigDecimal lateLogin;
	private BigDecimal targetAchived;
	private BigDecimal achievedMoreThen50Per;
	private BigDecimal achievedMoreThen100Per;
	private BigDecimal leaves;
	private BigDecimal punctual;
	private BigDecimal activities;
	private BigDecimal ticketSolveRating;
	private Map<Long,BigDecimal> targetAchive;
	private String targetAchivedStar1;
	private String targetAchivedStar2;
	private String targetAchivedStar3;
	private String targetAchivedStar4;
	private String targetAchivedStar5;
	private String activitiesStar1;
	private String activitiesStar2;
	private String activitiesStar3;
	private String activitiesStar4;
	private String activitiesStar5;
	private String leavesStar1;
	private String leavesStar2;
	private String leavesStar3;
	private String leavesStar4;
	private String leavesStar5;
	private String punctualStar1;
	private String punctualStar2;
	private String punctualStar3;
	private String punctualStar4;
	private String punctualStar5;
	public BigDecimal getUnapprovedLeave() {
		return unapprovedLeave==null?BigDecimal.ZERO:unapprovedLeave;
	}
	public void setUnapprovedLeave(BigDecimal unapprovedLeave) {
		this.unapprovedLeave = unapprovedLeave;
	}
	public BigDecimal getNoOfLeaveInMonth() {
		return noOfLeaveInMonth==null?BigDecimal.ZERO:noOfLeaveInMonth;
	}
	public void setNoOfLeaveInMonth(BigDecimal noOfLeaveInMonth) {
		this.noOfLeaveInMonth = noOfLeaveInMonth;
	}
	public BigDecimal getLateLogin() {
		return lateLogin==null?BigDecimal.ZERO:lateLogin;
	}
	public void setLateLogin(BigDecimal lateLogin) {
		this.lateLogin = lateLogin;
	}
	public BigDecimal getTargetAchived() {
		return targetAchived==null?BigDecimal.ZERO:targetAchived;
	}
	public void setTargetAchived(BigDecimal targetAchived) {
		this.targetAchived = targetAchived;
	}
	public BigDecimal getAchievedMoreThen50Per() {
		return achievedMoreThen50Per==null?BigDecimal.ZERO:achievedMoreThen50Per;
	}
	public void setAchievedMoreThen50Per(BigDecimal achievedMoreThen50Per) {
		this.achievedMoreThen50Per = achievedMoreThen50Per;
	}
	public BigDecimal getAchievedMoreThen100Per() {
		return achievedMoreThen100Per==null?BigDecimal.ZERO:achievedMoreThen100Per;
	}
	public void setAchievedMoreThen100Per(BigDecimal achievedMoreThen100Per) {
		this.achievedMoreThen100Per = achievedMoreThen100Per;
	}
	public BigDecimal getLeaves() {
		return leaves==null?BigDecimal.ZERO:leaves;
	}
	public void setLeaves(BigDecimal leaves) {
		this.leaves = leaves;
	}
	public BigDecimal getPunctual() {
		return punctual==null?BigDecimal.ZERO:punctual;
	}
	public void setPunctual(BigDecimal punctual) {
		this.punctual = punctual;
	}
	public BigDecimal getActivities() {
		return activities==null?BigDecimal.ZERO:activities;
	}
	public void setActivities(BigDecimal activities) {
		this.activities = activities;
	}
	
	
}
