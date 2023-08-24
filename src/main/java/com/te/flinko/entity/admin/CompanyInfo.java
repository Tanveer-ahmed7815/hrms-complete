package com.te.flinko.entity.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.te.flinko.audit.Audit;
import com.te.flinko.entity.account.CompanyCostEvaluation;
import com.te.flinko.entity.account.CompanyPurchaseInvoice;
import com.te.flinko.entity.account.CompanyPurchaseOrder;
import com.te.flinko.entity.account.CompanySalesInvoice;
import com.te.flinko.entity.account.CompanySalesOrder;
import com.te.flinko.entity.account.CompanyWorkOrder;
import com.te.flinko.entity.admindept.CompanySoftwareDetails;
import com.te.flinko.entity.admindept.CompanyStockGroupItems;
import com.te.flinko.entity.employee.CompanyEmployeeResignationDetails;
import com.te.flinko.entity.employee.EmployeePersonalInfo;
import com.te.flinko.entity.employee.EmployeeReviseSalary;
import com.te.flinko.entity.employee.EmployeeSalaryDetails;
import com.te.flinko.entity.employee.EmployeeTerminationDetails;
import com.te.flinko.entity.hr.CandidateInfo;
import com.te.flinko.entity.hr.CompanyEventDetails;
import com.te.flinko.entity.it.CompanyHardwareItems;
import com.te.flinko.entity.it.CompanyPcLaptopDetails;
import com.te.flinko.entity.project.ProjectDetails;
import com.te.flinko.entity.sales.CompanyClientInfo;
import com.te.flinko.entity.superadmin.CompanyNotification;
import com.te.flinko.entity.superadmin.PaymentDetails;
import com.te.flinko.util.MapOfMapToStringConverter;
import com.te.flinko.util.MapToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fa_company_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "companyId")
public class CompanyInfo extends Audit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ci_company_id", unique = true, nullable = false, precision = 19)
	private Long companyId;
	@Column(name = "ci_company_logo_url", length = 100)
	private String companyLogoUrl;
	@Column(name = "ci_company_name", nullable = false, length = 50)
	private String companyName;
	@Column(name = "ci_company_code", length = 50)
	private String companyCode;
	@Column(name = "ci_pan", precision = 19)
	private String pan;
	@Column(name = "ci_gstin", precision = 19)
	private String gstin;
	@Column(name = "ci_cin", precision = 19)
	private String cin;
	@Column(name = "ci_no_of_emp", precision = 19)
	private Long noOfEmp;
	@Column(name = "ci_email_id", length = 100)
	private String emailId;
	@Column(name = "ci_mobile_number", precision = 19)
	private Long mobileNumber;
	@Column(name = "ci_telephone_number", precision = 19)
	private Long telephoneNumber;
	@Column(name = "ci_type_of_industry", length = 50)
	private String typeOfIndustry;
	@Column(name = "ci_isActive", length = 50)
	private Boolean isActive;

	@Column(name = "ci_is_submited")
	private Boolean isSubmited;

	@Column(name = "ci_status", length = 999)
	@Convert(converter = MapToStringConverter.class)
	private Map<String, String> status;

	@Column(name = "ci_notification_status", length = 999)
	@Convert(converter = MapOfMapToStringConverter.class)
	private Map<String, Map<String, String>> notificationStatus;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CandidateInfo> candidateInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyBranchInfo> companyBranchInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyClientInfo> companyClientInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyCostEvaluation> companyCostEvaluationList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyDesignationInfo> companyDesignationInfoList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyEmployeeResignationDetails> companyEmployeeResignationDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyEventDetails> companyEventDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyExpenseCategories> companyExpenseCategoriesList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyHardwareItems> companyHardwareItemsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyHolidayDetails> companyHolidayDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyLeadCategories> companyLeadCategoriesList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyNotification> companyNotificationList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyPcLaptopDetails> companyPcLaptopDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyPurchaseInvoice> companyPurchaseInvoiceList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyPurchaseOrder> companyPurchaseOrderList;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private CompanyRuleInfo companyRuleInfo;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanySalesInvoice> companySalesInvoiceList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanySalesOrder> companySalesOrderList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanySoftwareDetails> companySoftwareDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyStockGroup> companyStockGroupList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyStockGroupItems> companyStockGroupItemsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyStockUnits> companyStockUnitsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyWorkOrder> companyWorkOrderList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyWorkWeekRule> companyWorkWeekRuleList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyPayrollInfo> companyPayrollInfoList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<EmployeePersonalInfo> employeePersonalInfoList;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private LevelsOfApproval levelsOfApproval;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<PaymentDetails> paymentDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<ProjectDetails> projectDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<EmployeeSalaryDetails> employeeSalaryDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<EmployeeTerminationDetails> employeeTerminationDetailsList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<EmployeeReviseSalary> employeeReviseSalaryList;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "companyInfo")
	private List<CompanyTermsAndConditions> companyTermsAndConditionsList;

	@PrePersist
	public void setIsSubmitted() {
		isSubmited = isSubmited == null ? Boolean.FALSE : isSubmited.booleanValue();
	}

}
