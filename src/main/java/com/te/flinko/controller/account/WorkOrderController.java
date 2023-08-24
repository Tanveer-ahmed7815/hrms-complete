package com.te.flinko.controller.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.CreatWorkOrderDealDropdownDto;
import com.te.flinko.dto.account.WorkOrderDTO;
import com.te.flinko.dto.account.WorkOrderListDto;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountDepartmentService;
import com.te.flinko.service.account.WorkOrderService;

@RestController
@RequestMapping("/api/v1/account/workorder")
@CrossOrigin(origins = "https://hrms.flinko.app")
public class WorkOrderController extends BaseConfigController {

	@Autowired
	WorkOrderService orderService;
	@Autowired
	AccountDepartmentService accountDepartmentService;

	@PostMapping("/")
	public ResponseEntity<SuccessResponse> createWorkOrder(@RequestBody WorkOrderDTO workOrderDTO) {
		WorkOrderDTO createWorkOrder = accountDepartmentService.createWorkOrder(workOrderDTO, getCompanyId());
		if (createWorkOrder == null)
			return ResponseEntity.ok()
					.body(SuccessResponse.builder().error(false).message("Work Order is not created").build());
		return ResponseEntity.ok().body(SuccessResponse.builder().error(false)
				.message("Work Order Successfully created").data(createWorkOrder).build());
	}

	@GetMapping("workorderlist")
	public ResponseEntity<SuccessResponse> getWorkOrderList() {
		List<WorkOrderListDto> workOrderList = orderService.getWorkOrderList(getCompanyId());

		if (!workOrderList.isEmpty())
			return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
					.message("Sucessfully Get WorkOrderList").data(workOrderList).build());

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.TRUE).message("WorkOrderList Not Found").build());
	}

	@GetMapping("createworkorder/deal/dropdown")
	public ResponseEntity<SuccessResponse> getCreateWorkOrderDeal() {
		List<CreatWorkOrderDealDropdownDto> companyDeals = orderService.getCompanyDeals(getCompanyId());

		if (!companyDeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
					.message("Sucessfully Get Deals").data(companyDeals).build());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(SuccessResponse.builder().error(Boolean.TRUE).message("Deals Not Found").build());
	}

	@GetMapping("/details/{companyId}/{workOrderId}")
	public ResponseEntity<SuccessResponse> getWorkOrderDetails(@PathVariable Long companyId,@PathVariable Long workOrderId) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(false).message("Details of work order")
						.data(orderService.getWorkOrderDetails(companyId, workOrderId,getUserId())).build());
	}

}
