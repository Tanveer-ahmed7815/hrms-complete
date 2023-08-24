package com.te.flinko.entity.report.mongo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProjectTargetPerformance implements Serializable {
	private Long projectId;
	private Double targetPerProject;
}
