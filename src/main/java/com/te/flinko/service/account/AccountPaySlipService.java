package com.te.flinko.service.account;

import java.util.List;

import com.te.flinko.dto.account.AccountPaySlipInputDTO;
import com.te.flinko.dto.account.AccountPaySlipListDTO;

public interface AccountPaySlipService {

	List<AccountPaySlipListDTO> paySlip(AccountPaySlipInputDTO accountPaySlipInputDTO);


}
