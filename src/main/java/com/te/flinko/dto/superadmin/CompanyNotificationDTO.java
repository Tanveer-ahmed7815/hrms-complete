package com.te.flinko.dto.superadmin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyNotificationDTO {
	private Long companyNotificationId;
	private String description;
	private Boolean isSeen;
}
