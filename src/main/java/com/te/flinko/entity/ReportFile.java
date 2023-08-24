package com.te.flinko.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.te.flinko.audit.Audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@Document("fa_report_file")
@AllArgsConstructor
@NoArgsConstructor
public class ReportFile extends Audit implements Serializable{
	
	@Id
	private String reportObjectId;
	
	@Field("rf_report_id")
	private Long reportId;
	
	@Field("rf_report_name")
	private String reportName;
	
	@Field("rf_is_sql_conn")
	private Boolean isSqlConn;
}
