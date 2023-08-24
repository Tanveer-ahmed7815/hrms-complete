package com.te.flinko.controller.account;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.dto.account.*;
import com.te.flinko.dto.admin.TermsAndConditionDTO;
import com.te.flinko.response.SuccessResponse;
import com.te.flinko.service.account.AccountDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.te.flinko.common.account.AccountDepartmentConstants.*;
import static com.te.flinko.common.project.ProjectManagementConstants.ADD_UPDATE_BILLING_SHIPPING_ADDRESS;

@CrossOrigin(origins = "https://hrms.flinko.app")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/account")
@RestController
public class AccountDepartmentController extends BaseConfigController {

	private final AccountDepartmentService accountDepartmentService;

	@PostMapping(path = "/save-purchase-order")
	public ResponseEntity<SuccessResponse> createUpdatePurchaseOrder(
			@RequestBody @Valid CompanyPurchaseOrderDTO companyPurchaseOrderDTO) {
		log.info("createUpdatePurchaseOrder method, execution start");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message(PURCHASE_ORDER_SAVED_UPDATED).data(
						accountDepartmentService.createUpdatePurchaseOrder(companyPurchaseOrderDTO, getCompanyId()))
						.build());
	}

	@PostMapping(path = "/save-sales-order")
	public ResponseEntity<SuccessResponse> createUpdateSalesOrder(
			@RequestBody @Valid CompanySalesOrderDTO companySalesOrderDTO) {
		log.info("createUpdateSalesOrder method, execution start");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message(SALES_ORDER_SAVED_UPDATED)
						.data(accountDepartmentService.createUpdateSalesOrder(companySalesOrderDTO, getCompanyId()))
						.build());
	}

	@PutMapping(path = "/add-purchase-address/{purchaseOrderId}")
	public ResponseEntity<SuccessResponse> addPurchaseBillingShippingAddress(
			@RequestBody @Valid BillingShippingAddressDTO billingShippingAddressDTO,
			@PathVariable Long purchaseOrderId) {
		return ResponseEntity
				.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
						.message(ADD_UPDATE_BILLING_SHIPPING_ADDRESS).data(accountDepartmentService
								.addPurchaseBillingShippingAddress(billingShippingAddressDTO, purchaseOrderId))
						.build());
	}

	@PutMapping(path = "/add-sales-address/{salesOrderId}")
	public ResponseEntity<SuccessResponse> addSalesBillingShippingAddress(
			@RequestBody @Valid BillingShippingAddressDTO billingShippingAddressDTO, @PathVariable Long salesOrderId) {
		return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.builder().error(Boolean.FALSE)
				.message(ADD_UPDATE_BILLING_SHIPPING_ADDRESS)
				.data(accountDepartmentService.addSalesBillingShippingAddress(billingShippingAddressDTO, salesOrderId))
				.build());
	}

	@PostMapping(path = "/add-purchase-items/{purchaseOrderId}")
	public ResponseEntity<SuccessResponse> addPurchasedItems(@RequestBody @Valid PurchaseItemsDTO purchaseItemsDTO,
			@PathVariable Long purchaseOrderId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message(ADD_UPDATE_PURCHASE_ITEMS)
						.data(accountDepartmentService.addPurchasedItems(purchaseItemsDTO, purchaseOrderId)).build());
	}

	@GetMapping("/purchase/{purcahseOrderId}")
	public ResponseEntity<SuccessResponse> getPurchaseOrderDetialsById(@PathVariable Long purcahseOrderId) {
		return ResponseEntity.ok(SuccessResponse.builder().error(false).message("PurchaseDetails Fethced")
				.data(accountDepartmentService.getPurchaseOrderDetailsById(purcahseOrderId)).build());

	}

	@PostMapping(path = "/add-ordered-items/{salesOrderId}")
	public ResponseEntity<SuccessResponse> addOrderedItems(@RequestBody @Valid SalesItemsDTO salesItemsDTO,
			@PathVariable Long salesOrderId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message(ADD_UPDATE_SALES_ITEMS)
						.data(accountDepartmentService.addOrderedItems(salesItemsDTO, salesOrderId)).build());
	}

	@GetMapping("/purchase")
	public ResponseEntity<SuccessResponse> getAllPurchaseOrder() {
		return ResponseEntity.ok(SuccessResponse.builder().error(Boolean.FALSE).message("Purchase Details List")
				.data(accountDepartmentService.getAllPurchaseOrder(getCompanyId())).build());
	}

	@GetMapping("/purchase-inventory")
	public ResponseEntity<SuccessResponse> getAllPurchaseInventory() {
		return ResponseEntity
				.ok(SuccessResponse.builder().error(Boolean.FALSE).message("Purchase Inventory Details List")
						.data(accountDepartmentService.getAllPurchaseInventory(getCompanyId())).build());
	}

	@GetMapping("/sales")
	public ResponseEntity<SuccessResponse> getAllSalesOrder() {
		return ResponseEntity.ok(SuccessResponse.builder().error(Boolean.FALSE).message("All Sales Details")
				.data(accountDepartmentService.getAllSalesDetails(getCompanyId())).build());

	}

	@GetMapping("/sales/{salesOrderId}")
	public ResponseEntity<SuccessResponse> getSalesOrderById(@PathVariable Long salesOrderId) {
		return ResponseEntity
				.ok(SuccessResponse.builder().error(Boolean.FALSE).message("Sale Details Fetched Successfully")
						.data(accountDepartmentService.getSalesDetailsById(salesOrderId)).build());
	}

	@PutMapping("/purchase-order/description")
	public ResponseEntity<SuccessResponse> updatePurchaseOrderDescription(
			@RequestBody AccountDescriptionDTO accountDescriptionDTO) {

		return ResponseEntity.ok(SuccessResponse.builder().error(Boolean.FALSE).message("Added decription")
				.data(accountDepartmentService.updateDescriptionOfPurchaseOrder(accountDescriptionDTO, getUserId()))
				.build());

	}

	@PutMapping("/sales-order/description")
	public ResponseEntity<SuccessResponse> updatesalesOrderDescription(
			@RequestBody AccountDescriptionDTO accountDescriptionDTO) {

		return ResponseEntity.ok(SuccessResponse.builder().error(Boolean.FALSE).message("Added decription")
				.data(accountDepartmentService.updateDescriptionOfSalesOrder(accountDescriptionDTO, getUserId()))
				.build());

	}

	@PostMapping(path = "/terms-and-condition")
	public ResponseEntity<SuccessResponse> getTermsAndConditionDetails(
			@RequestBody TermsAndConditionDTO termsAndConditionDTO) {
		log.info("getTermsAndConditionDetails method, execution start");
		return ResponseEntity.status(HttpStatus.OK)
				.body(SuccessResponse.builder().error(Boolean.FALSE).message("Terms and Conditions Fetched Successfully").data(
						accountDepartmentService.getTermsAndConditionDetails(getCompanyId(), termsAndConditionDTO))
						.build());
	}

}
