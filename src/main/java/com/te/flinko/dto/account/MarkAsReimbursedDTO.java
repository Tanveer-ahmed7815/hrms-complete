package com.te.flinko.dto.account;

import java.util.ArrayList;

import lombok.Data;

@Data
public class MarkAsReimbursedDTO {
	private ArrayList<Long> reimbursementIdList;

}
