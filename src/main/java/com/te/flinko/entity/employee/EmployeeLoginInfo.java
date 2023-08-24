package com.te.flinko.entity.employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.type.TypeReference;
import com.te.flinko.audit.Audit;
import com.te.flinko.beancopy.BeanCopy;
import com.te.flinko.dto.employee.EmployeeCapabilityDTO;
import com.te.flinko.util.JsonToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fa_employee_login_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "loginId")
public class EmployeeLoginInfo extends Audit implements Serializable, UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eli_login_id", unique = true, nullable = false, precision = 19)
	private Long loginId;
	@Column(name = "eli_employee_id", unique = true, nullable = false, length = 25)
	private String employeeId;
	@Column(name = "eli_old_password", length = 50)
	private String oldPassword;
	@Column(name = "eli_current_password", nullable = false, length = 50)
	private String currentPassword;
	@Convert(converter = JsonToStringConverter.class)
	@Column(name = "eli_roles", columnDefinition = "TEXT")
	private Object roles;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "eli_employee_info_id")
	private EmployeePersonalInfo employeePersonalInfo;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<String> arrayList = new ArrayList<>();
		if (roles != null && !roles.equals("{}")) {
			List<EmployeeCapabilityDTO> employeeCapabilityDTOs = BeanCopy.objectProperties(roles,
					new TypeReference<List<EmployeeCapabilityDTO>>() {
					});
			getRoles(employeeCapabilityDTOs, arrayList);
		}
		return arrayList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return getCurrentPassword();
	}

	@Override
	public String getUsername() {
		return getEmployeeId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void getRoles(List<EmployeeCapabilityDTO> employeeCapabilityDTOs, List<String> rol) {

		for (EmployeeCapabilityDTO employeeCapabilityDTO : employeeCapabilityDTOs) {
			if (employeeCapabilityDTO.getChildCapabilityNameList() != null
					&& !employeeCapabilityDTO.getChildCapabilityNameList().isEmpty()) {
				getRoles(employeeCapabilityDTO.getChildCapabilityNameList(), rol);
			}
			if (employeeCapabilityDTO.getIsEnable().booleanValue()) {
				rol.add(employeeCapabilityDTO.getCapabilityType());
			}
		}
	}
}
