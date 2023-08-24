package com.te.flinko.entity.admin;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.te.flinko.audit.Audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fa_company_terms_and_condition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "termsAndConditionId")
public class CompanyTermsAndConditions extends Audit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ctac_terms_and_condition_id", unique = true, nullable = false, precision = 19)
	private Long termsAndConditionId;
	@Column(name = "ctac_type", length = 25)
	private String type;
	@Column(name = "ctac_description", length = 999, columnDefinition = "TEXT")
	private String description;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ctac_company_id")
	private CompanyInfo companyInfo;
}
