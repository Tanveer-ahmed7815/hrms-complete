package com.te.flinko.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatWorkOrderDealDropdownDto {
	private Long clientId;
	private String deal;
}
