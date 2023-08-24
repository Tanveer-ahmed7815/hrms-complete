package com.te.flinko.entity.employee;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.te.flinko.audit.Audit;
import com.te.flinko.util.MapOfListToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fa_employee_extra_work_details")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "extraWorkId")
public class EmployeeExtraWorkDetails extends Audit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eew_extra_work_id", unique = true, nullable = false, precision = 19)
	private Long extraWorkId;

	@Column(name = "eew_date")
	private LocalDate date;

	@Column(name = "eew_login_time")
	@JsonFormat(shape = Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime loginTime;

	@Column(name = "eew_logout_time")
	@JsonFormat(shape = Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime logoutTime;

	@Column(name = "eew_break_duration")
	private Integer breakDuration;

	@Column(name = "eew_project_task_details")
	@Convert(converter = MapOfListToStringConverter.class)
	private Map<String, List<String>> projectTaskDetails;

	@Column(name = "eew_status")
	private String status;
	
	@Column(name = "eew_rejection_reason")
	private String rejectionReason;

	@ManyToOne
	@JoinColumn(name = "eew_employee_info_id")
	private EmployeePersonalInfo employeePersonalInfo;

}
