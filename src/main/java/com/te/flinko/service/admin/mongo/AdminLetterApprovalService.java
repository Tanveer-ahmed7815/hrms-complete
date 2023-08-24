package com.te.flinko.service.admin.mongo;

import java.util.List;

import com.te.flinko.dto.hr.mongo.EmployeeLetterBasicDTO;
import com.te.flinko.dto.hr.mongo.EmployeeLetterDTO;

public interface AdminLetterApprovalService {
	
	List<EmployeeLetterBasicDTO> getLetters(Long companyId, String status);
	
	EmployeeLetterDTO getLettersById(EmployeeLetterBasicDTO employeeLetterBasicDTO);
	
	Boolean updateStatus(EmployeeLetterDTO employeeLetterDTO, String status);

}
