package com.te.flinko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebContactDto {
private String companyName;
private String employeeName;
private Long mobileNo;
private String message;
private String email;

}
