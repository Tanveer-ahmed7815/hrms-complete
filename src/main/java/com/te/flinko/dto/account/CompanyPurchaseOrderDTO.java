package com.te.flinko.dto.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Max;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompanyPurchaseOrderDTO {
    private Long purchaseOrderId;
    private String purchaseOrderOwner;
    private String purchaseOrderNumber;
    private ProductType productType;
    private Long stockGroupId;
    private String stockGroupName;
    private String subject;
    private String vendorId;
    private String contactName;
    private LocalDate purchaseOrderDate;
    private String requisitionNumber;
    private String trackingNumber;
    private LocalDate dueDate;
    private String carrier;
    private String exciseDuty;
    @Max(message = "Sales Commission accepts 8 digits 2 decimals only",value = (long) 99999999.99)
    private BigDecimal salesCommission;
    private String status;
    private String description;
    private String adjustment;
    private String purchaseType;
    private List<AddressInformationDTO> addressInformationDTO;
    private List<PurchaseItemDTO> purchaseOrderItemsDTO;
    
    //sales field
    private Long customerNumber;
    private Long clientId;
    private String clientName;
    private Long contactId;
    private String pending;
    private String vendorName;
    
    
}
