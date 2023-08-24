package com.te.flinko.dto.account;

import java.math.BigDecimal;
import java.util.List;

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
public class WorkOrderListDto {

	private Long workOrderId;
	private String workTitle;
	private String deals;
	private String departmentName;
	private String requestedTo;
	private String priority;
	private BigDecimal cost;
	private Long noOfEmployee;
	private String status;
	private String workOrderOwner;
	private List<WorkOrderResourcesDTO> workOrderResourcesDTO;
	private Boolean isEstimated;

}
