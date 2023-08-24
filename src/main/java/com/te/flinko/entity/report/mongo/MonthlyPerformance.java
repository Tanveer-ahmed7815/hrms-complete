package com.te.flinko.entity.report.mongo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyPerformance implements Serializable {

	@Field("epd_punctual")
	private BigDecimal punctual;

	@Field("epd_leave")
	private BigDecimal leaves;

	@Field("epd_target_achive")
	private BigDecimal targetAchived;

	@Field("epd_activities")
	private BigDecimal activities;

	@Field("epd_tickets")
	private BigDecimal tickets;

	@Field("epd_project_details")
	private List<ProjectTargetPerformance> projectDetails;

	public BigDecimal getPunctual() {
		return punctual == null ? BigDecimal.ZERO : punctual;
	}

	public BigDecimal getLeaves() {
		return leaves == null ? BigDecimal.ZERO : leaves;
	}

	public BigDecimal getTargetAchived() {
		return targetAchived == null ? BigDecimal.ZERO : targetAchived;
	}

	public BigDecimal getActivities() {
		return activities == null ? BigDecimal.ZERO : activities;
	}
}
