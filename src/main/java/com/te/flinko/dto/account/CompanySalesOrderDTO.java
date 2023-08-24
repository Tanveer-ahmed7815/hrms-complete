package com.te.flinko.dto.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Max;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompanySalesOrderDTO {
    private Long salesOrderId; // This id will be set and used while updating the data
    private String salesOrderOwner;
    private Long companyClientInfoID; // private String dealName;
    private ProductType productType;
    private Long stockGroupId;
    private String subject;
    private String purchaseOrder;
    private Long customerNumber;
    private LocalDate dueDate;
    private Long clientContactPersonID; // private String contactName;
    private String pending;
    private String exciseDuty;
    private String carrier;
    private String status;
    @Max(message = "Sales Commission accepts 10 digits 2 decimals only",value = (long) 99999999.99)
    private BigDecimal salesCommission;
}
