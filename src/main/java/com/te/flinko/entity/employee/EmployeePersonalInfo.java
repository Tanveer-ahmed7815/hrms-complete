package com.te.flinko.entity.employee;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.te.flinko.audit.Audit;
import com.te.flinko.entity.account.CompanyWorkOrder;
import com.te.flinko.entity.admin.CompanyInfo;
import com.te.flinko.entity.hr.CandidateInfo;
import com.te.flinko.entity.hr.CandidateInterviewInfo;
import com.te.flinko.entity.it.CompanyHardwareItems;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.util.ListToStringConverter;
import com.te.flinko.util.MapToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fa_employee_personal_info")
@Getter
@Setter
//@ToString
@AllArgsConstructor
@NoArgsConstructor

@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "employeeInfoId")
public class EmployeePersonalInfo extends Audit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "epi_employee_info_id", unique = true, nullable = false, precision = 19)
	private Long employeeInfoId; 
	
	@Column(name = "epi_first_name", nullable = false, length = 50)
	private String firstName;
	
	@Column(name = "epi_last_name", nullable = false, length = 25)
	private String lastName;
	
	@Column(name = "epi_email_id", unique = true, length = 100)
	private String emailId;
	
	@Column(name = "epi_mobile_number", precision = 19)
	private Long mobileNumber;
	
	@Column(name = "epi_pan", length = 15)
	private String pan;
	
	@Column(name = "epi_gender", length = 10)
	private String gender;
	
	@Column(name = "epi_dob")
	private LocalDate dob;
	
	@Column(name = "epi_blood_group", length = 15)
	private String bloodGroup;	
	
	@Column(name = "epi_guardians_name", length = 100)
	private String guardiansName;
	
	@Column(name = "epi_marital_status", length = 15)
	private String maritalStatus;
	
	@Column(name = "epi_language", length = 100)
	@Convert(converter = ListToStringConverter.class)
	private List<String> language;
	
	@Column(name = "epi_linkedin_id", length = 100)
	private String linkedinId;
	
	@Column(name = "epi_passport_number" ,length=15)
	private String passportNumber;

	@Column(name = "epi_passport_expiry_date")
	private LocalDate passportExpiryDate;
	
	@Column(name = "epi_is_active", precision = 3)
	private Boolean isActive;
	
	@Column(name = "epi_status", length = 999)
	@Convert(converter = MapToStringConverter.class)
	private Map<String, String> status;
	
	@Column(name = "epi_expo_token")
	private String expoToken;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<CandidateInterviewInfo> candidateInterviewInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInterviewInfo")
	private List<CandidateInterviewInfo> employeeInterviewInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<CompanyEmployeeResignationDetails> companyEmployeeResignationDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<CompanyHardwareItems> companyHardwareItemsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<CompanyPcLaptopDetails> companyPcLaptopDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<CompanyWorkOrder> companyWorkOrderList;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "fa_discussion_attendees_details", joinColumns = { @JoinColumn(name = "dad_attendees_id") }, inverseJoinColumns = {
			@JoinColumn(name = "dad_duscussion_id") })
	private List<EmployeeResignationDiscussion> employeeResignationDiscussionList;
	
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name = "fa_meeting_attendees_details", joinColumns = { @JoinColumn(name = "ma_attendees_id") }, inverseJoinColumns = {
			@JoinColumn(name = "ma_meeting_id") })
	private List<ApprisalMeetingInfo> apprisalMeetingInfoList;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "fa_employee_project_details", joinColumns = { @JoinColumn(name = "cpd_employee_info_id") }, inverseJoinColumns = {
			@JoinColumn(name = "cpd_project_id") })
	private List<ProjectDetails> allocatedProjectList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeAddressInfo> employeeAddressInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeAdvanceSalary> employeeAdvanceSalaryList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeBankInfo> employeeBankInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeDependentInfo> employeeDependentInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeEducationInfo> employeeEducationInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeEmploymentInfo> employeeEmploymentInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeIdCardInfo> employeeIdCardInfoList;
	
	@OneToMany(fetch = FetchType.LAZY,  cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeLeaveApplied> employeeLeaveAppliedList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "senderEmployeePersonalInfo")
	private List<EmployeeNotification> receiverEmployeeNotificationList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "receiverEmployeePersonalInfo")
	private List<EmployeeNotification> senderEmployeeNotificationList;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade =  CascadeType.ALL)
	@JoinColumn(name = "epi_candidate_id")
	private CandidateInfo candidateInfo;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade =  CascadeType.ALL)
	@JoinColumn(name = "epi_company_id")
	private CompanyInfo companyInfo;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "epi_official_id")
	private EmployeeOfficialInfo employeeOfficialInfo;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "refferalEmployeePersonalInfo")
	private List<EmployeeReferenceInfo> employeeReferenceInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeReimbursementInfo> employeeReimbursementInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeReportingInfo> employeeInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "reportingManager")
	private List<EmployeeReportingInfo> employeeReportingInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "reportingHR")
	private List<EmployeeReportingInfo> employeeReportingInfoAsHRList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeVisaInfo> employeeVisaInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "projectManager")
	private List<ProjectDetails> projectManagerProjectList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "reportingManager")
	private List<ProjectDetails> reportingManagerProjectList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeSalaryDetails> employeeSalaryDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeTerminationDetails> employeeTerminationDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeReviseSalary> employeeReviseSalaryList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employeePersonalInfo")
	private List<EmployeeExtraWorkDetails> employeeExtraWorkDetailsList;

}
